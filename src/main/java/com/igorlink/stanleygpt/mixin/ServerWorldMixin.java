package com.igorlink.stanleygpt.mixin;

import com.igorlink.stanleygpt.payloads.ExplosionHappenedPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.block.TntBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin class for {@link ServerWorld}.
 * This class is used to send a packet to the client when an explosion happens in the world.
 */
@Mixin(ServerWorld.class)
public class ServerWorldMixin {

    /**
     * This method is called when an explosion happens in the world.
     *
     * @param entity the entity that causes the explosion
     * @param damageSource the damage source of the explosion
     * @param behavior the explosion behavior
     * @param x the x coordinate of the explosion
     * @param y the y coordinate of the explosion
     * @param z the z coordinate of the explosion
     * @param power the power
     * @param createFire whether to create fire
     * @param explosionSourceType the explosion source type
     * @param smallParticle the small particle effect
     * @param largeParticle the large particle effect
     * @param soundEvent the sound event
     * @param ci the callback info
     */
    @Inject(method = "createExplosion", at = @At("HEAD"))
    public void onExplosion(@Nullable Entity entity,
                            @Nullable DamageSource damageSource,
                            @Nullable ExplosionBehavior behavior,
                            double x,
                            double y,
                            double z,
                            float power,
                            boolean createFire,
                            World.ExplosionSourceType explosionSourceType,
                            ParticleEffect smallParticle,
                            ParticleEffect largeParticle,
                            RegistryEntry<SoundEvent> soundEvent,
                            CallbackInfo ci) {

        // Explosion position
        Vec3d explosionPos = new Vec3d(x, y, z);

        // Check a;; players in the world
        ServerWorld world = (ServerWorld) (Object) this;
        for (ServerPlayerEntity player : world.getPlayers()) {
            // Player position
            Vec3d playerPos = player.getPos();

            // Distance between player and explosion
            double distance = playerPos.distanceTo(explosionPos);

            //If the player is within 20 blocks of the explosion, send the payload
            if (distance <= 20) {
                ServerPlayNetworking.send(player, new ExplosionHappenedPayload(
                        getExplosionCause(entity, new BlockPos((int) x, (int) y, (int) z), world),
                        (int) distance
                ));
            }
        }
    }


    /**
     * This method is used to get the cause of the explosion.
     *
     * @param sourceEntity the entity that causes the explosion
     * @param explosionPos the position of the explosion
     * @param world the world where the explosion happens
     * @return the cause of the explosion
     */
    @Unique
    private String getExplosionCause(Entity sourceEntity, BlockPos explosionPos, ServerWorld world) {
        return switch (sourceEntity) {
            case CreeperEntity ignored -> "Creeper";
            case PlayerEntity ignored -> "Player";
            case FireworkRocketEntity ignored -> "Player (Firework)";
            case EndCrystalEntity ignored -> "End Crystal";
            case EnderDragonEntity ignored -> "Ender Dragon";
            case LightningEntity ignored -> "Thunderbolt";
            case null -> switch (world.getBlockState(explosionPos).getBlock()) {
                case BedBlock ignored -> "Bed";
                case RespawnAnchorBlock ignored -> "Respawn Anchor";
                case TntBlock ignored -> "Dynamite";
                default -> {
                    BlockState blockState = world.getBlockState(explosionPos);
                    yield blockState.getBlock().getName().getString();
                }
            };
            default -> "Unknown";
        };
    }

}
