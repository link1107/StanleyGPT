package com.igorlink.stanleygpt.mixin;

import com.igorlink.stanleygpt.payloads.PlayerTakeItemFromFurnacePayload;
import com.igorlink.stanleygpt.service.ItemStackDto;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.FurnaceOutputSlot;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin class for {@link FurnaceOutputSlot}.
 * This class is used to send a packet to the client when the player takes an item from the furnace output slot.
 */
@Slf4j
@Mixin(FurnaceOutputSlot.class)
public class FurnaceOutputSlotMixin {

    /**
     * This method is called when the player takes an item from the furnace output slot.
     *
     * @param player the player who takes the item
     * @param stack the item that the player takes
     * @param ci the callback info
     */
    @Inject(method = "onTakeItem", at = @At("TAIL"))
    private void onTakeItem(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        // Check if the player is a server player
        if (player instanceof ServerPlayerEntity serverPlayer) {
            ItemStackDto itemStackDto = new ItemStackDto(stack);

            // Send the payload to the client
            ServerPlayNetworking.send(
                    serverPlayer,
                    new PlayerTakeItemFromFurnacePayload(
                            itemStackDto)
            );
        }
    }

}
