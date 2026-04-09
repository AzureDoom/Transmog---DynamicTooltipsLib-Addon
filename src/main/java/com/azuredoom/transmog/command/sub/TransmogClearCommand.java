package com.azuredoom.transmog.command.sub;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

import com.azuredoom.transmog.TranslationKeys;
import com.azuredoom.transmog.util.ItemTransmogUtil;
import com.azuredoom.transmog.util.TranslationUtil;
import com.azuredoom.transmog.util.TransmogApplyUtil;

public class TransmogClearCommand extends AbstractPlayerCommand {

    public TransmogClearCommand() {
        super("clear", "Clear the held item's transmog");
        this.requirePermission("transmog.clear");
        this.setPermissionGroup(GameMode.Creative);
    }

    /**
     * Executes the "clear" command, which removes the transmog from the item currently held by the player.
     *
     * @param context   the command context providing relevant execution details; must not be null
     * @param store     the data store containing the relevant entity information; must not be null
     * @param ref       the reference to the target entity store; must not be null
     * @param playerRef the reference to the player executing the command; must not be null
     * @param world     in the current world in which the command is being executed, must not be null
     */
    @Override
    protected void execute(
        @Nonnull CommandContext context,
        @Nonnull Store<EntityStore> store,
        @Nonnull Ref<EntityStore> ref,
        @Nonnull PlayerRef playerRef,
        @Nonnull World world
    ) {
        var heldItem = InventoryComponent.getItemInHand(store, ref);
        if (heldItem == null || heldItem.isEmpty()) {
            context.sendMessage(TranslationUtil.translate(TranslationKeys.TRANSMOG_ITEM_NOT_IN_HAND));
            return;
        }

        var currentTransmogItemId = ItemTransmogUtil.getTransmogItemId(heldItem);
        if (currentTransmogItemId == null) {
            playerRef.sendMessage(
                TranslationUtil.translate(
                    TranslationKeys.TRANSMOG_NO_TRANSMOG_ITEM
                )
            );
            return;
        }

        var updatedItem = ItemTransmogUtil.withTransmogItemId(heldItem, null);
        if (TransmogApplyUtil.failedToReplaceHeldItem(store, ref, updatedItem)) {
            context.sendMessage(TranslationUtil.translate(TranslationKeys.TRANSMOG_COULD_NOT_UPDATE_SLOT));
            return;
        }

        TransmogApplyUtil.refreshPlayer(playerRef);

        context.sendMessage(
            TranslationUtil.translate(
                TranslationKeys.TRANSMOG_CLEARED_TRANSMOG_FROM_ITEM,
                msg -> msg.param("currentTransmogItemId", currentTransmogItemId)
                    .param("originalItem", heldItem.getItemId())
            )
        );
    }
}
