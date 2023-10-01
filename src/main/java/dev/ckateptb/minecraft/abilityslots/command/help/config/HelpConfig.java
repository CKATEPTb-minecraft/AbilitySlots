package dev.ckateptb.minecraft.abilityslots.command.help.config;

import lombok.Getter;

import java.util.LinkedHashSet;

@Getter
public class HelpConfig {
    private LinkedHashSet<String> reply;
    private String authorHover = "&7Visit author github profile";

    public HelpConfig() {
        this.reply = new LinkedHashSet<>();
        this.reply.add("&6%abilityslots_plugin_name% &av.%abilityslots_plugin_version% " +
                "&cby %abilityslots_plugin_author%");
        this.reply.add("&8↳ &b&o&[" +
                "&[/abilityslots display&]&(hover:text &7Display available categories&) " +
                "&8&[[category]&]&(hover:text &7Display available abilities in specified category&)" +
                "&]&(click:suggest /abilityslots display&)");
        this.reply.add("&8↳ &b&o&[" +
                "&[/abilityslots bind &e<ability>&]&(hover:text &7Bind specified ability to current slot&) " +
                "&8&[[slot]&]&(hover:text &7Bind specified ability to specified slot (1-9)&)" +
                "&]&(click:suggest /abilityslots bind&)");
        this.reply.add("&8↳ &b&o&[" +
                "&[/abilityslots clear&]&(hover:text &7Unbind abilities from slots&) " +
                "&8&[[slot]&]&(hover:text &7Unbind ability from specified slot (1-9)&)" +
                "&]&(click:suggest /abilityslots clear&)");
        this.reply.add("&8↳ &b&o&[/abilityslots who &e<player>&]" +
                "&(hover:text &7Display ability board of specified player&)" +
                "&(click:suggest /abilityslots who&)");
        this.reply.add("&8↳ &b&o&[/abilityslots toggle&]&(hover:text &7Enable/Disable abilities&)" +
                "&(click:suggest /abilityslots toggle&)");
        this.reply.add("&8↳ &b&o&[/abilityslots preset list&]&(hover:text &7Display your presets&)" +
                "&(click:suggest /abilityslots preset list&)");
        this.reply.add("&8↳ &b&o&[/abilityslots preset create &e<name>&]" +
                "&(hover:text &7Create a new preset from the current ability board with the specified name&)" +
                "&(click:suggest /abilityslots preset create&)");
        this.reply.add("&8↳ &b&o&[/abilityslots preset delete &e<name>&]" +
                "&(hover:text &7Delete the created ability board preset with the specified name&)" +
                "&(click:suggest /abilityslots preset delete&)");
        this.reply.add("&8↳ &b&o&[/abilityslots preset bind &e<name>&]" +
                "&(hover:text &7Replace the current ability board with a preset with the specified name&)" +
                "&(click:suggest /abilityslots preset bind&)");
        this.reply.add("&8↳ &b&o&[&[/abilityslots reload&]" +
                "&(hover:text &7Reload the plugin configuration, addon's and their configuration, " +
                "services used by the plugin&) &8&[[target]&]" +
                "&(hover:text &7Reload calculations for the specified player&)" +
                "&]&(click:suggest /abilityslots reload&)");
    }
}
