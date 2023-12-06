package io.github.md5sha256.pls;


import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import io.github.md5sha256.pls.paper.CommandParser;
import io.github.md5sha256.pls.paper.argument.ArgumentTypeAdapters;
import io.github.md5sha256.pls.function.Function;
import io.github.md5sha256.pls.function.FunctionParameter;
import io.github.md5sha256.pls.function.FunctionParameters;
import net.minecraft.commands.CommandSourceStack;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class TestCommandParser {


    @Test
    void testCommandParser() {
        CommandParser parser = new CommandParser(new ArgumentTypeAdapters());
        RootCommandNode<CommandSourceStack> root = new RootCommandNode<>();
        LiteralCommandNode<CommandSourceStack> testCommandRoot = LiteralArgumentBuilder.<CommandSourceStack>literal(
                "test").build();
        CommandNode<CommandSourceStack> testCommandParam =
                RequiredArgumentBuilder.<CommandSourceStack, Integer>argument("size",
                        IntegerArgumentType.integer()).build();
        testCommandRoot.addChild(testCommandParam);
        root.addChild(testCommandRoot);
        List<Function> parsed = parser.adaptRootFunction(root);
        Assertions.assertEquals(1, parsed.size());
        FunctionParameter testParam = new FunctionParameter(FunctionParameter.Type.INTEGER,
                "<size>",
                null);
        Function equivalentFunction = new Function("test", "test <size>", new FunctionParameters(
                Map.of("size", testParam)), new String[]{"size"});
        Assertions.assertEquals(equivalentFunction, parsed.get(0));
    }
}
