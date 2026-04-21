package com.azuredoom.transmog.util;

import com.google.gson.JsonParser;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import org.bson.BsonString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Utility class for handling transmog-related operations on items. Provides methods to retrieve, modify, and work with
 * transmog metadata in relation to {@code ItemStack} objects and their associated data.
 * <p>
 * Credit to <a href="http://www.daniel-wysdak.de/">globalhive</a> for the original implementation.
 */
public final class ItemTransmogUtil {

    public static final String TRANSMOG_METADATA_KEY = "TransmogItemId";

    private ItemTransmogUtil() {}

    /**
     * Retrieves the transmog item ID from the metadata of the given {@code ItemStack}.
     *
     * @param itemStack the {@code ItemStack} from which to extract the transmog item ID; can be null
     * @return the transmog item ID if it exists and is valid, or null if no transmog is applied or the input is null
     */
    @Nullable
    public static String getTransmogItemId(@Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }

        var value = itemStack.getFromMetadataOrNull(TRANSMOG_METADATA_KEY, Codec.STRING);
        return (value == null || value.isBlank()) ? null : value;
    }

    /**
     * Extracts the transmog item ID from a given metadata JSON string.
     *
     * @param metadataJson the JSON-formatted string containing item metadata; can be null or blank
     * @return the transmog item ID if successfully extracted and valid; null otherwise
     */
    @Nullable
    public static String getTransmogItemId(@Nullable String metadataJson) {
        if (metadataJson == null || metadataJson.isBlank()) {
            return null;
        }

        try {
            var root = JsonParser.parseString(metadataJson);
            if (!root.isJsonObject()) {
                return null;
            }

            var json = root.getAsJsonObject();
            if (!json.has(TRANSMOG_METADATA_KEY) || !json.get(TRANSMOG_METADATA_KEY).isJsonPrimitive()) {
                return null;
            }

            var value = json.get(TRANSMOG_METADATA_KEY).getAsString();
            return value == null || value.isBlank() ? null : value;
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * Modifies the given {@code ItemStack} by adding or removing a transmog item ID in its metadata.
     *
     * @param itemStack the {@code ItemStack} to be modified; cannot be null
     * @param itemId    the transmog item ID to be applied to the {@code ItemStack}; use null or a blank string to
     *                  remove the transmog
     * @return a modified {@code ItemStack} with the specified transmog item ID, or without transmog if {@code itemId}
     *         is null or blank
     */
    @Nonnull
    public static ItemStack withTransmogItemId(@Nonnull ItemStack itemStack, @Nullable String itemId) {
        if (itemId == null || itemId.isBlank()) {
            return itemStack.withMetadata(TRANSMOG_METADATA_KEY, null);
        }
        return itemStack.withMetadata(TRANSMOG_METADATA_KEY, new BsonString(itemId));
    }

    /**
     * Constructs a hash input string for use in transmog-related operations. The resulting string is constructed by
     * concatenating the item ID, transmog item ID, and model string using ':' as a delimiter.
     *
     * @param itemId         the unique identifier of the item; must not be null
     * @param transmogItemId the unique identifier of the transmog item; must not be null
     * @param model          the model identifier of the transmog item; must not be null
     * @return a hash input string in the format "transmog:<itemId>:<transmogItemId>:<model>"
     */
    @Nonnull
    public static String buildHashInput(@Nonnull String itemId, @Nonnull String transmogItemId, @Nonnull String model) {
        return "transmog:" + itemId + ':' + transmogItemId + ':' + model;
    }
}
