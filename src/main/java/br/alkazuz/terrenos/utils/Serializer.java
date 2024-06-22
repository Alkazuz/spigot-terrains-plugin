package br.alkazuz.terrenos.utils;

import br.alkazuz.terrenos.object.Terreno;
import org.bukkit.Chunk;

public class Serializer {
    public static int computeHash(Chunk chunk) {
        int hash = 4;
        hash = 19 * hash + chunk.getX();
        hash = 19 * hash + chunk.getZ();
        hash = 19 * hash + chunk.getWorld().getName().hashCode();
        return hash;
    }

    public static int computeHash(int x1, int x2, int z1, int z2) {
        int hash = 4;
        hash = 19 * hash + (int)(Double.doubleToLongBits(x1) ^ (Double.doubleToLongBits(x1) >>> 32));
        hash = 19 * hash + (int)(Double.doubleToLongBits(x2) ^ (Double.doubleToLongBits(x2) >>> 32));
        hash = 19 * hash + (int)(Double.doubleToLongBits(z1) ^ (Double.doubleToLongBits(z1) >>> 32));
        hash = 19 * hash + (int)(Double.doubleToLongBits(z2) ^ (Double.doubleToLongBits(z2) >>> 32));
        return hash;
    }

    public static int computeHash(Terreno terreno) {
        return computeHash(terreno.getX1(), terreno.getX2(), terreno.getZ1(), terreno.getZ2());
    }

}
