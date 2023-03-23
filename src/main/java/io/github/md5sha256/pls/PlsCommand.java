package io.github.md5sha256.pls;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;



public class PlsCommand implements CommandExecutor {

    private final RequestExecutor requestExecutor;
    private final Plugin plugin;


    public PlsCommand(RequestExecutor requestExecutor, Plugin plugin) {
        this.requestExecutor = requestExecutor;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Component.text("Please enter a sentence").color(NamedTextColor.RED));
            return false;
        }
        final String joined = String.join(" ", args);
        try {
            this.requestExecutor.sendRequest(joined).thenApply(this.requestExecutor::formatResponse)
                    .thenAccept(result -> printResult(sender, result));
        } catch (ConfigurateException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    private void printResult(CommandSender sender, RequestResult result) {
        Component message = Component.text("[OpenAi] ");
        String response = result.response();
        if (result.error()) {
            message = message.append(Component.text().content(response).color(NamedTextColor.RED).build());
        } else {
            message = message.append(Component.text().content(response).color(NamedTextColor.GREEN).build());
        }
        // run the commands on the main thread
        Bukkit.getScheduler().runTask(plugin, () -> {
            // split the response by newlines and run each command
            for (String command : response.split("\n")) {
                if (command.isBlank()) {
                    continue;
                }
                Bukkit.dispatchCommand(sender, command.substring(1));
            }
        });
        sender.sendMessage(message);
    }

    private void printRawResult(CommandSender sender, String message) {
        sender.sendMessage(message);
    }

}
