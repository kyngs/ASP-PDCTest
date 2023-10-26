package xyz.kyngs.pdctest;

import com.infernalsuite.aswm.api.SlimePlugin;
import com.infernalsuite.aswm.api.exceptions.*;
import com.infernalsuite.aswm.api.world.SlimeWorld;
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

    private SlimeWorld slimeWorld;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        var container = slimeWorld.getPersistentDataContainer();
        switch (command.getName()) {
            case "setpdc" -> {
                container.set(new NamespacedKey(this, "test"), PersistentDataType.STRING, "test");
                sender.sendMessage("Set");
            }
            case "getpdc" -> {
                var pdc = container.get(new NamespacedKey(this, "test"), PersistentDataType.STRING);
                sender.sendMessage(pdc == null ? "null" : pdc);
            }
            case "removepdc" -> {
                container.remove(new NamespacedKey(this, "test"));
                sender.sendMessage("Removed");
            }
            case "dumppdc" -> {
                container.getKeys().forEach(key -> sender.sendMessage(key.toString() + ":" + container.get(key, PersistentDataType.STRING)));
                sender.sendMessage("End extra PDC, begin extra NBT");
                sender.sendMessage(slimeWorld.getExtraData().toString());
            }
        }

        return true;
    };

    @Override
    public void onEnable() {
        SlimePlugin plugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");

        var loader = plugin.getLoader("file");

        try {
            slimeWorld = plugin.loadWorld(loader, "pdctest", false, new SlimePropertyMap());
        } catch (UnknownWorldException e) {
            try {
                slimeWorld = plugin.createEmptyWorld(loader, "pdctest", false, new SlimePropertyMap());
            } catch (WorldAlreadyExistsException | IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (IOException | CorruptedWorldException | NewerFormatException | WorldLockedException e) {
            throw new RuntimeException(e);
        }

        try {
            plugin.loadWorld(slimeWorld);
        } catch (UnknownWorldException | WorldLockedException | IOException e) {
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
