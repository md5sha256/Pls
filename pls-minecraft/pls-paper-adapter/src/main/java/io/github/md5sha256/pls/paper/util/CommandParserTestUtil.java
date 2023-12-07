package io.github.md5sha256.pls.paper.util;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import io.github.md5sha256.pls.paper.CommandParser;
import io.github.md5sha256.pls.paper.argument.ArgumentTypeAdapters;
import io.github.md5sha256.pls.function.Function;
import io.github.md5sha256.pls.paper.argument.adapter.KnownArgumentTypes;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R2.CraftServer;
import org.bukkit.plugin.Plugin;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.jackson.JacksonConfigurationLoader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

public class CommandParserTestUtil {

    public static void dumpTestCommand() {
        RootCommandNode<CommandSourceStack> commandNode = new RootCommandNode<>();
        ArgumentCommandNode<CommandSourceStack, Integer> test =
                RequiredArgumentBuilder.<CommandSourceStack, Integer>argument("test",
                        IntegerArgumentType.integer()).build();
        commandNode.addChild(test);
        dumpAdaptedCommands(commandNode, System.out);
    }

    public static void dumpServerCommands(Plugin plugin, OutputStream outputStream) {
        plugin.getLogger().info("Starting test");
        CraftServer bukkitServer = (CraftServer) Bukkit.getServer();
        MinecraftServer minecraftServer = bukkitServer.getServer();
        Commands commands = minecraftServer.getCommands();
        RootCommandNode<CommandSourceStack> root = commands.getDispatcher().getRoot();
        plugin.getLogger().info("Detected " + root.getChildren().size() + "commands!");
        dumpAdaptedCommands(root, outputStream);
    }

    public static void dumpAdaptedCommands(RootCommandNode<CommandSourceStack> root, OutputStream outputStream) {
        CommandParser parser = new CommandParser(KnownArgumentTypes.defaultAdapters());
        List<Function> functions = parser.adaptRootFunction(root);
        try(Writer writer = new OutputStreamWriter(outputStream);
            BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            JacksonConfigurationLoader loader = JacksonConfigurationLoader.builder()
                    .sink(() -> bufferedWriter)
                    .build();
            ConfigurationNode node = loader.createNode();
            node.setList(Function.class, functions);
            loader.save(node);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

}
