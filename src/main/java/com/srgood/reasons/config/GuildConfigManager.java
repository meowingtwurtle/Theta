package com.srgood.reasons.config;

import com.srgood.reasons.commands.CommandDescriptor;
import net.dv8tion.jda.core.entities.Role;

public interface GuildConfigManager extends BasicConfigManager {
    String PREFIX_PROPERTY_NAME = "prefix";
    String DEFAULT_PREFIX = "#!";

    RoleConfigManager getRoleConfigManager(Role role);
    CommandConfigManager getCommandConfigManager(CommandDescriptor command);

    default String getPrefix() {
        return getProperty(PREFIX_PROPERTY_NAME, DEFAULT_PREFIX, true);
    }

    default void setPrefix(String prefix) {
        setProperty(PREFIX_PROPERTY_NAME, prefix);
    }

    void delete();
}