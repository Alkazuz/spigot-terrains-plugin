package br.alkazuz.terrenos.task;

import br.alkazuz.terrenos.Main;
import br.alkazuz.terrenos.object.Terreno;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class TerrainStorageTask implements Runnable {

    private static final List<Terreno> toSave = new ArrayList<>();

    @Override
    public void run() {
        if (toSave.isEmpty()) return;
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
            List<Terreno> toSaveNow = new ArrayList<>(toSave);
            toSave.clear();
            toSaveNow.forEach(Terreno::saveStorageSQL);
        });
    }

    public static void save(Terreno terreno) {
        if (!toSave.contains(terreno))
            toSave.add(terreno);
    }

    public static List<Terreno> getToSave() {
        return toSave;
    }
}
