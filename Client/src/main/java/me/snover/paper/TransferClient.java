package me.snover.paper;

import me.snover.paper.command.CommandTransfer;
import me.snover.paper.config.CompositeTransferConfiguration;
import me.snover.paper.event.Events;
import me.snover.paper.pointer.CoordinateContainer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

//TODO List of things to implements in version 1.1.3
//TODO Either implement serialization for locations and save them or associate coordinate sets with worlds.
public class TransferClient extends JavaPlugin {

    private static JavaPlugin plugin;
    private static CompositeTransferConfiguration config;

    public TransferClient() {
        ConfigurationSerialization.registerClass(CoordinateContainer.class);
    }

    @Override
    public void onEnable() {
        plugin = this;
        config = new CompositeTransferConfiguration(this);
        config.loadResources(true, true);

        getServer().getMessenger().registerOutgoingPluginChannel(this, "transfers:main");
        if(getServer().getMessenger().isOutgoingChannelRegistered(this, "transfers:main")) getLogger().info("Channel registered successfully");
        else getLogger().severe("Channel failed to register!");

        getServer().getPluginManager().registerEvents(new Events(), this);

        CommandTransfer commandTransfer = new CommandTransfer(config);
        if(getServer().getCommandMap().register("transfer", "", commandTransfer)) getLogger().info("Transfer command registered successfully");
        else getLogger().severe("Transfer command failed to register!");


        getLogger().warning("This plugin does not offer compatibility for other worlds besides the normal vanilla overworld! Changes made to the server's normal world functionality will break this plugin! Compatibility will be coming in the future.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Saving resources...");
        config.saveResources(true, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> tabList = new ArrayList<>();
        //TODO Finish Tab Suggestions
        if(command.getName().equalsIgnoreCase("transfer")) {
            switch (args[0]) {
                case "yes":
                    if(args[1].equalsIgnoreCase("register")) {

                }
            }
        }
        return null;
    }

    /**
     * Get the current instance of this plugin
     * @return Returns the current instance of this plugin being used by the server
     */
    public static Plugin getPlugin() {
        return plugin;
    }
    public static CompositeTransferConfiguration getCompositeConfig() {
        return config;
    }
}