package io.github.md5sha256.pls;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

@MethodsReturnNonnullByDefault
public class NullCommandSource implements CommandSource {

    public static final CommandSource NULL = new NullCommandSource();

    private NullCommandSource() {}

    @Override
    public void sendSystemMessage(Component message) {

    }

    @Override
    public boolean acceptsSuccess() {
        return false;
    }

    @Override
    public boolean acceptsFailure() {
        return false;
    }

    @Override
    public boolean shouldInformAdmins() {
        return false;
    }

    @Override
    public CommandSender getBukkitSender(@NonNull CommandSourceStack wrapper) {
        return new NullCommandSender();
    }
}
