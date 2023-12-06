package io.github.md5sha256.pls.paper;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import io.github.md5sha256.pls.paper.argument.ArgumentTypeAdapter;
import io.github.md5sha256.pls.paper.argument.ArgumentTypeAdapters;
import io.github.md5sha256.pls.paper.argument.BasicArgumentTypeAdapter;
import io.github.md5sha256.pls.function.Function;
import io.github.md5sha256.pls.function.FunctionParameter;
import io.github.md5sha256.pls.function.FunctionParameters;
import io.github.md5sha256.pls.paper.util.NullCommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class CommandParser {

    private static final int OPERATOR_PERMISSION_LEVEL = 4;

    private static final CommandSourceStack TEST_SOURCE = createTestSource();

    private final ArgumentTypeAdapters adapters;

    public CommandParser(ArgumentTypeAdapters adapters) {
        this.adapters = adapters;
    }

    private static CommandSourceStack createTestSource() {
        return new CommandSourceStack(
                NullCommandSource.NULL,
                Vec3.ZERO,
                Vec2.ZERO,
                null,
                OPERATOR_PERMISSION_LEVEL,
                "",
                Component.empty(),
                null,
                null
        );
    }

    public <T> List<List<CommandNode<T>>> parseCommandBranches(CommandNode<T> root) {
        List<List<CommandNode<T>>> result = new ArrayList<>();
        dfs(root, new ArrayList<>(), result);
        return result;
    }

    private <T> void dfs(CommandNode<T> node, List<CommandNode<T>> currentList, List<List<CommandNode<T>>> result) {
        currentList.add(node);

        if (node.getChildren().isEmpty()) {
            result.add(new ArrayList<>(currentList));
        } else {
            for (CommandNode<T> child : node.getChildren()) {
                dfs(child, currentList, result);
            }
        }

        currentList.remove(currentList.size() - 1);
    }

    public List<Function> adaptRootFunction(RootCommandNode<CommandSourceStack> root) {
        List<Function> functions = new ArrayList<>();
        for (CommandNode<CommandSourceStack> child : root.getChildren()) {
            functions.addAll(adaptFunction(root.getName(), child));
        }
        return functions;
    }

    private List<Function> adaptFunction(String name, CommandNode<CommandSourceStack> root) {
        List<List<CommandNode<CommandSourceStack>>> commandBranches = parseCommandBranches(root);
        List<Function> functions = new LinkedList<>();
        commandBranches.forEach(rawFunction -> functions.addAll(adaptFunction(name, rawFunction)));
        return functions;
    }

    private List<Function> adaptFunction(String name, List<CommandNode<CommandSourceStack>> nodes) {

        List<ParameterContext> parameterContexts = new ArrayList<>(nodes.size());
        List<Function> subCommands = new LinkedList<>();
        StringJoiner label = new StringJoiner(" ");
        if (!name.isEmpty()) {
            label.add(name);
        }
        for (CommandNode<CommandSourceStack> node : nodes) {
            boolean required = node.requirement.test(TEST_SOURCE);
            if (node instanceof RootCommandNode<CommandSourceStack>
                    || node instanceof LiteralCommandNode<CommandSourceStack>
            ) {
                label.add(node.getName());
            } else if (node instanceof ArgumentCommandNode<?, ?> argumentNode) {
                ArgumentType<?> argumentType = argumentNode.getType();
                @SuppressWarnings("rawtypes")
                ArgumentTypeAdapter adapter = this.adapters.getRawAdapter(argumentType.getClass())
                        .orElseGet(BasicArgumentTypeAdapter::new);
                @SuppressWarnings("unchecked")
                FunctionParameter parameter = adapter.adaptArgumentType(argumentType,
                        node.getUsageText(),
                        required
                );
                parameterContexts.add(new ParameterContext(parameter, node.getName(), required));
            } else {
                throw new IllegalStateException("Unsupported node type: " + node.getClass());
            }
        }
        subCommands.add(createFunction(label.toString(), parameterContexts));
        return subCommands;
    }

    private Function createFunction(String commandName, List<ParameterContext> parameterContexts) {
        Map<String, FunctionParameter> namedParameters = new LinkedHashMap<>(parameterContexts.size());
        List<String> requiredParams = new ArrayList<>(parameterContexts.size());
        for (ParameterContext parameterContext : parameterContexts) {
            namedParameters.put(parameterContext.name(), parameterContext.parameter());
            if (parameterContext.required()) {
                requiredParams.add(parameterContext.name());
            }
        }
        StringJoiner desc = new StringJoiner(" ");
        desc.add(commandName);
        parameterContexts.forEach(context -> desc.add(context.parameter().description()));
        FunctionParameters parameters = new FunctionParameters(namedParameters);
        return new Function(
                commandName,
                desc.toString(),
                parameters,
                requiredParams.toArray(String[]::new)
        );
    }


}
