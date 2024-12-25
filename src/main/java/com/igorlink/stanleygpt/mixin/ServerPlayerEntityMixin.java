package com.igorlink.stanleygpt.mixin;

import com.igorlink.stanleygpt.payloads.PlayerDamagedPayload;
import com.igorlink.stanleygpt.payloads.PlayerDeathPayload;
import com.igorlink.stanleygpt.payloads.PlayerEatPayload;
import com.igorlink.stanleygpt.payloads.PlayerMovedToAnotherWorldPayload;
import com.igorlink.stanleygpt.service.ItemStackDto;
import com.igorlink.stanleygpt.service.LivingEntityDto;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.TeleportTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin class for {@link ServerPlayerEntity}.
 * This class is used to send a packet to the client when the player dies, consumes an item, teleports to another
 * location, or is damaged.
 */
@Slf4j
@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    /**
     * This method is called when the player dies.
     *
     * @param source the source of the damage
     * @param ci the callback info
     */
    @Inject(method = "onDeath", at = @At("TAIL"))
    public void onDeath(DamageSource source, CallbackInfo ci) {
        // Get the reason of death
        String deathReason = source.getName();

        // Get the attacker
        LivingEntityDto attacker = null;
        if (source.getAttacker() != null) {
            attacker = new LivingEntityDto((LivingEntity) source.getAttacker());
        }

        ServerPlayNetworking.send((ServerPlayerEntity) (Object) this,
                new PlayerDeathPayload(deathReason, attacker));
    }


    /**
     * This method is called when the player consumes an item.
     *
     * @param ci the callback info
     */
    @Inject(method = "consumeItem", at = @At("HEAD"))
    public void onConsumeItem(CallbackInfo ci) {
        // Get the server instance of a player
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        // Retrieve the item that the player is consuming
        ItemStack stack = player.getActiveItem();
        ItemStackDto itemStackDto = new ItemStackDto(stack);

        // Send the payload to the client
        ServerPlayNetworking.send(player,
                new PlayerEatPayload(itemStackDto));
    }


    /**
     * This method is called when the player teleports to another location.
     *
     * @param teleportTarget the target location
     * @param cir the callback info returnable
     */
    @Inject(method = "teleportTo*", at = @At("TAIL"))
    private void onTeleportTo(TeleportTarget teleportTarget, CallbackInfoReturnable<ServerPlayerEntity> cir) {
        // Check if the object is an instance of ServerPlayerEntity
        if ((Object) this instanceof ServerPlayerEntity player) {
            // Get the source and target worlds
            ServerWorld sourceWorld = (ServerWorld) player.getWorld();
            ServerWorld targetWorld = teleportTarget.world();

            // Get the names of the source and target worlds
            String sourceWorldName = sourceWorld.getRegistryKey().getValue().toString();
            String targetWorldName = targetWorld.getRegistryKey().getValue().toString();

            // Send the payload to the client
            ServerPlayNetworking.send(player,
                    new PlayerMovedToAnotherWorldPayload(sourceWorldName,
                            targetWorldName));
        }
    }


    /**
     * This method is called when the player is damaged.
     *
     * @param world world where the player is located
     * @param source the source of the damage
     * @param amount the amount of damage
     * @param cir the callback info returnable
     */
    @Inject(method = "damage", at = @At("TAIL"))
    public void onDamage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        // Get the name of the damage source and the amount of damage
        String damageSource = source.getName();
        float damageAmount = amount;

        // Get the attacker
        LivingEntityDto attacker = null;
        if (source.getAttacker() != null) {
            attacker = new LivingEntityDto((LivingEntity) source.getAttacker());
        }

        // Send the payload to the client
        ServerPlayNetworking.send((ServerPlayerEntity) (Object) this,
                new PlayerDamagedPayload(damageSource,
                        damageAmount,
                        attacker));
    }

}

