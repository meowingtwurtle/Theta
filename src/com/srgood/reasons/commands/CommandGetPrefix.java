package com.srgood.reasons.commands;

import com.srgood.reasons.BotMain;
import com.srgood.reasons.utils.config.ConfigUtils;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;

public class CommandGetPrefix implements Command {

    private static final String HELP = "Prints the current prefix for this server. Use: '" + BotMain.prefix + "getprefix'";

    @Override
    public boolean called(String[] args, GuildMessageReceivedEvent event) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void action(String[] args, GuildMessageReceivedEvent event) {
        event.getChannel().sendMessage("Prefix: " + ConfigUtils.getGuildPrefix(event.getGuild()));
    }

    @Override
    public String help() {
        // TODO Auto-generated method stub
        return HELP;
    }

    @Override
    public void executed(boolean success, GuildMessageReceivedEvent event) {
        // TODO Auto-generated method stub
    }

    @Override
    public com.srgood.reasons.PermissionLevels permissionLevel(Guild guild) {
        // TODO Auto-generated method stub
        return ConfigUtils.getCommandPermission(guild, this);
    }

    @Override
    public com.srgood.reasons.PermissionLevels defaultPermissionLevel() {
        // TODO Auto-generated method stub
        return com.srgood.reasons.PermissionLevels.STANDARD;
    }

    @Override
    public String[] names() {
        return new String[] {"getprefix"};
    }

}