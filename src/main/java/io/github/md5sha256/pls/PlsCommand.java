package io.github.md5sha256.pls;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;


public class PlsCommand implements CommandExecutor {

    private final RequestExecutor requestExecutor;

    public PlsCommand(RequestExecutor requestExecutor) {
        this.requestExecutor = requestExecutor;
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
        if (result.error()) {
            message = message.append(Component.text().content(result.response()).color(NamedTextColor.RED).build());
        } else {
            message = message.append(Component.text().content(result.response()).color(NamedTextColor.GREEN).build());
        }
        Bukkit.getScheduler().runTask(plugin, () -> {
            Bukkit.dispatchCommand(sender, result.response());
        });
        sender.sendMessage(message);
    }

    private void printRawResult(CommandSender sender, String message) {
        sender.sendMessage(message);
    }

}
