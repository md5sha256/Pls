package io.github.md5sha256.pls;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;


public class PlsCommand implements CommandExecutor {

    private final Endpoint endpoint;
    private final Plugin plugin;
    public PlsCommand(Endpoint endpoint, Plugin plugin) {
        this.endpoint = endpoint;
        this.plugin = plugin;
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
        this.endpoint.requestCommand(joined).thenAccept(requestResult -> this.plugin.getServer().getScheduler().runTask(this.plugin, () -> printResult(sender, requestResult)));
        return true;
    }

    private void printResult(CommandSender sender, RequestResult<String> result) {
        Component message = Component.text("[OpenAi] ");
        if (result.error()) {
            // Print error messages in red
            message = message.append(Component.text().content(result.errorData().errorMessage()).color(NamedTextColor.RED).build());
            sender.sendMessage(message);
            return;
        }
        String response = result.result();
        // Print results in green
        message = message.append(Component.text().content(response).color(NamedTextColor.GREEN).build());
        sender.sendMessage(message);

        // run the commands on the main thread
        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
            // split the result by newlines and run each command
            for (String command : response.split("\n")) {
                // Don't dispatch blank commands
                if (command.isBlank()) {
                    continue;
                }
                this.plugin.getServer().dispatchCommand(sender, command.substring(1));
            }
        });
    }

    private void printRawResult(CommandSender sender, String message) {
        sender.sendMessage(message);
    }

}
