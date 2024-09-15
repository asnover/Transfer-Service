package me.snover.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TransferTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args[0].equalsIgnoreCase("register")) return null;
        return List.of("register", "edit-mode", "listservers", "remserver", "showcoord", "remcoord", "test", "setspawn","toggleforcedspawn");
    }
}
