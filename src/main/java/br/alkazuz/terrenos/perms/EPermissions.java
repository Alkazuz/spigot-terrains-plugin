package br.alkazuz.terrenos.perms;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum EPermissions {
    BUILD("Construir", "Permite que o jogador construa no terreno.", new ItemStack(Material.STONE), "build"),
    CHEST("Chest", "Permite que o jogador abra baús no terreno.", new ItemStack(Material.CHEST), "chest_access"),
    COMMANDS("Comandos", "Permite que o jogador use comandos no terreno.", new ItemStack(Material.COMMAND), "commands");

    private String name;
    private String description;
    private ItemStack item;
    private String flag;

    EPermissions(String name, String description, ItemStack item, String flag) {
        this.name = name;
        this.description = description;
        this.item = item;
        this.flag = flag;
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
