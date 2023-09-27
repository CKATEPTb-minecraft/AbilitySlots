package dev.ckateptb.minecraft.abilityslots.command.sender;

import dev.ckateptb.minecraft.abilityslots.message.MessageFormatter;
import lombok.experimental.Delegate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class AbilityCommandSender implements CommandSender {
    public static AbilityCommandSender of(CommandSender commandSender) {
        return new AbilityCommandSender(commandSender);
    }

    @Delegate(excludes = Exclude.class)
    protected final CommandSender handle_;

    private AbilityCommandSender(CommandSender handle_) {
        this.handle_ = handle_;
    }

    /**
     * Sends this sender a message
     *
     * @param message Message to be displayed
     * @see #sendMessage(net.kyori.adventure.text.Component)
     */
    @Override
    public void sendMessage(@NotNull String message) {
        this.handle_.sendMessage(MessageFormatter.toComponent(message));
    }

    /**
     * Sends this sender multiple messages
     *
     * @param messages An array of messages to be displayed
     * @see #sendMessage(net.kyori.adventure.text.Component)
     */
    @Override
    public void sendMessage(@NotNull String... messages) {
        if (messages.length > 1) {
            List<String> list = new ArrayList<>(Arrays.asList(messages));
            this.handle_.sendMessage(MessageFormatter.toComponent(list.remove(0), list.toArray(String[]::new)));
        }
    }

    public Optional<Player> asPlayer() {
        if (this.handle_ instanceof Player player) {
            return Optional.of(player);
        } else return Optional.empty();
    }

    public CommandSender getHandle() {
        return this.handle_;
    }

    private interface Exclude {
        void sendMessage(String message);

        void sendMessage(String... messages);
    }
}
