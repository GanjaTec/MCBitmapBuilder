package de.jml.bitmapbuilder;

import de.jml.external.util.Time;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class CommandManager implements CommandExecutor, TabCompleter {

    private static final Map<Player, Builder> openBuilders = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        String name = cmd.getName();
        if (!name.equalsIgnoreCase("bitmapbuilder"))
            return false;
        if (sender instanceof Player player) {
            if (args.length == 0) {
                showHelp(player);
                return false;
            }
            String arg = args[0];
            switch (arg) {
                case "open" -> {
                    if (args.length < 2) {
                        player.sendMessage("§cFilename required: /bb build <filename>");
                        return false;
                    }
                    String filename = args[1];
                    if (!filename.endsWith(".bmp"))
                        filename += ".bmp";
                    try {
                        player.sendMessage("§eOpening Bitmap '" + filename + "'...");
                        Builder builder = Builder.create()
                                .location(player.getLocation())
                                .file(filename)
                                .setUp();
                        openBuilders.put(player, builder);
                        player.sendMessage("§aBitmap '" + filename + "' is now open!");
                        return true;
                    } catch (IOException e) {
                        player.sendMessage(e instanceof FileNotFoundException
                                ? "§cCould not find the Bitmap file, make sure you used the right file ending (.bmp)"
                                : "§cCould not build the Bitmap, please contact an admin!");
                        BitmapBuilder.log.warning(e.getMessage());
                    }
                }
                case "build" -> {
                    Builder builder = openBuilders.get(player);
                    if (builder != null) {
                        player.sendMessage("§eBuilding Bitmap...");
                        long startedAt = Time.nowMillis();
                        try {
                            builder.build(1, () -> {
                                player.sendMessage("§aBitmap build in " + Time.elapsedSecs(startedAt) + " seconds!");
                                openBuilders.remove(player);
                            });
                            return true;
                        } catch (IOException e) {
                            player.sendMessage("§cCould not set Bitmap blocks!", "§7" + e.getMessage());
                        }
                    }
                }
                case "cancel" -> {
                    Bukkit.getScheduler().cancelTasks(BitmapBuilder.PLUGIN);
                    openBuilders.clear();
                    player.sendMessage("§cAll building-tasks cancelled successfully!");
                }
            }
        }
        return false;
    }

    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        int len = args.length;
        return switch (len) {
            case 1 -> List.of("open", "build");
            case 2 -> {
                String[] files = new File(BitmapBuilder.BMP_FOLDER_NAME).list();
                yield args[0].equalsIgnoreCase("open")
                        ? files != null ? Arrays.asList(files)
                        : List.of("<filename>") : null;
            }
            default -> null;
        };
    }

    private void showHelp(@NotNull Player player) {
        player.sendMessage("§8---------------------------------------------------",
                "§e---   §bHelp for BitmapBuilder   §e---",
                "§aCommands:",
                "§7  - §abb open <§7filename§a> §8-> §7Opens a Bitmap file",
                "§7  - §abb build §8-> §7Builds an opened Bitmap",
                "§8---------------------------------------------------"
        );
    }

}
