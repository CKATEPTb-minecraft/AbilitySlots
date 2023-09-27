package dev.ckateptb.minecraft.abilityslots.command.sender;

import dev.ckateptb.minecraft.abilityslots.message.MessageFormatter;
import ink.glowing.text.InkyMessage;
import lombok.experimental.Delegate;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        this.handle_.sendMessage(MessageFormatter.toComponent(ChatColor.translateAlternateColorCodes('&', message)));
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
            List<String> list = Arrays.stream(messages)
                    .map(message -> ChatColor.translateAlternateColorCodes('&', message))
                    .collect(Collectors.toList());
            Component component = MessageFormatter.toComponent(list.remove(0), list.toArray(String[]::new));
            InkyMessage serializer = InkyMessage.inkyMessage();
            String result = ChatColor.translateAlternateColorCodes('&', serializer.serialize(component)
                    .replace("\\\\(", "(")
                    .replace("\\\\)", ")"));
            this.handle_.sendMessage(serializer.deserialize(result));
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
