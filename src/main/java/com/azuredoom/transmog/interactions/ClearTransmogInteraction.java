package com.azuredoom.transmog.interactions;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.player.pages.choices.ChoiceInteraction;
import com.hypixel.hytale.server.core.inventory.ItemContext;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

import com.azuredoom.transmog.TranslationKeys;
import com.azuredoom.transmog.util.ItemTransmogUtil;
import com.azuredoom.transmog.util.TranslationUtil;
import com.azuredoom.transmog.util.TransmogApplyUtil;

public class ClearTransmogInteraction extends ChoiceInteraction {

    @Nonnull
    private final ItemContext targetItemContext;

    public ClearTransmogInteraction(@Nonnull ItemContext targetItemContext) {
        this.targetItemContext = targetItemContext;
    }

    @Override
    public void run(
        @Nonnull Store<EntityStore> store,
        @Nonnull Ref<EntityStore> entityRef,
        @Nonnull PlayerRef playerRef
    ) {
        var originalItem = targetItemContext.getItemStack();
        if (originalItem.isEmpty()) {
            playerRef.sendMessage(
                TranslationUtil.translate(
                    TranslationKeys.TRANSMOG_NO_VALID_ITEM
                )
            );
            return;
        }

        var currentTransmogItemId = ItemTransmogUtil.getTransmogItemId(originalItem);
        if (currentTransmogItemId == null) {
            playerRef.sendMessage(
                TranslationUtil.translate(
                    TranslationKeys.TRANSMOG_NO_TRANSMOG_ITEM
                )
            );
            return;
        }

        if (TransmogApplyUtil.failedToApplyToItemContext(targetItemContext, null)) {
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
                TranslationKeys.TRANSMOG_CLEAR,
                msg -> msg.param("transmogItemId", currentTransmogItemId)
                    .param("originalItem", originalItem.getItemId())
            )
        );
    }
}
