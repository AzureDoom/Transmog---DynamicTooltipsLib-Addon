package com.azuredoom.transmog.command.sub;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Objects;
import javax.annotation.Nonnull;

import com.azuredoom.transmog.TranslationKeys;
import com.azuredoom.transmog.util.ItemTransmogUtil;
import com.azuredoom.transmog.util.TranslationUtil;

public class TransmogInspectCommand extends AbstractPlayerCommand {

    public TransmogInspectCommand() {
        super("inspect", "Inspect the held item's transmog state");
        this.requirePermission("transmog.inspect");
        this.setPermissionGroup(GameMode.Creative);
    }

    /**
     * Executes the logic for inspecting the transmog state of the item currently held by the player. This method
     * gathers detailed information about the held item's transmog configuration, including its model and texture
     * identifiers, and sends various messages back to the player based on the transmog state and associated properties
     * of the item.
     *
     * @param context   the command execution context, used for sending messages and interacting with other game
     *                  systems, must not be null
     * @param store     the store containing the entity data; must not be null
     * @param ref       a reference to the player's entity in the store system; must not be null
     * @param playerRef a reference to the player issuing the command; must not be null
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

        var heldItemId = heldItem.getItemId();
        var transmogItemId = ItemTransmogUtil.getTransmogItemId(heldItem);

        context.sendMessage(
            TranslationUtil.translate(TranslationKeys.TRANSMOG_HELD_ITEM, msg -> msg.param("itemId", heldItemId))
        );

        if (transmogItemId == null) {
            context.sendMessage(TranslationUtil.translate(TranslationKeys.TRANSMOG_NOT_TRANSMOG));
            return;
        }

        context.sendMessage(
            TranslationUtil.translate(TranslationKeys.TRANSMOG_ID, msg -> msg.param("itemId", transmogItemId))
        );

        var transmogItem = Item.getAssetMap().getAsset(transmogItemId);
        if (transmogItem == null) {
            context.sendMessage(TranslationUtil.translate(TranslationKeys.TRANSMOG_MISSING_SOURCE_ITEM));
            return;
        }

        var model = transmogItem.getModel();
        var texture = transmogItem.getTexture();

        context.sendMessage(TranslationUtil.translate(TranslationKeys.TRANSMOG_FOUND_SOURCE_ITEM));
        context.sendMessage(
            TranslationUtil.translate(TranslationKeys.TRANSMOG_MODEL_RESULT, msg -> msg.param("model", safe(model)))
        );
        context.sendMessage(
            TranslationUtil.translate(
                TranslationKeys.TRANSMOG_TEXTURE_RESULT,
                msg -> msg.param("texture", safe(texture))
            )
        );

        var baseItem = Item.getAssetMap().getAsset(heldItemId);
        if (baseItem != null) {
            boolean sameModel = equals(baseItem.getModel(), model);
            boolean sameTexture = equals(baseItem.getTexture(), texture);
            context.sendMessage(
                TranslationUtil.translate(
                    TranslationKeys.TRANSMOG_SAME_VISUALS_REPORT,
                    msg -> msg.param("answer", (sameModel && sameTexture ? "yes" : "no"))
                )
            );
        }
    }

    /**
     * Safely retrieves a non-blank string value, replacing null or blank inputs with the default string "<none>".
     *
     * @param value the input string to be checked and processed; may be null or blank
     * @return the original input string if it is non-blank, or "<none>" if the input is null or blank
     */
    private static String safe(String value) {
        return value == null || value.isBlank() ? "<none>" : value;
    }

    /**
     * Compares two strings for equality, safely handling null values. This method utilizes {@code Objects.equals} to
     * perform the comparison.
     *
     * @param a the first string to compare; may be null
     * @param b the second string to compare; may be null
     * @return {@code true} if both strings are equal or if both are null; {@code false} otherwise
     */
    private static boolean equals(String a, String b) {
        return Objects.equals(a, b);
    }
}
