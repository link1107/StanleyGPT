package com.igorlink.stanleygpt.mixin;

import com.igorlink.stanleygpt.payloads.PlayerBreedEntityPayload;
import com.igorlink.stanleygpt.payloads.PlayerFeedEntityPayload;
import com.igorlink.stanleygpt.service.ItemStackDto;
import com.igorlink.stanleygpt.service.LivingEntityDto;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin class for {@link AnimalEntity}.
 * This class is used to send a packet to the client when the player interacts with the animal.
 */
@Slf4j
@Mixin(AnimalEntity.class)
public class AnimalMixin {

    /**
     * This method is called when the player interacts with the animal.
     *
     * @param player the player who interacts with the animal
     * @param hand the hand that the player uses to interact with the animal
     * @param cir the callback info returnable
     */
    @Inject(method = "interactMob", at = @At("TAIL"))
    public void onInteractMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        // Check if the player if this is a server environment
        if (player.getWorld().isClient) {
            return;
        }

        // Get the item that the player uses to interact with the animal and the animal itself
        ItemStack stack = player.getStackInHand(hand);
        AnimalEntity animal = (AnimalEntity) (Object) this;

        // Create DTO objects from the ItemStack and the AnimalEntity
        ItemStackDto food = new ItemStackDto(stack);
        LivingEntityDto entity = new LivingEntityDto(animal);

        // Send the payload to the client
        ServerPlayNetworking.send((ServerPlayerEntity) player,
                new PlayerFeedEntityPayload(
                        food,
                        entity
                )
        );
    }


    /**
     * This method is called when the animal breeds.
     *
     * @param world the world where the animal breeds
     * @param otherParent the other parent of the animal
     * @param ci the callback info
     */
    @Inject(method = "breed*", at = @At("HEAD"))
    private void onBreed(ServerWorld world, AnimalEntity otherParent, CallbackInfo ci) {
        // Retrieve the animal and the other parent
        AnimalEntity self = (AnimalEntity) (Object) this;

        // Retrieve the last child
        PassiveEntity lastChild = self.createChild(world, otherParent);
        if (lastChild == null) {
            return;
        }

        // Create DTO objects from the AnimalEntity objects
        LivingEntityDto childDto = new LivingEntityDto(lastChild);
        LivingEntityDto parent1Dto = new LivingEntityDto(self);
        LivingEntityDto parent2Dto = new LivingEntityDto(otherParent);

        for (ServerPlayerEntity player : world.getPlayers()) {
            // Player position
            Vec3d playerPos = player.getPos();

            // Distance between player and parent
            double distance = playerPos.distanceTo(otherParent.getPos());

            // If the player is within 15 blocks of the parent, send the payload
            if (distance <= 15) {

                ServerPlayNetworking.send(player,
                        new PlayerBreedEntityPayload(
                                childDto,
                                parent1Dto,
                                parent2Dto
                        )
                );
            }
        }
    }

}
