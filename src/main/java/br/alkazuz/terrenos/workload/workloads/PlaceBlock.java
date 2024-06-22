package br.alkazuz.terrenos.workload.workloads;

import org.bukkit.Material;
import org.bukkit.World;

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
        if (blockY == 0) {
            world.getBlockAt(blockX, blockY, blockZ).setType(Material.BEDROCK);
        } else if (blockY < 3) {
            world.getBlockAt(blockX, blockY, blockZ).setType(Material.DIRT);
        } else if (blockY == 3) {
            world.getBlockAt(blockX, blockY, blockZ).setType(Material.GRASS);
        } else {
            world.getBlockAt(blockX, blockY, blockZ).setType(material);
        }
    }

}
