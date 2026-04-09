package com.azuredoom.transmog.interactions;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.player.pages.choices.ChoiceInteraction;
import com.hypixel.hytale.server.core.inventory.ItemContext;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

import com.azuredoom.transmog.TranslationKeys;
import com.azuredoom.transmog.util.TranslationUtil;
import com.azuredoom.transmog.util.TransmogApplyUtil;

public class SetTransmogInteraction extends ChoiceInteraction {

    @Nonnull
    private final ItemContext targetItemContext;

    @Nonnull
    private final String transmogItemId;

    public SetTransmogInteraction(@Nonnull ItemContext targetItemContext, @Nonnull String transmogItemId) {
        this.targetItemContext = targetItemContext;
        this.transmogItemId = transmogItemId;
    }

    @Override
    public void run(
        @Nonnull Store<EntityStore> store,
        @Nonnull Ref<EntityStore> entityRef,
        @Nonnull PlayerRef playerRef
    ) {
        if (transmogItemId.isBlank()) {
            playerRef.sendMessage(
                TranslationUtil.translate(
                    TranslationKeys.TRANSMOG_ITEM_EMPTY
                )
            );
            return;
        }

        var originalItem = targetItemContext.getItemStack();
        if (originalItem.isEmpty()) {
            playerRef.sendMessage(
                TranslationUtil.translate(
                    TranslationKeys.TRANSMOG_NO_VALID_ITEM
                )
            );
            return;
        }

        if (TransmogApplyUtil.failedToApplyToItemContext(targetItemContext, transmogItemId)) {
            playerRef.sendMessage(
                TranslationUtil.translate(
                    TranslationKeys.TRANSMOG_COULD_NOT_UPDATE
                )
            );
            return;
        }

        TransmogApplyUtil.refreshPlayer(playerRef);

        playerRef.sendMessage(
            TranslationUtil.translate(
                TranslationKeys.TRANSMOG_SUCCESS,
                msg -> msg.param("transmogItemId", transmogItemId).param("originalItem", originalItem.getItemId())
            )
        );
    }
}
