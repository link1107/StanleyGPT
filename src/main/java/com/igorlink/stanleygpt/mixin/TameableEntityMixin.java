package com.igorlink.stanleygpt.mixin;

import com.igorlink.stanleygpt.payloads.PlayerTamedEntityPayload;
import com.igorlink.stanleygpt.service.LivingEntityDto;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin class for {@link TameableEntity}.
 * This class is used to send a packet to the client when an entity is tamed.
 */
@Slf4j
@Mixin(TameableEntity.class)
public class TameableEntityMixin {

    /**
     * This method is called when the entity is tamed.
     *
     * @param player the player who tamed the entity
     * @param ci the callback info
     */
    @Inject(method = "setOwner", at = @At("TAIL"))
    public void onTamed(PlayerEntity player, CallbackInfo ci) {
        // Check if the player is a server player
        if (player instanceof ServerPlayerEntity serverPlayer) {
            // Send the payload to the client
            ServerPlayNetworking.send(
                    serverPlayer,
                    new PlayerTamedEntityPayload(
                            new LivingEntityDto((TameableEntity) (Object) this)
                    )
            );
        }
    }

}
