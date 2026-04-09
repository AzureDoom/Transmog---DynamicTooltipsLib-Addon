package com.azuredoom.transmog.provider;

import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.herolias.tooltips.api.ItemVisualOverrides;
import org.herolias.tooltips.api.TooltipData;
import org.herolias.tooltips.api.TooltipPriority;
import org.herolias.tooltips.api.TooltipProvider;

import java.util.Objects;
import javax.annotation.Nonnull;

import com.azuredoom.transmog.util.ItemTransmogUtil;

/**
 * Provides tooltip data for items that support transmogrification, allowing for dynamic updates to the visual
 * representation of items based on associated transmog metadata.
 * <p>
 * Credit to <a href="http://www.daniel-wysdak.de/">globalhive</a> for the original implementation.
 */
public class ItemTransmogTooltipProvider implements TooltipProvider {

    public static final String PROVIDER_ID = "transmog:item-transmog";

    @Override
    public @Nonnull String getProviderId() {
        return PROVIDER_ID;
    }

    @Override
    public int getPriority() {
        return TooltipPriority.OVERRIDE;
    }

    @Override
    public TooltipData getTooltipData(@NonNullDecl String itemId, String metadata) {
        return getTooltipData(itemId, metadata, null);
    }

    /**
     * Generates tooltip data for a given item, incorporating transmog metadata and locale-specific visual overrides if
     * applicable.
     *
     * @param itemId   the unique identifier for the item whose tooltip data is to be generated; must not be null
     * @param metadata the metadata associated with the item, used to retrieve transmog information, can be null
     * @param locale   the locale to use for generating locale-specific visual overrides; can be null
     * @return a {@code TooltipData} object containing the visual overrides and transmog hash input if transmog data is
     *         applicable, or {@code null} if no tooltip data is applicable for the item
     */
    @Override
    public TooltipData getTooltipData(@NonNullDecl String itemId, String metadata, String locale) {
        var transmogItemId = ItemTransmogUtil.getTransmogItemId(metadata);
        if (transmogItemId == null) {
            return null;
        }

        var transmogItem = Item.getAssetMap().getAsset(transmogItemId);
        if (transmogItem == null) {
            return null;
        }

        var model = transmogItem.getModel();
        if (model == null || model.isBlank()) {
            return null;
        }

        var texture = transmogItem.getTexture();

        var baseItem = Item.getAssetMap().getAsset(itemId);
        if (
            baseItem != null && Objects.equals(baseItem.getModel(), model)
                && Objects.equals(baseItem.getTexture(), texture)
        ) {
            return null;
        }

        var overrides = ItemVisualOverrides.builder().model(model);
        if (texture != null && !texture.isBlank()) {
            overrides.texture(texture);
        }

        return TooltipData.builder()
            .hashInput(ItemTransmogUtil.buildHashInput(itemId, transmogItemId, model + "|" + texture))
            .visualOverrides(overrides.build())
            .build();
    }
}
