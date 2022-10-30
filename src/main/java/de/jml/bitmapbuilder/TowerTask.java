package de.jml.bitmapbuilder;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class TowerTask implements Runnable {

    @NotNull private final World world;
    @NotNull private final Material mat;
    private final int x, y, z;
    private final int height;

    public TowerTask(@NotNull World world, int x, int y, int z, int height, float heightFactor) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.mat = getMaterial(height, heightFactor);
        this.height = (int) (height * heightFactor);
    }

    @NotNull
    private static Material getMaterial(int height, float heightFactor) {
        height *= heightFactor;
        return height == 0
                ? Material.WHITE_WOOL : height < 40 * heightFactor
                ? Material.GREEN_WOOL : height < 90 * heightFactor
                ? Material.YELLOW_WOOL : height < 170 * heightFactor
                ? Material.ORANGE_WOOL : height < 230 * heightFactor
                ? Material.RED_WOOL : Material.REDSTONE_BLOCK;
    }

    @Override
    public void run() {
        for (int h = 0; h < height; h++) {
            world.getBlockAt(x, y + h, z).setType(mat);
        }
    }



}
