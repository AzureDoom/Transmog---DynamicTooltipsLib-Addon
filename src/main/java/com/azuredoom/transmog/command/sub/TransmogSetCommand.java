package com.azuredoom.transmog.command.sub;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

import com.azuredoom.transmog.TranslationKeys;
import com.azuredoom.transmog.command.args.ItemArgumentType;
import com.azuredoom.transmog.util.TranslationUtil;
import com.azuredoom.transmog.util.TransmogApplyUtil;

public class TransmogSetCommand extends AbstractPlayerCommand {

    @Nonnull
    private final RequiredArg<Item> itemArg;

    public TransmogSetCommand() {
        super("set", "Apply another item's model to the held item for transmog testing");
        this.requirePermission("transmog.set");
        this.setPermissionGroup(GameMode.Creative);

        this.itemArg = withRequiredArg("itemId", "Item id to copy the model from", ItemArgumentType.INSTANCE);
    }

    /**
     * Executes the transmog set operation. This command applies the model and texture of the specified source item to
     * the currently held item in the player's hand. If any preconditions such as missing item data or invalid source
     * item information are not met, appropriate error messages are sent to the player.
     *
     * @param context   the {@code CommandContext} providing contextual information about the command execution,
     *                  including arguments and methods for interacting with the player; must not be null
     * @param store     the {@code Store} containing the {@code EntityStore} used to fetch or modify data during the
     *                  transmog operation; must not be null
     * @param ref       a {@code Ref} of the {@code EntityStore}, used to reference the specific data entity being
     *                  modified; must not be null
     * @param playerRef a {@code PlayerRef} representing the player executing the command; must not be null
     * @param world     the {@code World} environment context in which the command is being executed; must not be null
     */
    @Override
    protected void execute(
        @Nonnull CommandContext context,
        @Nonnull Store<EntityStore> store,
        @Nonnull Ref<EntityStore> ref,
        @Nonnull PlayerRef playerRef,
        @Nonnull World world
    ) {
        var sourceItem = context.get(itemArg);
        if (sourceItem == null) {
            context.sendMessage(TranslationUtil.translate(TranslationKeys.TRANSMOG_INVALID_ITEM));
            return;
        }

        var sourceItemId = sourceItem.getId();
        if (sourceItemId == null || sourceItemId.isBlank()) {
            context.sendMessage(TranslationUtil.translate(TranslationKeys.TRANSMOG_SOURCE_ITEM_ID_EMPTY));
            return;
        }

        var sourceModel = sourceItem.getModel();
        if (sourceModel == null || sourceModel.isBlank()) {
            context.sendMessage(
                TranslationUtil.translate(
                    TranslationKeys.TRANSMOG_NO_USABLE_MODEL,
                    msg -> msg.param("originalItem", sourceItemId)
                )
            );
            return;
        }

        var sourceTexture = sourceItem.getTexture();
        if (sourceTexture == null || sourceTexture.isBlank()) {
            context.sendMessage(
                TranslationUtil.translate(
                    TranslationKeys.TRANSMOG_NO_USABLE_TEXTURE,
                    msg -> msg.param("originalItem", sourceItemId)
                )
            );
            return;
        }

        if (!TransmogApplyUtil.applyToHeldItem(store, ref, playerRef, sourceItemId)) {
            context.sendMessage(TranslationUtil.translate(TranslationKeys.TRANSMOG_COULD_NOT_UPDATE_SLOT));
            return;
        }

        context.sendMessage(
            TranslationUtil.translate(
                TranslationKeys.TRANSMOG_APPLIED_TRANSMOG_TO_ITEM,
                msg -> msg.param("transmogItemId", sourceItemId)
            )
        );
    }
}
