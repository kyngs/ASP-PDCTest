package xyz.kyngs.pdctest;

import com.infernalsuite.aswm.api.SlimePlugin;
import com.infernalsuite.aswm.api.exceptions.*;
import com.infernalsuite.aswm.api.world.properties.SlimePropertyMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.TileState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public final class PDCTest extends JavaPlugin {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            var world = Bukkit.getWorld("pdctest");
            var loc = new Location(world, 0, 0, 0);

            if (world.getBlockAt(loc).getType() != Material.CHEST) {
                world.setBlockData(loc, Bukkit.createBlockData(Material.CHEST));
            }

            var worldPdc = world.getPersistentDataContainer();
            var chunkPdc = player.getLocation().getChunk().getPersistentDataContainer();
            var playerPdc = player.getPersistentDataContainer();
            var state = (TileState) world.getBlockAt(loc).getState();
            var tileEntityPDC = state.getPersistentDataContainer();

            switch (command.getName()) {
                case "setpdc" -> {
                    worldPdc.set(new NamespacedKey(this, "test"), PersistentDataType.STRING, "test");
                    chunkPdc.set(new NamespacedKey(this, "test"), PersistentDataType.STRING, "test");
                    playerPdc.set(new NamespacedKey(this, "test"), PersistentDataType.STRING, "test");
                    tileEntityPDC.set(new NamespacedKey(this, "test"), PersistentDataType.STRING, "test");
                    state.update();
                    sender.sendMessage("Set");
                }
                case "getpdc" -> {
                    var pd = worldPdc.get(new NamespacedKey(this, "test"), PersistentDataType.STRING);
                    var chunkPd = chunkPdc.get(new NamespacedKey(this, "test"), PersistentDataType.STRING);
                    var playerPd = playerPdc.get(new NamespacedKey(this, "test"), PersistentDataType.STRING);
                    var tileEntityPd = tileEntityPDC.get(new NamespacedKey(this, "test"), PersistentDataType.STRING);
                    sender.sendMessage(pd == null ? "null" : pd);
                    sender.sendMessage(chunkPd == null ? "null" : chunkPd);
                    sender.sendMessage(playerPd == null ? "null" : playerPd);
                    sender.sendMessage(tileEntityPd == null ? "null" : tileEntityPd);
                }
                case "removepdc" -> {
                    worldPdc.remove(new NamespacedKey(this, "test"));
                    chunkPdc.remove(new NamespacedKey(this, "test"));
                    playerPdc.remove(new NamespacedKey(this, "test"));
                    tileEntityPDC.remove(new NamespacedKey(this, "test"));
                    sender.sendMessage("Removed");
                }
                case "dumppdc" -> {
                    worldPdc.getKeys().forEach(key -> sender.sendMessage(key.toString() + ":" + worldPdc.get(key, PersistentDataType.STRING)));
                    sender.sendMessage("chunk");
                    chunkPdc.getKeys().forEach(key -> sender.sendMessage(key.toString() + ":" + chunkPdc.get(key, PersistentDataType.STRING)));
                    sender.sendMessage("player");
                    playerPdc.getKeys().forEach(key -> sender.sendMessage(key.toString() + ":" + playerPdc.get(key, PersistentDataType.STRING)));
                    sender.sendMessage("tileentity");
                    tileEntityPDC.getKeys().forEach(key -> sender.sendMessage(key.toString() + ":" + tileEntityPDC.get(key, PersistentDataType.STRING)));
                }
            }
        }

        return true;
    }

    ;

    @Override
    public void onEnable() {
        SlimePlugin plugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");

        var loader = plugin.getLoader("file");

        try {
            plugin.loadWorld(loader, "pdctest", false, new SlimePropertyMap());
        } catch (UnknownWorldException e) {
            try {
                plugin.createEmptyWorld(loader, "pdctest", false, new SlimePropertyMap());
            } catch (WorldAlreadyExistsException | IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (IOException | CorruptedWorldException | NewerFormatException | WorldLockedException e) {
            throw new RuntimeException(e);
        }

        Bukkit.getPluginCommand("setpdc").setExecutor(this);
        Bukkit.getPluginCommand("getpdc").setExecutor(this);
        Bukkit.getPluginCommand("removepdc").setExecutor(this);
        Bukkit.getPluginCommand("dumppdc").setExecutor(this);

    }

    @Override
    public void onDisable() {
        Bukkit.getWorld("pdctest").save();
    }
}
