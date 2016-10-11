package com.srgood.dbot.utils.config;

import com.srgood.dbot.PermissionLevels;
import com.srgood.dbot.commands.Command;
import com.srgood.dbot.utils.CommandUtils;
import com.srgood.dbot.utils.PermissionUtils;
import net.dv8tion.jda.entities.Guild;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

class ConfigCommandUtils {
    private static final Map<String, Function<String, Object>> requiredCommandSubElements = new HashMap<String, Function<String, Object>>() {
        private static final long serialVersionUID = -710068261487017415L;

        {
            put("permLevel", name -> CommandUtils.getCommandByName(name).defaultPermissionLevel().getLevel());
            put("isEnabled", name -> true);
        }
    };

    static Element getCommandElement(Guild guild, String commandName) {
        return getCommandElement(getCommandsElement(guild), commandName);
    }

    static Element getCommandElement(Guild guild, Command command) {
        return getCommandElement(guild, CommandUtils.getNameFromCommand(command));
    }

    static Element getCommandElement(Element commandsElement, String commandName) {
            List<Node> commandList = ConfigBasicUtils.nodeListToList(commandsElement.getElementsByTagName("command"));

            for (Node n : commandList) {
                Element elem = (Element) n;
                if (elem.getAttribute("name").equals(commandName)) {
                    return elem;
                }
            }
        return null;
    }

    static String getCommandProperty(Element commandElement, String property) {
        Element propertyElement =ConfigBasicUtils.getFirstSubElement(commandElement, property);
        return propertyElement != null ? propertyElement.getTextContent() : null;
    }

    static String getCommandProperty(Guild guild, Command command, String property) {
        return getCommandProperty(getCommandElement(guild, command), property);
    }

    static String getCommandProperty(Guild guild, String commandName, String property) {
        return getCommandProperty(guild, CommandUtils.getCommandByName(commandName), property);
    }

    static void setCommandProperty(Element commandElement, String property, String value) {
        Element firstMatchElement = ConfigBasicUtils.getFirstSubElement(commandElement, property);
        if (firstMatchElement == null) {
            Element newPropElem = ConfigBasicUtils.getDocument().createElement(property);
            newPropElem.setTextContent(value);
            commandElement.appendChild(newPropElem);
            return;
        }
        firstMatchElement.setTextContent(value);
    }

    static void setCommandProperty(Guild guild, Command command, String property, String value) {
        setCommandProperty(getCommandElement(guild, command), property, value);
    }

    static void setCommandProperty(Guild guild, String commandName, String property, String value) {
        setCommandProperty(guild, CommandUtils.getCommandByName(commandName), property, value);
    }

    static Element getCommandsElement(Guild guild) {
        return ConfigBasicUtils.getFirstSubElement(ConfigGuildUtils.getGuildNode(guild), "commands");
    }

    public static void initGuildCommands(Guild guild) {
        Element commandsElement = ConfigBasicUtils.getDocument().createElement("commands");
        ConfigGuildUtils.getGuildNode(guild).appendChild(commandsElement);
        initCommandsElement(commandsElement);
    }

    private static void initCommandsElement(Element commandsElement) {
        try {
            for (String command : CommandUtils.commands.keySet()) {
                initCommandElement(commandsElement, command);
            }
        } catch (Exception e4) {
            e4.printStackTrace();
        }
    }

    private static void initCommandElement(Element commandsElement, String command) {
        command = CommandUtils.getPrimaryCommandAlias(command);

        if (commandElementExists(commandsElement, command)) { return; }

        Element commandElement = ConfigBasicUtils.getDocument().createElement("command");

        commandElement.setAttribute("name", command);

        commandsElement.appendChild(commandElement);

        addMissingSubElementsToCommand(commandsElement, command);
    }

    public static boolean isCommandEnabled(Guild guild, Command command) {
        return Boolean.parseBoolean(getCommandProperty(guild, command, "isEnabled"));
    }

    public static void setCommandIsEnabled(Guild guild, Command command, boolean enabled) {
        setCommandProperty(guild, command, "isEnabled", "" + enabled);
    }

    private static boolean commandElementExists(Element commandsElement, String cmdName) {
        return getCommandElement(commandsElement, cmdName) != null;
    }

    private static void addMissingSubElementsToCommand(Element commandsElement, String commandName) {
        Element targetCommandElement = getCommandElement(commandsElement, commandName);

        for (Map.Entry<String, Function<String, Object>> entry : requiredCommandSubElements.entrySet()) {
            if (getCommandProperty(targetCommandElement, entry.getKey()) == null) {
                setCommandProperty(targetCommandElement, entry.getKey(), entry.getValue().apply(commandName).toString());
            }
        }
    }

    public static void initCommandConfigIfNotExists(com.srgood.dbot.commands.CommandParser.CommandContainer cmd) {
        Element serverElement = ConfigGuildUtils.getGuildNode(cmd.event.getGuild());
        Element commandsElement;
        {
            NodeList commandsNodeList = serverElement.getElementsByTagName("commands");
            if (commandsNodeList.getLength() == 0) {
                initGuildCommands(cmd.event.getGuild());
            }
            commandsElement = getCommandsElement(cmd.event.getGuild());

            NodeList commandNodeList = commandsElement.getElementsByTagName("command");
            if (commandNodeList.getLength() == 0) {
                initCommandsElement(commandsElement);
            }
        }
        if (commandElementExists(commandsElement, cmd.invoke)) {
            addMissingSubElementsToCommand(commandsElement, cmd.invoke);
            return;
        }
        initCommandElement(commandsElement, cmd.invoke);
    }

    public static PermissionLevels getCommandPermission(Guild guild, Command command) {
        return PermissionUtils.intToEnum(Integer.parseInt(getCommandProperty(guild, command, "permLevel")));
    }

    public static void setCommandPermission(Guild guild, Command command, PermissionLevels perm) {
        setCommandProperty(guild, command, "permLevel" , "" + perm.getLevel());
    }
}
