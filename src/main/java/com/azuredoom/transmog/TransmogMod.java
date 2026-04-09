package com.azuredoom.transmog;

import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.plugin.PluginManager;

import com.azuredoom.transmog.command.TransmogCommands;
import com.azuredoom.transmog.compat.DynamicTooltipsLibCompat;

public class TransmogMod extends JavaPlugin {

    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public TransmogMod(JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void start() {
        infoLog("Starting Transmog!");
    }

    @Override
    protected void setup() {
        infoLog("Setting up Transmog!");

        if (PluginManager.get().getPlugin(new PluginIdentifier("org.herolias", "DynamicTooltipsLib")) != null) {
            DynamicTooltipsLibCompat.register();
        }
        this.getCommandRegistry().registerCommand(new TransmogCommands());
    }

    @Override
    protected void shutdown() {
        infoLog("Shutting down Transmog!");
    }

    /**
     * Logs a message at the SEVERE level using the application's logger.
     *
     * @param message the message to log at the SEVERE level
     */
    public static void severeLog(String message) {
        LOGGER.atSevere().log(message);
    }

    /**
     * Logs a message at the INFO level using the application's logger.
     *
     * @param message the message to log at the INFO level
     */
    public static void infoLog(String message) {
        LOGGER.atInfo().log(message);
    }
}
