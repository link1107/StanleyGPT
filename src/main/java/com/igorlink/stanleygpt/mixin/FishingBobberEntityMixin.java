package com.igorlink.stanleygpt.mixin;

import com.igorlink.stanleygpt.payloads.PlayerHookedItemWithFishingRodPayload;
import com.igorlink.stanleygpt.service.ItemStackDto;
import com.llamalad7.mixinextras.sugar.Local;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Mixin class for {@link FishingBobberEntity}.
 * This class is used to send a packet to the client when the fishing rod is used.
 */
@Slf4j
@Mixin(FishingBobberEntity.class)
public class FishingBobberEntityMixin {

    /**
     * This method is called when the fishing rod is used.
     *
     * @param usedItem the item that is used
     * @param cir the callback info
     * @param list the list of items
     * @param playerEntity the player who uses the item
     */
    @Inject(method = "use", at = @At(value = "CONSTANT", args = "intValue=1", ordinal = 0))
    private void onFishingRodHooked(ItemStack usedItem,
                                   CallbackInfoReturnable<Integer> cir,
                                   @Local List<ItemStack> list,
                                   @Local PlayerEntity playerEntity) {

        // Check if the player is a server player
        if (playerEntity instanceof ServerPlayerEntity player) {
            // Create a list of DTO objects from the ItemStacks from the loot
            List<ItemStackDto> loot = new ArrayList<>();
            for (ItemStack itemStack : list) {
                loot.add(new ItemStackDto(itemStack));
            }

            // Send the payload to the client
            ServerPlayNetworking.send(player,
                    new PlayerHookedItemWithFishingRodPayload(loot.toArray(ItemStackDto[]::new)));
        }
    }

}
