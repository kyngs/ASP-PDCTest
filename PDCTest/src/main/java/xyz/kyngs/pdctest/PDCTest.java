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
import org.bukkit.entity.Player;
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
                if (sender instanceof Player player) {
                    player.getPersistentDataContainer().set(new NamespacedKey(this, "playertest"), PersistentDataType.STRING, "This is a test tag in a complex tag");
                    container.set(new NamespacedKey(this, "playertest"), PersistentDataType.TAG_CONTAINER, player.getPersistentDataContainer());
                }
                sender.sendMessage("Set");
            }
            case "getpdc" -> {
                var pdc = container.get(new NamespacedKey(this, "test"), PersistentDataType.STRING);
                sender.sendMessage(pdc == null ? "null" : pdc);
                var playerPDC = container.get(new NamespacedKey(this, "playertest"), PersistentDataType.TAG_CONTAINER);
                sender.sendMessage(playerPDC == null ? "null" : playerPDC.toString());
            }
            case "removepdc" -> {
                container.remove(new NamespacedKey(this, "test"));
                container.remove(new NamespacedKey(this, "playertest"));
                sender.sendMessage("Removed");
            }
            case "dumppdc" -> {
                sender.sendMessage("Begin extra NBT");
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
