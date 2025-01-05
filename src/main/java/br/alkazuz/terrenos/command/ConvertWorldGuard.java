package br.alkazuz.terrenos.command;

import br.alkazuz.terrenos.object.PlayerTerreno;
import br.alkazuz.terrenos.object.PlayerTerrenoManager;
import br.alkazuz.terrenos.object.Terreno;
import br.alkazuz.terrenos.utils.TerrenoManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.List;

public class ConvertWorldGuard implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!commandSender.hasPermission("terrenos.admin")) {
            commandSender.sendMessage("§cSem permissão.");
            return true;
        }
        /*if (Bukkit.getPluginManager().getPlugin("TGTerrenos") == null) {
            commandSender.sendMessage("§cPlugin TGTerrenos não encontrado.");
            return true;
        }*/

        Plugin worldGuard = Bukkit.getPluginManager().getPlugin("WorldGuard");

        if (worldGuard == null) {
            commandSender.sendMessage("§cPlugin WorldGuard não encontrado.");
            return true;
        }

        if (strings.length == 0) {
            commandSender.sendMessage("§cUtilize /convertworldguard <mundo>");
            return true;
        }

        String worldName = strings[0];

        File fileWorldguardRegions = new File(worldGuard.getDataFolder(), "worlds/" + worldName + "/regions.yml");

        if (!fileWorldguardRegions.exists()) {
            commandSender.sendMessage("§cArquivo regions.yml não encontrado.");
            return true;
        }

        FileConfiguration fileConfiguration = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(fileWorldguardRegions);

        if (fileConfiguration.getConfigurationSection("regions") == null) {
            commandSender.sendMessage("§cNenhuma região encontrada.");
            return true;
        }
        int count = 0;
        for (String regionName : fileConfiguration.getConfigurationSection("regions").getKeys(false)) {
            String path = "regions." + regionName + ".";
            int minX = fileConfiguration.getInt("regions." + regionName + ".min.x");
            int maxX = fileConfiguration.getInt("regions." + regionName + ".max.x");
            int minZ = fileConfiguration.getInt("regions." + regionName + ".min.z");
            int maxZ = fileConfiguration.getInt("regions." + regionName + ".max.z");

            List<String> owners = fileConfiguration.getStringList(path + "owners.players");
            List<String> members = fileConfiguration.getStringList(path + "members.players");

            String owner = owners.isEmpty() ? null : owners.get(0);

            Terreno terreno = new Terreno(null, owner, minX, maxX, minZ, maxZ, worldName);
            terreno.setFlag("pvp", false);
            TerrenoManager.addTerreno(terreno);
            terreno.saveAndWait();

            count++;

            for (String member : members) {
                PlayerTerreno playerTerreno = new PlayerTerreno(member);
                playerTerreno.setTerreno(terreno);
                playerTerreno.setBreakBlock(true);
                playerTerreno.setPlaceBlocks(true);
                playerTerreno.setOpenChest(true);
                playerTerreno.setUseCommands(true);
                playerTerreno.save();

                PlayerTerrenoManager.savePlayerTerreno(playerTerreno);
            }
        }

        commandSender.sendMessage("§a" + count + " terrenos convertidos.");

        return true;
    }
}
