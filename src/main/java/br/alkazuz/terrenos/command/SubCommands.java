package br.alkazuz.terrenos.command;

import br.alkazuz.terrenos.command.sub.SubcommandInfo;
import br.alkazuz.terrenos.command.sub.SubcommandVender;

import java.util.ArrayList;
import java.util.List;

public class SubCommands {
    private static final List<SubCommandBase> subCommands = new ArrayList<>();

    public static void load() {
        registerSubCommand(new SubcommandInfo());
        registerSubCommand(new SubcommandVender());
    }

    public static List<SubCommandBase> getSubCommands() {
        return subCommands;
    }

    public static void registerSubCommand(SubCommandBase subCommandBase) {
        subCommands.add(subCommandBase);
    }

    public static SubCommandBase getSubCommand(String string) {
        for (SubCommandBase subCommandBase : subCommands) {
            if (subCommandBase.getName().equalsIgnoreCase(string)) {
                return subCommandBase;
            }
        }
        return null;
    }
}
