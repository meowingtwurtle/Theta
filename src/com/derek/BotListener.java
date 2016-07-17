package com.derek;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

import net.dv8tion.jda.audio.player.Player;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.events.ReadyEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import net.dv8tion.jda.utils.SimpleLog;

/**
 * <h1>Bot Listener</h1>
 * 
 * Bot Listener deals with MessageRecieved events, excluding its own.
 * 
 * @author srgood
 * @version 0.8
 * @since 7/12/16
 */

public class BotListener extends ListenerAdapter {
	public static String localPrefix;
	public static Map<String,Player> players = new HashMap<>();
	
		@Override
		public void onMessageReceived(MessageReceivedEvent event){
			

			if(Main.servers.containsKey(event.getGuild().getId()) && event.getJDA().getSelfInfo().getId() != event.getAuthor().getId()){
				if(Main.servers.get(event.getGuild().getId()).containsKey("prefix")) {
					localPrefix = Main.servers.get(event.getGuild().getId()).get("prefix");
					SimpleLog.getLog("Reasons").info("Found server with valid prefix ("+ localPrefix +")");
				} else {
					SimpleLog.getLog("Reasons").info("Found server without valid prefix");
					Main.servers.get(event.getGuild().getId()).put("prefix", Main.prefix);
				}
			} else if (event.getJDA().getSelfInfo().getId() != event.getAuthor().getId()) {
				SimpleLog.getLog("Reasons").info("Found nothing");
				Main.servers.put(event.getGuild().getId(),new HashMap<String, String>());
				Main.servers.get(event.getGuild().getId()).put("prefix", Main.prefix);
			}
			
			if (event.getMessage().getContent().equals("(\u256F\u00B0\u25A1\u00B0\uFF09\u256F\uFE35 \u253B\u2501\u253B")) {
				event.getChannel().sendMessage("\u252C\u2500\u252C\uFEFF \u30CE( \u309C-\u309C\u30CE) \n\nCalm down!");
			}
			
			
			
			if(event.getMessage().getContent().startsWith(localPrefix) && event.getMessage().getAuthor().getId() != event.getJDA().getSelfInfo().getId()){
				Main.handleCommand(Main.parser.parse(event.getMessage().getContent().toLowerCase(),event));
				//if (event.getMessage().getContent().toString().substring(1,7).equals("compile"))
				SimpleLog.getLog("Reasons").info("Got Valid Input");
			} else {
				try {
					
					if(event.getJDA().getSelfInfo().getAsMention().equals(event.getMessage().getMentionedUsers().get(0).getAsMention())) {
						
						SimpleLog.getLog("Reasons").info("Got Valid Input (mention)");
						Main.handleCommand(Main.parser.parse(event.getMessage().getContent().toLowerCase(),event));
					}
				} catch (Exception e) {
					
				}
			}
			
		}
		
		@Override
		public void onReady(ReadyEvent event){
			
		}
}
