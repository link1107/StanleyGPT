package com.igorlink.stanleygpt.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class ItemStackDto {
    /**
     * Item ID.
     */
    @Getter
    private final int itemId;

    /**
     * Item custom name.
     */
    @Getter
    private final String itemCustomName;

    /**
     * Item amount.
     */
    @Getter
    private final int amount;


    /**
     * Constructor. Creates a DTO from an item stack.
     *
     * @param itemStack the item stack
     */
    public ItemStackDto(@NotNull ItemStack itemStack) {
        this.itemId = Registries.ITEM.getRawId(itemStack.getItem());
        this.itemCustomName = itemStack.getCustomName() == null ? "" : itemStack.getCustomName().getString();
        this.amount = itemStack.getCount();
    }


    /**
     * Constructor. Creates a DTO from an item ID, custom name, and amount.
     *
     * @param itemId         the item ID
     * @param itemCustomName the item custom name
     * @param amount         the amount
     */
    public ItemStackDto(int itemId, @NotNull String itemCustomName, int amount) {
        this.itemId = itemId;
        this.itemCustomName = itemCustomName;
        this.amount = amount;
    }


    /**
     * Creates a string from an array of item stack DTOs.
     *
     * @param drops the array of item stack DTOs
     * @return the string
     */
    @NotNull
    public static String createStringFromArray(ItemStackDto[] drops) {
        StringBuilder stringBuilder = new StringBuilder();
        for (ItemStackDto drop : drops) {
            if (drop.getItem() != Items.AIR) {
                stringBuilder.append(drop).append(", ");
            }
        }

        return stringBuilder.isEmpty() ? "ничего" : stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length()).toString();
    }


    /**
     * Returns a string representation of the object.
     */
    @Override
    public String toString() {
        return Registries.ITEM.get(itemId).getName().getString() +
                (itemCustomName.isEmpty() ? "" : " (кастомное название: \"" + itemCustomName + "\")") +
                " x " + amount;
    }


    /**
     * Returns a string representation of the item.
     */
    public String toItemString() {
        return Registries.ITEM.get(itemId).getName().getString() +
                (itemCustomName.isEmpty() ? "" : " (кастомное название: \"" + itemCustomName + "\")");
    }


    /**
     * Returns the item.
     *
     * @return the item
     */
    public Item getItem() {
        return Registries.ITEM.get(itemId);
    }


    /**
     * Returns whether the item stack is empty.
     *
     * @return true if the item stack is empty, false otherwise
     */
    public boolean isEmpty() {
        return getItem() == Items.AIR;
    }


    /**
     * Returns the tool name.
     *
     * @return the tool name
     */
    public String getToolName() {
        Item item = getItem();
        if (item == Items.AIR) {
            return "голыми руками";
        }
        return this.toString();
    }


    /**
     * Returns the item name.
     *
     * @return the item name
     */
    public String getItemName() {
        return getItem().getName().getString();
    }
}
