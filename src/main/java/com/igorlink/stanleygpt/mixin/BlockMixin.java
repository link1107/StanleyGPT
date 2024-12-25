package com.igorlink.stanleygpt.mixin;

import com.igorlink.stanleygpt.payloads.DropFromBlockPayload;
import com.igorlink.stanleygpt.service.ItemStackDto;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

/**
 * Mixin class for {@link Block}.
 * This class is used to send a packet to the client when a block is broken.
 */
@Slf4j
@Mixin(Block.class)
public class BlockMixin {

    /**
     * This method is called when the block is broken.
     *
     * @param state the block state
     * @param world the world where the block is located
     * @param pos the position of the block
     * @param blockEntity the block entity
     * @param entity the entity that breaks the block
     * @param tool the tool that breaks the block
     * @return the list of dropped items
     */
    @Redirect(
            method = "dropStacks(Lnet/minecraft/block/BlockState;" +
                    "Lnet/minecraft/world/World;" +
                    "Lnet/minecraft/util/math/BlockPos;" +
                    "Lnet/minecraft/block/entity/BlockEntity;" +
                    "Lnet/minecraft/entity/Entity;" +
                    "Lnet/minecraft/item/ItemStack;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;getDroppedStacks(" +
                            "Lnet/minecraft/block/BlockState;" +
                            "Lnet/minecraft/server/world/ServerWorld;" +
                            "Lnet/minecraft/util/math/BlockPos;" +
                            "Lnet/minecraft/block/entity/BlockEntity;" +
                            "Lnet/minecraft/entity/Entity;" +
                            "Lnet/minecraft/item/ItemStack;)" +
                            "Ljava/util/List;"
            )
    )
    private static List<ItemStack> onGetDroppedStacks(BlockState state,
                                                      ServerWorld world,
                                                      BlockPos pos,
                                                      @Nullable BlockEntity blockEntity,
                                                      @Nullable Entity entity,
                                                      ItemStack tool
    ) {
        // Get the actual drops
        List<ItemStack> actualDrops = Block.getDroppedStacks(state, world, pos, blockEntity, entity, tool);

        // Check if the entity is a player
        if (entity instanceof ServerPlayerEntity player) {
            ServerPlayNetworking.send(player,
                    new DropFromBlockPayload(
                            Registries.BLOCK.getRawId(state.getBlock()),
                            new ItemStackDto(tool),
                            actualDrops.stream().map(ItemStackDto::new).toArray(ItemStackDto[]::new)));
        }

        return actualDrops;
    }




}
