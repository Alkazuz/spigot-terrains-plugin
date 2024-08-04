package br.alkazuz.terrenos.workload.workloads;

import net.minecraft.server.v1_5_R3.WorldServer;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_5_R3.CraftWorld;

public class PlaceBlock implements WorkLoad {
    private World world;
    private int blockX, blockY, blockZ;
    private Material material;

    public PlaceBlock(World world, int blockX, int blockY, int blockZ, Material material) {
        this.world = world;
        this.blockX = blockX;
        this.blockY = blockY;
        this.blockZ = blockZ;
        this.material = material;
    }

    @Override
    public void compute() {
        int y = blockY;
        if (y == 0) {
            material = Material.BEDROCK;
        } else if (y < 3) {
            material = Material.DIRT;
        } else if (y == 3) {
            material = Material.GRASS;
        }

        WorldServer worldServer = ((CraftWorld) this.world).getHandle();
        worldServer.setTypeId(blockX, blockY, blockZ, material.getId());
    }

}
