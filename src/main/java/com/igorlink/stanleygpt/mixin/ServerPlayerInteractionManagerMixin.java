package com.igorlink.stanleygpt.mixin;

import com.igorlink.stanleygpt.payloads.DropFromBlockPayload;
import com.igorlink.stanleygpt.service.ItemStackDto;
import com.llamalad7.mixinextras.sugar.Local;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin class for {@link ServerPlayerInteractionManager}.
 * This class is used to send a packet to the client when a block is broken without #afterBreak method triggering.
 */
@Slf4j
@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {

    @Shadow
    @Final
    protected ServerPlayerEntity player;

    /**
     * This method is called when a block is just to be broken.
     *
     * @param pos the position of the block
     * @param cir the callback info returnable
     * @param bl1 the first boolean
     * @param bl2 the second boolean
     * @param blockState the block state
     */
    @Inject(
            method = "tryBreakBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;postMine(Lnet/minecraft/world/World;" +
                            "Lnet/minecraft/block/BlockState;" +
                            "Lnet/minecraft/util/math/BlockPos;" +
                            "Lnet/minecraft/entity/player/PlayerEntity;)V"
            )
    )
    protected void onBreakCheck(BlockPos pos, CallbackInfoReturnable<Boolean> cir,
                                @Local(ordinal = 0) boolean bl1,
                                @Local(ordinal = 1) boolean bl2,
                                @Local(ordinal = 0) BlockState blockState) {
        // Check if the tool can harvest the block and if the block is broken
        if (!bl1 || !bl2) {
            ServerPlayNetworking.send(player,
                    new DropFromBlockPayload(
                            Registries.BLOCK.getRawId(blockState.getBlock()),
                            new ItemStackDto(player.getMainHandStack()),
                            new ItemStackDto[0]));
        }
    }

}

