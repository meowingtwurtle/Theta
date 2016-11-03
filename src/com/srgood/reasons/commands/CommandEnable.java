package com.srgood.reasons.commands;

import com.srgood.reasons.BotMain;
import com.srgood.reasons.utils.CommandUtils;
import com.srgood.reasons.utils.config.ConfigUtils;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;

public class CommandEnable implements Command {
    private static final String HELP = "Enables a command on this server. Use: '" + BotMain.prefix + "enable <command>'";

    @Override
    public boolean called(String[] args, GuildMessageReceivedEvent event) {
        return true;
    }

    @Override
    public void action(String[] args, GuildMessageReceivedEvent event) {
        if (args.length > 0) {
            Command mCommand = CommandUtils.getCommandByName(args[0]);
            try {
                CommandUtils.setCommandEnabled(event.getGuild(), mCommand, true);
                event.getChannel().sendMessage(String.format("Command %s enabled.", CommandUtils.getNameFromCommand(mCommand)));
            } catch (IllegalArgumentException e) {
                event.getChannel().sendMessage(String.format("Cannot enable command %s.", CommandUtils.getNameFromCommand(mCommand)));
            }
        } else {
            event.getChannel().sendMessage("Please specify a command to enable.");
        }
    }

    @Override
    public String help() {
        // TODO Auto-generated method stub
        return HELP;
    }

    @Override
    public void executed(boolean success, GuildMessageReceivedEvent event) {
    }

    @Override
    public com.srgood.reasons.PermissionLevels permissionLevel(Guild guild) {
        return ConfigUtils.getCommandPermission(guild, this);
    }

    @Override
    public com.srgood.reasons.PermissionLevels defaultPermissionLevel() {
        return com.srgood.reasons.PermissionLevels.ADMINISTRATOR;
    }

    @Override
    public String[] names() {
        return new String[] { "enable" };
    }

}