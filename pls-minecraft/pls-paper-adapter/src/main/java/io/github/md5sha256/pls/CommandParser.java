package io.github.md5sha256.pls;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R2.CraftServer;
import org.bukkit.plugin.Plugin;

import java.util.Collection;

public class CommandParser {

    public void test(Plugin plugin) {
        plugin.getLogger().info("Starting test");
        CraftServer bukkitServer = (CraftServer) Bukkit.getServer();
        MinecraftServer minecraftServer = bukkitServer.getServer();
        Commands commands = minecraftServer.getCommands();
        RootCommandNode<CommandSourceStack> root = commands.getDispatcher().getRoot();
        processCommand(root);
    }

    private void processCommand(CommandNode<CommandSourceStack> root) {
        if (root instanceof LiteralCommandNode<CommandSourceStack> literalCommandNode) {
            literalCommandNode.getLiteral();
        }
        if (root instanceof ArgumentCommandNode<CommandSourceStack,?> argumentCommandNode) {
            ArgumentType<?> argumentType = argumentCommandNode.getType();
        }
        for (CommandNode<CommandSourceStack> child : root.getChildren()) {
            processCommand(child);
        }

    }

}
