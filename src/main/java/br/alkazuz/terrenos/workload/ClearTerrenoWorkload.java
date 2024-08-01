package br.alkazuz.terrenos.workload;

import br.alkazuz.terrenos.object.PlayerTerrenoManager;
import br.alkazuz.terrenos.object.Terreno;
import br.alkazuz.terrenos.utils.TerrenoManager;
import br.alkazuz.terrenos.workload.workloads.PlaceBlock;
import br.alkazuz.terrenos.workload.workloads.WorkLoad;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayDeque;
import java.util.Deque;

public class ClearTerrenoWorkload implements Runnable {

    private static final double MAX_MILLIS_PER_TICK = 0.1;
    private static final double MAX_NANOS_PER_TICK = MAX_MILLIS_PER_TICK * 1E6;

    private final Deque<WorkLoad> workloads = new ArrayDeque<>();
    public boolean finished = false;
    private BukkitTask task;
    private final Terreno terreno;

    public ClearTerrenoWorkload(Terreno terreno, BukkitTask task) {
        this.task = task;
        this.terreno = terreno;
        Location initial = new Location(Bukkit.getWorld(terreno.getWorld()), terreno.getX1(), 0, terreno.getZ1());
        Location end = new Location(Bukkit.getWorld(terreno.getWorld()), terreno.getX2(), 256, terreno.getZ2());

        for (int x = initial.getBlockX(); x <= end.getBlockX(); x++) {
            for (int y = initial.getBlockY(); y <= end.getBlockY(); y++) {
                for (int z = initial.getBlockZ(); z <= end.getBlockZ(); z++) {
                    Location location = new Location(Bukkit.getWorld(terreno.getWorld()), x, y, z);

                    int currentBlock = location.getWorld().getBlockTypeIdAt(location);
                    if (currentBlock == 0) {
                        continue;
                    }
                    Material material;
                    if (y == 0) {
                        material = Material.BEDROCK;
                    } else if (y < 3) {
                        material = Material.DIRT;
                    } else if (y == 3) {
                        material = Material.GRASS;
                    } else {
                        material = Material.AIR;
                    }

                    workloads.add(
                            new PlaceBlock(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ(),
                                    material));
                }
            }
        }
    }

    @Override
    public void run() {
        long start = System.nanoTime();
        while (!workloads.isEmpty() && System.nanoTime() - start < MAX_NANOS_PER_TICK) {
            WorkLoad workLoad = workloads.poll();
            if (workLoad == null) {
                continue;
            }
            workLoad.compute();
        }

        if (workloads.isEmpty()) {
            finished = true;
            if (task != null) {
                terreno.delete();
                terreno.deleting = false;
                TerrenoManager.removeTerreno(terreno);
                PlayerTerrenoManager.removeTerreno(terreno);
                task.cancel();
                String owner = terreno.getOwner();
                Player player = Bukkit.getPlayer(owner);
                if (player != null) {
                    player.sendMessage("§aSeu terreno foi deletado com sucesso.");
                }
            }
        }
    }

    public void setTask(BukkitTask task) {
        this.task = task;
    }
}
