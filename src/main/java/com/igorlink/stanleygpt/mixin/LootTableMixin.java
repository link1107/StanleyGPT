package com.igorlink.stanleygpt.mixin;

import com.igorlink.stanleygpt.payloads.PlayerKilledEntityPayload;
import com.igorlink.stanleygpt.payloads.TamedEntityKilledLivingEntityPayload;
import com.igorlink.stanleygpt.payloads.TamedEntityKilledPayload;
import com.igorlink.stanleygpt.service.ItemStackDto;
import com.igorlink.stanleygpt.service.LivingEntityDto;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Mixin class for {@link LootTable}.
 * This class is used to send a packet to the client when an entity is killed.
 */
@Slf4j
@Mixin(LootTable.class)
public class LootTableMixin {

    @Unique
    private Consumer<ItemStack> wrapperConsumer;

    @Unique
    private List<ItemStack> itemStackList;

    @Unique
    private LivingEntity droppingEntity;

    @Unique
    private DamageSource damageSource;

    /**
     * This method is called when the loot is generated.
     *
     * @param lootTable        the loot table
     * @param context          the loot context
     * @param originalConsumer the original consumer
     */
    @Redirect(
            method = "generateLoot(Lnet/minecraft/loot/context/LootWorldContext;" +
                    "J" +
                    "Ljava/util/function/Consumer;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/loot/LootTable;generateUnprocessedLoot(" +
                            "Lnet/minecraft/loot/context/LootContext;" +
                            "Ljava/util/function/Consumer;)V"
            )
    )
    private void redirectGenerateUnprocessedLoot(
            LootTable lootTable,
            LootContext context,
            Consumer<ItemStack> originalConsumer
    ) {
        // Create a new list to store the dropped items
        this.itemStackList = new ArrayList<>();

        this.wrapperConsumer = (ItemStack stack) -> {
            // Add the dropped item to the list
            this.itemStackList.add(stack);
            // Call the original consumer
            originalConsumer.accept(stack);
        };

        // Store the entity and the damage source
        Entity entity = context.get(LootContextParameters.THIS_ENTITY);
        // Check if the entity is a living entity
        if (entity instanceof LivingEntity livingEntity) {
            this.droppingEntity = livingEntity;
        }
        // Store the damage source
        this.damageSource = context.get(LootContextParameters.DAMAGE_SOURCE);

        // Call the original method
        lootTable.generateUnprocessedLoot(context, this.wrapperConsumer);
    }


    /**
     * This method is called after the loot is generated.
     *
     * @param parameters       loot world context
     * @param seed             loot generation seed
     * @param originalConsumer original consumer
     * @param ci               callback info
     */
    @Inject(
            method = "generateLoot(Lnet/minecraft/loot/context/LootWorldContext;" +
                    "J" +
                    "Ljava/util/function/Consumer;)V",
            at = @At("TAIL")
    )
    private void onGenerateLootTail(LootWorldContext parameters, long seed, Consumer<ItemStack> originalConsumer, CallbackInfo ci) {
        try {
            // Check if the entity and the damage source are not null
            if (this.droppingEntity == null || this.damageSource == null
                    || this.wrapperConsumer == null || this.itemStackList == null) {
                return;
            }

            // Create a list of DTO objects from the ItemStacks from the loot
            List<ItemStack> loot = this.itemStackList;
            List<ItemStackDto> lootDto = new ArrayList<>();
            for (ItemStack stack : loot) {
                lootDto.add(new ItemStackDto(stack));
            }

            // Create DTO objects from the entity and the damage source
            LivingEntityDto killedEntityDto = new LivingEntityDto(this.droppingEntity);

            if (this.damageSource.getAttacker() instanceof ServerPlayerEntity player) {
                ServerPlayNetworking.send(
                        player,
                        new PlayerKilledEntityPayload(
                                killedEntityDto,
                                lootDto.toArray(ItemStackDto[]::new)));

            // Check if the entity is a tameable entity
            } else if (this.damageSource.getAttacker() instanceof TameableEntity tameableEntity) {
                // Check if the tamed entity has an owner
                if (tameableEntity.getOwner() == null) {
                    return;
                }

                // Create a DTO object from the tamed entity
                LivingEntityDto attackerEntityDto = new LivingEntityDto(tameableEntity);
                ServerPlayerEntity player = (ServerPlayerEntity) tameableEntity.getOwner();
                ServerPlayNetworking.send(
                        player,
                        new TamedEntityKilledLivingEntityPayload(
                                attackerEntityDto,
                                killedEntityDto,
                                lootDto.toArray(ItemStackDto[]::new)));

            // Check if tamed entity is killed by a living entity
            } else if (!(this.damageSource.getAttacker() instanceof ServerPlayerEntity)
                    && (this.droppingEntity instanceof TameableEntity tameable)
                    && (tameable.getOwner() instanceof ServerPlayerEntity player)) {

                LivingEntityDto attackerEntityDto = null;
                if (this.damageSource.getAttacker() instanceof LivingEntity livingEntity) {
                    attackerEntityDto = new LivingEntityDto(livingEntity);
                }

                // Send the event payload to the client
                ServerPlayNetworking.send(player,
                        new TamedEntityKilledPayload(killedEntityDto,
                                damageSource.getName(),
                                attackerEntityDto,
                                lootDto.toArray(ItemStackDto[]::new)));
            }
        } catch (Exception e) {
            log.error("Error in onGenerateLootTail", e);
        } finally {
            // Reset the fields
            this.droppingEntity = null;
            this.damageSource = null;
            this.wrapperConsumer = null;
            this.itemStackList = null;
        }
    }


}
