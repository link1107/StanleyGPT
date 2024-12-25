package com.igorlink.stanleygpt.mixin;

import com.igorlink.stanleygpt.payloads.PlayerCraftedItemPayload;
import com.igorlink.stanleygpt.service.ItemStackDto;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin class for {@link CraftingResultSlot}.
 * This class is used to send a packet to the client when the player takes an item from the crafting result slot.
 */
@Slf4j
@Mixin(CraftingResultSlot.class)
public class CraftingResultSlotMixin {

    /**
     * This method is called when the player takes an item from the crafting result slot.
     *
     * @param player the player who takes the item
     * @param stack the item that the player takes
     * @param ci the callback info
     */
    @Inject(method = "onTakeItem", at = @At("HEAD"))
    private void onTakeItem(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            // We are tracking only cases when the player takes the first item from the crafting result slot
            if (player.currentScreenHandler.getCursorStack().getCount() > 1) {
                return;
            }

            // Create a DTO object from the ItemStack
            ItemStackDto itemStackDto = new ItemStackDto(stack);

            // Send the payload to the client
            ServerPlayNetworking.send(serverPlayer,
                    new PlayerCraftedItemPayload(
                            itemStackDto));
        }
    }

}
