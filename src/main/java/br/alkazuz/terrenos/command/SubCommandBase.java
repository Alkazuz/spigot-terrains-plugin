package br.alkazuz.terrenos.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SubCommandBase {
    private final String name;
    private final String permission;
    private final String usage;
    private final String description;

    public SubCommandBase(String name, String permission, String usage, String description) {
        this.name = name;
        this.permission = permission;
        this.usage = usage;
        this.description = description;
    }

    public boolean avaibleForPlayer(Player player) {
        return true;
    }

    public String getName() {
        return this.name;
    }

    public String getPermission() {
        return this.permission;
    }

    public String getUsage() {
        return this.usage;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean canExecute(CommandSender sender) {
        return sender.hasPermission(this.permission);
    }

    public boolean execute(CommandSender sender, String[] args) {
        return false;
    }

    public String getHelpMessage() {
        return "§c/" + this.usage + " §7- " + this.description;
    }

}
