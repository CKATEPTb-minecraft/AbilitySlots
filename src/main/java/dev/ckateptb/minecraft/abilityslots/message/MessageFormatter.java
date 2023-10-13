package dev.ckateptb.minecraft.abilityslots.message;

import ink.glowing.text.InkyMessage;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.Validate;

public class MessageFormatter {
    private static final InkyMessage serializer = InkyMessage.inkyMessage();

    public static Component toComponent(String string, String... replacements) {
        Validate.isTrue(replacements.length % 2 == 0);
        Component deserialize = serializer.deserialize(string);
        for (int i = 0; i < replacements.length; i += 2) {
            int finalI = i;
            deserialize = deserialize.replaceText(builder -> {
                builder.matchLiteral(replacements[finalI]);
                builder.replacement(replacements[finalI + 1]);
            });
        }
        return deserialize;
    }
}
