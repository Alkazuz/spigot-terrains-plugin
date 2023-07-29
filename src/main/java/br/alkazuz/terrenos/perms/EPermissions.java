package br.alkazuz.terrenos.perms;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum EPermissions {
    BUILD("Construir", "Permite que o jogador construa no terreno.", new ItemStack(Material.STONE), DefaultFlag.BUILD),
    PVP("PvP", "Permite que o jogador ataque outros jogadores no terreno.", new ItemStack(Material.DIAMOND_SWORD), DefaultFlag.PVP),
    CHEST("Chest", "Permite que o jogador abra baús no terreno.", new ItemStack(Material.CHEST), DefaultFlag.CHEST_ACCESS),
    USE("Use", "Permite que o jogador use itens no terreno.", new ItemStack(Material.DIAMOND_PICKAXE), DefaultFlag.USE);
    // INTERACT("Interact", "Permite que o jogador interaja com itens no terreno.", new ItemStack(Material.DIAMOND), DefaultFlag.INTERACT);
    // COMMANDS("Comandos", "Permite que o jogador use comandos no terreno.", new ItemStack(Material.COMMAND), DefaultFlag.ALLOWED_CMDS),

    private String name;
    private String description;
    private ItemStack item;
    private StateFlag flag;

    EPermissions(String name, String description, ItemStack item, StateFlag flag) {
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

    public StateFlag getFlag() {
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
