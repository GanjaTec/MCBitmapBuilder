package de.jml.bitmapbuilder;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public final class BitmapBuilder extends JavaPlugin {

    public static final Logger log = Bukkit.getLogger();
    public static BitmapBuilder PLUGIN;
    public static String BMP_FOLDER_NAME;

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onEnable() {
        // Plugin startup logic
        PLUGIN = this;

        File pluginsFolder = Bukkit.getPluginsFolder();
        BMP_FOLDER_NAME = pluginsFolder.getName() + "\\BitmapBuilder\\";
        File bmpFolder = new File(BMP_FOLDER_NAME);
        if (!bmpFolder.exists() && bmpFolder.mkdir())
            log.fine("Plugin folder ('/BitmapBuilder') created successfully!");

        PluginCommand bb = getCommand("bitmapbuilder");
        bb.setExecutor(new CommandManager());

        log.fine("BitmapBuilder-v1.0 initialized successfully!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic


        log.info("Disabled BitmapBuilder-v1.0 successfully!");
    }
}
