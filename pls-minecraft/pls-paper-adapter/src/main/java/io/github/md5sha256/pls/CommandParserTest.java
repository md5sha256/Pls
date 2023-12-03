package io.github.md5sha256.pls;

import com.mojang.brigadier.tree.RootCommandNode;
import io.github.md5sha256.pls.function.Function;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R2.CraftServer;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class CommandParserTest {

    private final CommandParser parser;

    public CommandParserTest(CommandParser parser) {
        this.parser = parser;
    }

    public void test(Plugin plugin) {
        plugin.getLogger().info("Starting test");
        CraftServer bukkitServer = (CraftServer) Bukkit.getServer();
        MinecraftServer minecraftServer = bukkitServer.getServer();
        Commands commands = minecraftServer.getCommands();
        RootCommandNode<CommandSourceStack> root = commands.getDispatcher().getRoot();
        plugin.getLogger().info("Detected " + root.getChildren().size() + "commands!");
        List<Function> functions = parser.adaptCommand(root);
        plugin.getLogger().info("Parsed " + functions.size() + " commands!");
    }

}
