package br.alkazuz.terrenos.workload;

import br.alkazuz.terrenos.config.Settings;
import br.alkazuz.terrenos.workload.workloads.PlaceBlock;
import br.alkazuz.terrenos.workload.workloads.WorkLoad;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayDeque;
import java.util.Deque;

public class FenceTerrenoWorkload implements Runnable {

    private static final double MAX_MILLIS_PER_TICK = 3.0;
    private static final double MAX_NANOS_PER_TICK = MAX_MILLIS_PER_TICK * 1E6;

    private final Deque<WorkLoad> workloads = new ArrayDeque<>();
    public boolean finished = false;
    private BukkitTask task;

    public FenceTerrenoWorkload(Location location, Settings.Size size, BukkitTask task) {
        this.task = task;

        int x = (int) Math.floor(location.getX());
        int z = (int) Math.floor(location.getZ());
        int y = 4;
        for (int i = x - (size.getSize() / 2); i < x + (size.getSize() / 2); i++) {
            for (int j = z - (size.getSize() / 2); j < z + (size.getSize() / 2); j++) {
                if (i != x - (size.getSize() / 2) && i != x + (size.getSize() / 2) - 1
                        && j != z - (size.getSize() / 2) && j != z + (size.getSize() / 2) - 1) {
                    continue;
                }
                workloads.add(
                        new PlaceBlock(location.getWorld(), i, y, j,
                                Material.FENCE));
            }
        }
    }

    @Override
    public void run() {
        long start = System.nanoTime();
        while (!workloads.isEmpty() && System.nanoTime() - start < MAX_NANOS_PER_TICK) {
            WorkLoad workLoad = workloads.poll();
            assert workLoad != null;
            workLoad.compute();
        }

        if (workloads.isEmpty()) {
            finished = true;
            task.cancel();
        }
    }

    public void setTask(BukkitTask task) {
        this.task = task;
    }
}
