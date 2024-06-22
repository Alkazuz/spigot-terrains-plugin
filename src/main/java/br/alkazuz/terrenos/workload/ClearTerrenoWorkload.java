package br.alkazuz.terrenos.workload;

import br.alkazuz.terrenos.object.Terreno;
import br.alkazuz.terrenos.workload.workloads.PlaceBlock;
import br.alkazuz.terrenos.workload.workloads.WorkLoad;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayDeque;
import java.util.Deque;

public class ClearTerrenoWorkload implements Runnable {

    private static final double MAX_MILLIS_PER_TICK = 3.0;
    private static final double MAX_NANOS_PER_TICK = MAX_MILLIS_PER_TICK * 1E6;

    private final Deque<WorkLoad> workloads = new ArrayDeque<>();
    public boolean finished = false;
    private BukkitTask task;

    public ClearTerrenoWorkload(Terreno terreno, BukkitTask task) {
        this.task = task;
        Location initial = new Location(Bukkit.getWorld(terreno.getWorld()), terreno.getX1(), 0, terreno.getZ1());
        Location end = new Location(Bukkit.getWorld(terreno.getWorld()), terreno.getX2(), 256, terreno.getZ2());

        for (int x = initial.getBlockX(); x <= end.getBlockX(); x++) {
            for (int y = initial.getBlockY(); y <= end.getBlockY(); y++) {
                for (int z = initial.getBlockZ(); z <= end.getBlockZ(); z++) {
                    Location location = new Location(Bukkit.getWorld(terreno.getWorld()), x, y, z);
                    workloads.add(new PlaceBlock(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), Material.AIR));
                }
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
            if (task != null) {
                task.cancel();
            }
        }
    }

    public void setTask(BukkitTask task) {
        this.task = task;
    }
}
