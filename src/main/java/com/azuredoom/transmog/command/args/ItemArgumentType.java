package com.azuredoom.transmog.command.args;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.ParseResult;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
import com.hypixel.hytale.server.core.command.system.suggestion.SuggestionResult;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import com.azuredoom.transmog.TranslationKeys;
import com.azuredoom.transmog.util.TranslationUtil;

/**
 * Represents an argument type that parses and suggests valid {@code Item} objects based on a provided item ID. This
 * class is used in command parsing to ensure that the input corresponds to a valid {@code Item} asset. It also supports
 * suggestions for item IDs based on partial input.
 */
public final class ItemArgumentType extends ArgumentType<Item> {

    public static final ItemArgumentType INSTANCE = new ItemArgumentType(
        TranslationUtil.translate(TranslationKeys.TRANSMOG_ITEM),
        TranslationUtil.translate(TranslationKeys.TRANSMOG_ITEM_ID),
        1,
        "Weapon_Club_Copper",
        "Weapon_Club_Crude"
    );

    ItemArgumentType(
        @NonNullDecl Message name,
        @NonNullDecl Message argumentUsage,
        int numberOfParameters,
        @NullableDecl String... examples
    ) {
        super(name, argumentUsage, numberOfParameters, examples);
    }

    /**
     * Parses an array of strings to extract and validate an {@code Item} object based on a provided item ID. If parsing
     * fails, appropriate failure messages are added to the provided {@code ParseResult}.
     *
     * @param strings     an array of strings where the first element is expected to contain the item ID; must not be
     *                    null
     * @param parseResult the {@code ParseResult} object used to record parsing status and failure messages; must not be
     *                    null
     * @return the {@code Item} object corresponding to the provided item ID, or null if the parsing fails
     */
    @NullableDecl
    @Override
    public Item parse(@NonNullDecl String[] strings, @NonNullDecl ParseResult parseResult) {
        if (strings.length == 0) {
            parseResult.fail(TranslationUtil.translate(TranslationKeys.TRANSMOG_MISSING_ITEM_ID));
            return null;
        }

        var itemId = strings[0];
        if (itemId == null || itemId.isBlank()) {
            parseResult.fail(TranslationUtil.translate(TranslationKeys.TRANSMOG_SOURCE_ITEM_ID_EMPTY));
            return null;
        }

        var item = Item.getAssetMap().getAsset(itemId);
        if (item == null) {
            parseResult.fail(
                TranslationUtil.translate(TranslationKeys.TRANSMOG_UNKNOWN_ITEM_ID, msg -> msg.param("itemId", itemId))
            );
            return null;
        }

        return item;
    }

    /**
     * Provides suggestions for valid item IDs based on the text already entered by the user. This method is used in
     * interactive command handling to assist users in selecting valid {@code Item} objects by matching input prefixes.
     *
     * @param sender             the command sender who requested the suggestions; must not be null
     * @param textAlreadyEntered the text that the user has entered so far; must not be null
     * @param numParametersTyped the number of parameters that have been typed in the command
     * @param result             the {@code SuggestionResult} object used to add matching suggestions; must not be null
     */
    @Override
    public void suggest(
        @NonNullDecl CommandSender sender,
        @NonNullDecl String textAlreadyEntered,
        int numParametersTyped,
        @NonNullDecl SuggestionResult result
    ) {
        var entered = textAlreadyEntered.toLowerCase();

        for (var itemId : Item.getAssetMap().getAssetMap().keySet()) {
            if (entered.isEmpty() || itemId.toLowerCase().startsWith(entered)) {
                result.suggest(itemId);
            }
        }
    }
}
