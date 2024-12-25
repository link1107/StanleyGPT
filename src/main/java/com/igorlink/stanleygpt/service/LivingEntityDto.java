package com.igorlink.stanleygpt.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class LivingEntityDto {
    /**
     * Entity ID.
     */
    @Getter
    private final int entityId;

    /**
     * Entity custom name.
     */
    @Getter
    @NotNull
    private final String entityCustomName;

    /**
     * Is entity a baby.
     */
    @Getter
    private final boolean isBaby;

    /**
     * Is entity tamed by player.
     */
    @Getter
    private final boolean isTamedByPlayer;


    /**
     * Constructor. Creates a DTO from a living entity.
     *
     * @param livingEntity the living entity
     */
    public LivingEntityDto(@NotNull LivingEntity livingEntity) {
        this.entityId = Registries.ENTITY_TYPE.getRawId(livingEntity.getType());
        this.entityCustomName = livingEntity.getCustomName() == null ? "" : livingEntity.getCustomName().getString();
        this.isBaby = livingEntity.age < 0 || livingEntity.isBaby();
        this.isTamedByPlayer = livingEntity instanceof TameableEntity tameableEntity
                && tameableEntity.getOwner() instanceof ServerPlayerEntity;
    }


    /**
     * Constructor. Creates a DTO from an entity ID, custom name, baby status, and tamed status.
     *
     * @param entityId         the entity ID
     * @param entityCustomName the entity custom name
     * @param isBaby           the baby status
     * @param isTamedByPlayer  the tamed status
     */
    public LivingEntityDto(int entityId,
                           @NotNull String entityCustomName,
                           boolean isBaby,
                           boolean isTamedByPlayer) {
        this.entityId = entityId;
        this.entityCustomName = entityCustomName;
        this.isBaby = isBaby;
        this.isTamedByPlayer = isTamedByPlayer;
    }


    /**
     * Returns a string representation of the object.
     */
    @Override
    public String toString() {
        return Registries.ENTITY_TYPE.get(entityId).getName().getString() +
                " (имя: " + (entityCustomName.isEmpty() ? "нет" : "\"" + entityCustomName + "\"") +
                ", ребенок: " + isBaby + ", приручен игроком: " + isTamedByPlayer + ")";
    }


    /**
     * Returns the entity type.
     *
     * @return the entity type
     */
    public EntityType<?> getEntityType() {
        return Registries.ENTITY_TYPE.get(entityId);
    }


    /**
     * Returns the entity type name.
     *
     * @return the entity type name
     */
    public String getEntityTypeName() {
        return getEntityType().getName().getString();
    }

}
