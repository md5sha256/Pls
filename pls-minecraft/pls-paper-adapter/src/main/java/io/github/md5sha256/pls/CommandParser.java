package io.github.md5sha256.pls;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import io.github.md5sha256.pls.function.Function;
import io.github.md5sha256.pls.function.FunctionParameter;
import io.github.md5sha256.pls.function.FunctionParameters;
import io.leangen.geantyref.GenericTypeReflector;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CommandParser {

    private static final int OPERATOR_PERMISSION_LEVEL = 4;

    private static final CommandSourceStack TEST_SOURCE = createTestSource();

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

    private final ArgumentTypeAdapters adapters;

    public CommandParser(@NonNull ArgumentTypeAdapters adapters) {
        this.adapters = adapters;
    }


    private String guessDescription(CommandNode<?> commandNode) {
        return commandNode.getName() + ", " + commandNode.getUsageText();
    }

    public List<Function> adaptCommand(RootCommandNode<CommandSourceStack> root) {
        Collection<CommandNode<CommandSourceStack>> children = root.getChildren();
        List<Function> functions = new ArrayList<>(children.size());
        for (CommandNode<CommandSourceStack> child : children) {
            List<ParameterContext> parameterContexts = new LinkedList<>();
            adaptChildCommand(child, parameterContexts);
            Map<String, FunctionParameter> namedParameters = new LinkedHashMap<>(parameterContexts.size());
            List<String> requiredParams = new ArrayList<>();
            for (ParameterContext parameterContext : parameterContexts) {
                namedParameters.put(parameterContext.name(), parameterContext.parameter());
                if (parameterContext.required()) {
                    requiredParams.add(parameterContext.name());
                }
            }
            FunctionParameters parameters = new FunctionParameters(namedParameters);
            Function function = new Function(
                    child.getName(),
                    guessDescription(child),
                    parameters,
                    requiredParams.toArray(String[]::new)
            );
            functions.add(function);
        }
        return functions;
    }

    @SuppressWarnings("unchecked")
    private void adaptChildCommand(CommandNode<CommandSourceStack> root,
                                   List<ParameterContext> params) {
        boolean required = root.requirement.test(TEST_SOURCE);
        if (root instanceof LiteralCommandNode<CommandSourceStack> literalCommandNode) {
            FunctionParameter parameter = new FunctionParameter(
                    FunctionParameter.Type.STRING,
                    "subcommand: " + literalCommandNode.getLiteral(),
                    null
            );
            params.add(new ParameterContext(parameter, root.getName(), required));
        }
        if (root instanceof ArgumentCommandNode<CommandSourceStack, ?> argumentCommandNode) {
            ArgumentType<?> argumentType = argumentCommandNode.getType();
            @SuppressWarnings("rawtypes")
            ArgumentTypeAdapter adapter = this.adapters.getRawAdapter(argumentType.getClass())
                    .orElseGet(BasicArgumentTypeAdapter::new);
            FunctionParameter parameter = adapter.adaptArgumentType(argumentType, "", required);
            params.add(new ParameterContext(parameter, root.getName(), required));

        }
        for (CommandNode<CommandSourceStack> child : root.getChildren()) {
            adaptChildCommand(child, params);
        }
    }

}
