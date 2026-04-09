package com.azuredoom.transmog.command;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

import com.azuredoom.transmog.command.sub.TransmogClearCommand;
import com.azuredoom.transmog.command.sub.TransmogSetCommand;

public class TransmogCommands extends AbstractCommandCollection {

    public TransmogCommands() {
        super("transmog", "Commands for item model transmogs");
        addSubCommand(new TransmogSetCommand());
        addSubCommand(new TransmogClearCommand());
    }
}
