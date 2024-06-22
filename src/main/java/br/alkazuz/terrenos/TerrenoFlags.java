package br.alkazuz.terrenos;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum TerrenoFlags {

    PVP("PvP", true, 50000, new ItemStack(Material.DIAMOND_SWORD), "pvp", "Permite o pvp no terreno."),
    SET_HOME("/sethome", true, 30000, new ItemStack(Material.BED), "sethome", "Permite outros jogadores setar a home no terreno."),
    TP_ACCEPT("/tpaccept", true, 30000, new ItemStack(Material.ENDER_PEARL), "tpaccept", "Permite outros jogadores aceitar teleporte para o terreno."),
    PVP_24H("PvP 24h", false, 8_000_000, new ItemStack(Material.GOLD_SWORD), "pvp24h", "Permite o pvp no terreno 24h.");
    private final String name;
    private final boolean defaultValue;
    private final double priceToggler;
    private final ItemStack icon;
    private final String flag;
    private final String description;

    TerrenoFlags(String name, boolean defaultValue, double priceToggle, ItemStack icon, String flag, String description) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.priceToggler = priceToggle;
        this.icon = icon;
        this.flag = flag;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public double getPriceToggler() {
        return priceToggler;
    }

    public boolean getDefaultValue() {
        return defaultValue;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public String getFlag() {
        return flag;
    }

    public String getDescription() {
        return description;
    }

    public static TerrenoFlags getByName(String name) {
        for (TerrenoFlags flag : values()) {
            if (flag.getName().equalsIgnoreCase(name)) {
                return flag;
            }
        }
        return null;
    }

}
