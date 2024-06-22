package br.alkazuz.terrenos.perms;

import br.alkazuz.terrenos.object.PlayerTerreno;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Predicate;

public enum EPermissions {
    BUILD("Construir", "Permite que o jogador construa no terreno.",
            new ItemStack(Material.STONE), "build", PlayerTerreno::canPlaceBlocks),
    BREAK("Quebrar", "Permite que o jogador quebre blocos no terreno.", new ItemStack(Material.DIAMOND_PICKAXE), "break", PlayerTerreno::canBreakBlock),
    CHEST("Chest", "Permite que o jogador abra baús no terreno.", new ItemStack(Material.CHEST), "chest_access", PlayerTerreno::canOpenChest),
    COMMANDS("Comandos", "Permite que o jogador use comandos no terreno.", new ItemStack(Material.COMMAND), "commands", PlayerTerreno::canUseCommands);

    private String name;
    private String description;
    private ItemStack item;
    private String flag;
    private final Predicate<PlayerTerreno> isEnabled;

    EPermissions(String name, String description, ItemStack item, String flag, Predicate<PlayerTerreno> isEnabled) {
        this.name = name;
        this.description = description;
        this.item = item;
        this.flag = flag;
        this.isEnabled = isEnabled;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ItemStack getItem() {
        return item;
    }

    public String getFlag() {
        return flag;
    }

    public boolean isEnabled(PlayerTerreno playerTerreno) {
        return isEnabled.test(playerTerreno);
    }

    public static EPermissions getByName(String name) {
        for (EPermissions perm : values()) {
            if (perm.getName().equalsIgnoreCase(name)) {
                return perm;
            }
        }
        return null;
    }

    public static EPermissions getByItem(ItemStack item) {
        for (EPermissions perm : values()) {
            if (perm.getItem().equals(item)) {
                return perm;
            }
        }
        return null;
    }
}
