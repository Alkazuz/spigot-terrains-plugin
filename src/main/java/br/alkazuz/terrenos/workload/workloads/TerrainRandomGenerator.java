package br.alkazuz.terrenos.workload.workloads;

import org.bukkit.entity.Player;

public class TerrainRandomGenerator implements WorkLoad {
    private static final int MAX_TERRAIN_SIZE = 10000;
    private static final int STEP = 20;
    private final Player player;
    private final int size;
    private int currentStep = 0;
    private int x = 0, z = 0;

    public TerrainRandomGenerator(Player player, int size) {
        this.player = player;
        this.size = size;
    }

    public Player getPlayer() {
        return this.player;
    }

    public int getSize() {
        return this.size;
    }

    @Override
    public void compute() {
        if (currentStep <= MAX_TERRAIN_SIZE) {
            for (int i = -1; i <= 1; i++) {
                int x = currentStep * i;
                int z = currentStep * i;


            }

            currentStep += STEP;
        }
    }
}
