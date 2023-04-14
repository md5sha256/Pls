package io.github.md5sha256.pls;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class DatapackCommand implements CommandExecutor {

    private final Endpoint endpoint;

    private final DatapackInstaller datapackHandler;

    public DatapackCommand(Endpoint endpoint, Plugin plugin) {
        this.endpoint = endpoint;
        this.datapackHandler = new DatapackInstaller(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Component.text("Please enter a sentence").color(NamedTextColor.RED));
            return false;
        }
        // Join the args into a single string (greedy arg)
        final String joined = String.join(" ", args);
        // Send the request async, then format the result, then print the result to the sender
        sender.sendMessage(Component.text("Requesting generated datapack. This might take a while.").color(NamedTextColor.YELLOW));
        this.endpoint.requestDatapack(joined, this.datapackHandler).thenAccept(result -> {
            if (result.error()) {
                sender.sendMessage(Component.text(result.errorData().errorMessage()).color(NamedTextColor.RED));
                return;
            }
            sender.sendMessage(Component.text("Datapack has been installed successfully, name: " + result.result()).color(NamedTextColor.GREEN));
        });
        return true;
    }
}
