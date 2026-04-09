package com.azuredoom.transmog.util;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemContext;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.herolias.tooltips.api.DynamicTooltipsApiProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class TransmogApplyUtil {

    private TransmogApplyUtil() {}

    /**
     * Applies a transmog effect to the item currently held by the specified player, using the provided transmog item
     * ID. If the player is not holding a valid item or the update operation fails, the method returns {@code false}.
     *
     * @param store          the store containing entity data and components, used to retrieve and update the player's
     *                       held item; must not be null
     * @param ref            a reference to the entity whose held item will be modified; must not be null
     * @param playerRef      a reference to the player performing the transmog operation; must not be null
     * @param transmogItemId the ID of the transmog item to apply to the held item; must not be null
     * @return {@code true} if the transmog operation was successful, otherwise {@code false}
     */
    public static boolean applyToHeldItem(
        @Nonnull Store<EntityStore> store,
        @Nonnull Ref<EntityStore> ref,
        @Nonnull PlayerRef playerRef,
        @Nonnull String transmogItemId
    ) {
        var heldItem = InventoryComponent.getItemInHand(store, ref);
        if (heldItem == null || heldItem.isEmpty()) {
            return false;
        }

        var updatedItem = ItemTransmogUtil.withTransmogItemId(heldItem, transmogItemId);
        if (failedToReplaceHeldItem(store, ref, updatedItem)) {
            return false;
        }

        refreshPlayer(playerRef);
        return true;
    }

    /**
     * Determines if an attempt to apply a transmog effect to the item in the specified {@code ItemContext} has failed.
     * This involves replacing the current item stack in the specified slot with an updated version that includes the
     * provided transmog item ID.
     *
     * @param itemContext    the context containing the item and inventory information; must not be null
     * @param transmogItemId the transmog item ID to apply to the item; can be null if no transmog is to be applied
     * @return {@code true} if the operation to apply the transmog effect fails, otherwise {@code false}
     */
    public static boolean failedToApplyToItemContext(
        @Nonnull ItemContext itemContext,
        @Nullable String transmogItemId
    ) {
        var originalItem = itemContext.getItemStack();
        if (originalItem.isEmpty()) {
            return true;
        }

        var updatedItem = ItemTransmogUtil.withTransmogItemId(originalItem, transmogItemId);

        var transaction = itemContext.getContainer()
            .replaceItemStackInSlot(
                itemContext.getSlot(),
                originalItem,
                updatedItem
            );

        return !transaction.succeeded();
    }

    /**
     * Attempts to replace the currently held item in the player's inventory with the provided {@code itemStack}. If the
     * operation can be performed due to no valid active slot in the inventory, this method returns {@code true}.
     * Otherwise, it attempts to update the active slot in the "Tool" or "Hotbar" inventory component and returns
     * {@code false}.
     *
     * @param store     the store containing entity data and components; must not be null
     * @param ref       a reference to the entity whose held item is being replaced; must not be null
     * @param itemStack the {@code ItemStack} to set in the currently active inventory slot; must not be null
     * @return {@code true} if the replacement fails due to no valid active slot, otherwise {@code false}
     */
    public static boolean failedToReplaceHeldItem(
        @Nonnull Store<EntityStore> store,
        @Nonnull Ref<EntityStore> ref,
        @Nonnull ItemStack itemStack
    ) {
        var tool = store.getComponent(ref, InventoryComponent.Tool.getComponentType());
        if (tool != null && tool.isUsingToolsItem() && tool.getActiveSlot() >= 0) {
            tool.getInventory().setItemStackForSlot(tool.getActiveSlot(), itemStack, true);
            return false;
        }

        var hotbar = store.getComponent(ref, InventoryComponent.Hotbar.getComponentType());
        if (hotbar != null && hotbar.getActiveSlot() >= 0) {
            hotbar.getInventory().setItemStackForSlot(hotbar.getActiveSlot(), itemStack, true);
            return false;
        }

        return true;
    }

    /**
     * Refreshes the player by invoking the associated API to update their state or related information. This method
     * interacts with the {@link DynamicTooltipsApiProvider} to trigger a refresh for the specified player based on
     * their unique identifier.
     *
     * @param playerRef a reference to the player to be refreshed; must not be null
     */
    public static void refreshPlayer(@Nonnull PlayerRef playerRef) {
        var api = DynamicTooltipsApiProvider.get();
        if (api != null) {
            api.refreshPlayer(playerRef.getUuid());
        }
    }
}
