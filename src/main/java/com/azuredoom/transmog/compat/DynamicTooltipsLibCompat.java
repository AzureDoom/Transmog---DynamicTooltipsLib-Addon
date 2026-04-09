package com.azuredoom.transmog.compat;

import org.herolias.tooltips.api.DynamicTooltipsApiProvider;

import com.azuredoom.transmog.TransmogMod;
import com.azuredoom.transmog.provider.ItemTransmogTooltipProvider;

public class DynamicTooltipsLibCompat {

    private static boolean registered = false;

    /**
     * Registers the compatibility layer for the DynamicTooltipsLib API.
     */
    public static void register() {
        if (registered)
            return;
        registered = true;

        var api = DynamicTooltipsApiProvider.get();
        if (api == null) {
            TransmogMod.severeLog("DynamicTooltipsLib is not installed!");
            return;
        }

        api.registerProvider(new ItemTransmogTooltipProvider());
    }
}
