package xyz.kyngs.pdctest;

import com.infernalsuite.aswm.api.SlimePlugin;
import com.infernalsuite.aswm.api.exceptions.*;
import com.infernalsuite.aswm.api.world.properties.SlimePropertyMap;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public final class PDCTest extends JavaPlugin{

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        var world = Bukkit.getWorld("pdctest");

        switch (command.getName()) {
            case "setpdc" -> {
                world.getPersistentDataContainer().set(new NamespacedKey(this, "test"), PersistentDataType.STRING, "test");
                sender.sendMessage("Set");
            }
            case "getpdc" -> {
                var pdc = world.getPersistentDataContainer().get(new NamespacedKey(this, "test"), PersistentDataType.STRING);
                sender.sendMessage(pdc == null ? "null" : pdc);
            }
            case "removepdc" -> {
                world.getPersistentDataContainer().remove(new NamespacedKey(this, "test"));
                sender.sendMessage("Removed");
            }
            case "dumppdc" -> {
                world.getPersistentDataContainer().getKeys().forEach(key -> sender.sendMessage(key.toString() + ":" + world.getPersistentDataContainer().get(key, PersistentDataType.STRING)));
            }
        }

        return true;
    };

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
