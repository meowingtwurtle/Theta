package com.srgood.dbot.utils.config;

import com.srgood.dbot.PermissionLevels;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Role;
import net.dv8tion.jda.utils.SimpleLog;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.*;
import java.util.stream.Collectors;

class ConfigGuildUtils {
    static Map<String, Element> servers = new HashMap<>();

    static Element getGuildNode(Guild guild) {
        return servers.get(guild.getId());
    }

    static Set<Role> getGuildRolesFromPermissionName(Guild guild, String permissionName) {
        return getGuildRoleIDsFromPermissionName(guild, permissionName).stream().map(guild::getRoleById).collect(Collectors.toSet());
    }

    static Set<String> getGuildRoleIDsFromPermission(Guild guild, PermissionLevels permissionLevel) {
        return getGuildRoleIDsFromPermissionName(guild, permissionLevel.getXMLName());
    }

    static Set<String> getGuildRoleIDs(Guild guild) {
        return getRoleNodeListFromGuild(guild).stream().map(Node::getTextContent).collect(Collectors.toSet());
    }

    static Set<String> getGuildRoleIDsFromPermissionName(Guild guild, String permissionName) {
        return getRoleNodeListFromGuild(guild).stream()
                .filter(n -> n instanceof Element)
                .map(n -> (Element) n)
                .filter(elem -> elem.getAttribute("name").equals(permissionName))
                .map(Node::getTextContent)
                .collect(Collectors.toSet());
    }

    static Element getRolesElement(Guild guild) {
        return ConfigBasicUtils.getFirstSubElement(getGuildNode(guild), "roles");
    }

    static void initGuildConfigIfNotExists(Guild guild) {
        if (getGuildNode(guild) == null) {
            initGuildConfig(guild);
        }
    }

    static void initGuildConfig(Guild guild) {
        Element elementServer = ConfigBasicUtils.getDocument().createElement("server");

        Element elementServers = ConfigBasicUtils.getFirstSubElement(ConfigBasicUtils.getDocument().getDocumentElement(), "servers");

        Attr attrID = ConfigBasicUtils.getDocument().createAttribute("id");
        attrID.setValue(guild.getId());
        elementServer.setAttributeNode(attrID);

        Element elementDefault = ConfigBasicUtils.getFirstSubElement(ConfigBasicUtils.getDocument().getDocumentElement(), "default");

        ConfigBasicUtils.nodeListToList(elementDefault.getChildNodes()).stream().filter(n -> n instanceof Element).forEach(n -> {
            Element elem = (Element) n;
            elementServer.appendChild(elem.cloneNode(true));
        });

        elementServers.appendChild(elementServer);
        servers.put(guild.getId(), elementServer);

        Element elementRoleContainer = ConfigBasicUtils.getDocument().createElement("roles");

        elementServer.appendChild(elementRoleContainer);
    }

    static List<Node> getRoleNodeListFromGuild(Guild guild) {
        Element rolesElem = getRolesElement(guild);

        return ConfigBasicUtils.nodeListToList(rolesElem.getElementsByTagName("role"));
    }

    static void deleteGuildConfig(Guild guild) {
        getGuildNode(guild).getParentNode().removeChild(getGuildNode(guild));
    }

    static String getGuildPrefix(Guild guild) {
        if (!servers.containsKey(guild.getId())) {
            SimpleLog.getLog("Reasons").info("initializing Guild from message");
            com.srgood.dbot.utils.GuildUtils.initGuild(guild);
        }
        return getGuildPrefixNode(guild).getTextContent();
    }

    static void setGuildPrefix(Guild guild, String newPrefix) {
        getGuildPrefixNode(guild).setTextContent(newPrefix);
    }

    static Node getGuildPrefixNode(Guild guild) {
        return ConfigBasicUtils.getFirstSubElement(getGuildNode(guild), "prefix");
    }

    static PermissionLevels roleToPermission(Guild guild, Role role) {
        PermissionLevels permission = null;
        if (role == null) {
            return null;
        }

        List<Node> roleNodeList = getRoleNodeListFromGuild(guild);

        String roleID = role.getId();

        for (Node n : roleNodeList) {
            Element roleElem = (Element) n;
            String roleXMLName = roleElem.getAttribute("name");

            if (!roleID.equals(roleElem.getTextContent())) {
                continue;
            }

            for (PermissionLevels permLevel : PermissionLevels.values()) {
                if (permLevel.getLevel() >= (permission == null ? PermissionLevels.STANDARD : permission).getLevel() && permLevel.getXMLName().equals(roleXMLName)) {
                    permission = permLevel;
                }
            }
        }

        return permission;
    }

    static boolean guildHasRoleForPermission(Guild guild, PermissionLevels roleLevel) {

        List<Node> roleElementList = getRoleNodeListFromGuild(guild);

        for (Node n : roleElementList) {
            Element roleElem = (Element) n;
            if (roleElem.getAttribute("name").equals(roleLevel.getXMLName())) {
                return true;
            }
        }
        return false;
    }

    static void registerRoleConfig(Guild guild, Role role, PermissionLevels roleLevel) {
        Element elementRoles = ConfigBasicUtils.getFirstSubElement(getGuildNode(guild), "roles");

        Element elementRole = ConfigBasicUtils.getDocument().createElement("role");
        Attr roleAttr = ConfigBasicUtils.getDocument().createAttribute("name");
        roleAttr.setValue(roleLevel.getXMLName());
        elementRole.setAttributeNode(roleAttr);
        elementRole.setTextContent(role.getId());

        elementRoles.appendChild(elementRole);
    }

    static void deregisterRoleConfig(Guild guild, String roleID) {
        Element elementRole = null;

        List<Node> roleNodeList = getRoleNodeListFromGuild(guild);
        for (Node n : roleNodeList) {
            Element elem = (Element) n;
            String textContent = elem.getTextContent();
            if (textContent.equals(roleID)) {
                elementRole = elem;
                break;
            }
        }
        if (elementRole == null) {
            return;
        }
        elementRole.getParentNode().removeChild(elementRole);
    }
}
