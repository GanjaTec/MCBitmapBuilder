package de.jml.bitmapbuilder;

import de.jml.external.util.Bitmap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Builder {

    private static Builder ref; // = this

    @Nullable private World world;
    @Nullable private Bitmap bitmap;
    @Nullable private Location loc;
    @NotNull private final Queue<TowerTask> towerTasks;
    @NotNull private final MappedQueue<Location, Material> locations;
    private boolean ready;

    private Builder() {
        world = null;
        bitmap = null;
        loc = null;
        towerTasks = new ConcurrentLinkedQueue<>();
        locations = new MappedQueue<>(true);
        ready = false;
    }

    public static Builder create() {
        return ref = new Builder();
    }

    private static int toUnsignedInt(byte b) {
        return b + 128;
    }

    private Builder world(@NotNull World world) {
        this.world = world;
        return ref;
    }

    public Builder file(@NotNull String filename) throws IOException {
        File file = new File(BitmapBuilder.BMP_FOLDER_NAME + filename);
        if (!file.exists())
            throw new FileNotFoundException("Bitmap file (.bmp) not found!");
        // FIXME: 29.10.2022 ERROR here! File not found!
        this.bitmap = Bitmap.fromImage(file);
        return ref;
    }

    public Builder location(@NotNull Location location) {
        this.loc = location;
        return world(location.getWorld());
    }

    public Builder setUp() throws IOException {
        if (world == null || bitmap == null || loc == null)
            throw new IOException("World, Location or File parameter not filled!");

        byte[][] bmp = bitmap.getBitmap();
        int width = bmp.length,
            depth = bmp[0].length,
            x = loc.getBlockX(),
            y = loc.getBlockY(),
            z = loc.getBlockZ();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < depth; j++) {
                towerTasks.add(new TowerTask(world, x + i, y, z + j, toUnsignedInt(bmp[i][j]), 0.5f));
            }
        }
        return ref;
    }

    public void build(int tickPeriod, @NotNull Runnable onFinish) throws IOException {
        if (world == null || bitmap == null || loc == null)
            throw new IOException("World, Location or File parameter not filled!");
        if (towerTasks.isEmpty())
            throw new IOException("No locations to build at, make sure you called the setUp() method before!");

        BukkitScheduler sched = Bukkit.getScheduler();

        Runnable nextTower = () -> {
            if (towerTasks.isEmpty()) {
                sched.cancelTasks(BitmapBuilder.PLUGIN);
                onFinish.run();
                return;
            }
            TowerTask next = towerTasks.poll();
            if (next != null) {
                next.run();
            }
        };

        sched.scheduleSyncRepeatingTask(BitmapBuilder.PLUGIN, nextTower, 0, tickPeriod);

        ready = true;
        ref = null;
    }

    public boolean isReady() {
        return ready;
    }
}
