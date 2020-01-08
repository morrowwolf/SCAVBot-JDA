package SCAV;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class Bot
{
	
	Guild scavGuild = null;
	static MessageChannel serverLogsChannel = null;
	ChannelHistoryController chc = null;
	MessageHelper messageHelper = null;
	
	boolean startUpComplete = false;
	boolean running = true;
	
	static String websiteDir = "";
	static String websiteAddress = "";
	
	static String adminRoleID = "";

	
	Bot()
	{
		String botToken = "";
		String guildID = "";
		String serverLogChannelID = "";
		
		if(!new File("config.txt").exists())
		{
			BufferedWriter out = null;
			
			try
			{
				out = new BufferedWriter(new FileWriter("config.txt"));
				out.append("Bot token=");
				out.newLine();
				out.append("Guild ID=");
				out.newLine();
				out.append("Server logs channel ID=");
				out.newLine();
				out.append("Server dir=");
				out.newLine();
				out.append("Server address=");
				out.newLine();
				out.append("Admin role ID=");
				out.flush();
			} 
			catch (IOException e) {e.printStackTrace();}
			finally{if(out != null){try {out.close();}catch (IOException e) {e.printStackTrace();}}}			//close that shit
		}
		
		
		BufferedReader in = null;
		try 
		{
			in = new BufferedReader(new FileReader("config.txt"));
			botToken = in.readLine().substring(10);
			guildID = in.readLine().substring(9);
			serverLogChannelID = in.readLine().substring(23);
			websiteDir = in.readLine().substring(11);
			websiteAddress = in.readLine().substring(15);
			adminRoleID = in.readLine().substring(14);
			
			
			if(botToken.equals("") || guildID.equals("") || serverLogChannelID.equals("") || websiteDir.equals("") || adminRoleID.equals(""))
			{
				System.out.println("One or all of the configuration values is not set.  Ending program.");
				System.exit(0);
			}
		}
		catch (IOException e) {e.printStackTrace();} 
		finally {if(in != null) {try {in.close();} catch (IOException e) {e.printStackTrace();}}}		//close that shit
		
		
		
		JDA jda = null;
		try
		{
			jda = new JDABuilder(botToken).addEventListeners(new MessageListener()).build();
			jda.awaitReady();
			System.out.println("Finished building JDA!");
		}
		catch (LoginException | InterruptedException e) {e.printStackTrace();}
		
		List<Guild> guilds = jda.getGuilds();
		
		for(int i = 0; i < guilds.size(); i++)
		{
			if(guilds.get(i).getId().equals(guildID))
			{
				scavGuild = guilds.get(i);
				System.out.println("Guild found: " + scavGuild.getName());
			}
		}
		
		List<GuildChannel> channels = scavGuild.getChannels();
		
		ArrayList<MessageChannel> messageChannels = new ArrayList<MessageChannel>();
		for(int i = 0; i < channels.size(); i++)
		{
			if(channels.get(i).getId().equals(serverLogChannelID))
			{
				serverLogsChannel = (MessageChannel) channels.get(i);
			}
			if(channels.get(i) instanceof MessageChannel)
			{
				System.out.println(channels.get(i).getName());
				messageChannels.add((MessageChannel) channels.get(i));
			}
		}
		
		FileHelper.cleanFiles();
		
		chc = new ChannelHistoryController(messageChannels);
		
		System.out.println("CHC up and running.");
		
		messageHelper = new MessageHelper(chc);
		
		System.out.println("MessageHelper set.");
		
		startUpComplete = true;
		
		System.out.println("Start up complete!");
		
	}
	
	public boolean go()
	{
		while(running)
		{
			try 
			{
				Thread.sleep(2000);
			} 
			catch (InterruptedException e) {e.printStackTrace();}
		}
		
		return true;
	}

	public class MessageListener extends ListenerAdapter
	{
		
		@Override
		public void onGuildMessageReceived(GuildMessageReceivedEvent event)
		{
			while(!startUpComplete) {try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}} //Hold everything until every part is set and ready to go
			
			chc.addPastMessage(event.getChannel().getId(), event.getMessage());
			
			if(event.getAuthor().isBot())
				return;
			
			JDA jda = event.getJDA();
			
			String msg = event.getMessage().getContentRaw();
			
			
			if(event.getMember().getRoles().contains(jda.getRoleById(adminRoleID)))
			{
				if(msg.toLowerCase().startsWith("!index"))
				{
					if(event.getChannel().getName().startsWith("ban-appeal"))
					{
						FileHelper.exportIndexedChannelToFile(websiteDir + "\\logs\\ban_appeals\\", event.getChannel());
						((GuildChannel)event.getChannel()).delete().queue();
					}
					else if(event.getChannel().getName().startsWith("player-complaint"))
					{
						FileHelper.exportIndexedChannelToFile(websiteDir + "\\logs\\player_complaints\\", event.getChannel());
						((GuildChannel)event.getChannel()).delete().queue();
					}
					else if(event.getChannel().getName().startsWith("admin-complaint"))
					{
						FileHelper.exportIndexedChannelToFile(websiteDir + "\\logs\\admin_complaints\\", event.getChannel());
						((GuildChannel)event.getChannel()).delete().queue();
					}
					else if(event.getChannel().getName().startsWith("admin-app"))
					{
						FileHelper.exportIndexedChannelToFile(websiteDir + "\\logs\\admin_apps\\", event.getChannel());
						((GuildChannel)event.getChannel()).delete().queue();
					}
					else if(event.getChannel().getName().startsWith("coder-app"))
					{
						FileHelper.exportIndexedChannelToFile(websiteDir + "\\logs\\coder_apps\\", event.getChannel());
						((GuildChannel)event.getChannel()).delete().queue();
					}
					else
					{
						MessageHelper.sendMessage(event.getChannel(), "This channel cannot be indexed.");
					}
					return;
					
				}
				else if(msg.toLowerCase().startsWith("!restart"))
				{
					System.out.println("Restarting...");
					FileHelper.cleanFiles();
					System.out.println("Files cleaned.");
					jda.shutdown();
					System.out.println("Shutdown complete");
					running = false;
				}
				else if(msg.toLowerCase().startsWith("!shutdown"))
				{
					if(event.getAuthor().getId().equals(event.getGuild().getOwner().getId()))
					{
						System.out.println("Shutting down...");
						FileHelper.cleanFiles();
						System.out.println("Files cleaned.");
						jda.shutdown();
						System.out.println("Shutdown complete.");
						System.exit(0);
					}
				}
				else if(msg.toLowerCase().startsWith("!find"))
				{
					String ID = msg.substring(msg.lastIndexOf(" ") + 1);
					
					if(ID.length() != 18)
					{
						MessageHelper.sendMessage(event.getChannel(), "That is not a valid ID :angry:");
						return;
					}
					
					User user = jda.getUserById(ID);
					if(user != null)
					{
						MessageHelper.sendMessage(event.getChannel(), "That ID is for the following user: **" + user.toString() + "**");
						return;
					}
					Role role = jda.getRoleById(ID);
					if(role != null)
					{
						MessageHelper.sendMessage(event.getChannel(), "That ID is for the following role: **" + role.toString() + "**");
						return;
					}
					GuildChannel guildChannel = jda.getGuildChannelById(ID);
					if(guildChannel != null)
					{
						MessageHelper.sendMessage(event.getChannel(), "That ID is for the following channel: **" + guildChannel.toString() + "**");
						return;
					}
					
					MessageHelper.sendMessage(event.getChannel(), "That ID could not be located as a user, role, or channel :frowning:");
					return;
					
				}
				else if(msg.toLowerCase().startsWith("!add"))
				{
					if(event.getGuild().getCategoriesByName("appeals/apps/complaints", true).get(0).getChannels().contains(event.getChannel()))
					{

						List<Member> mentionedMembers = event.getMessage().getMentionedMembers();
						for(int i = 0; i < mentionedMembers.size(); i++)
						{
							event.getChannel().getManager().putPermissionOverride(mentionedMembers.get(i), EnumSet.of(Permission.MESSAGE_WRITE), null).queue();;
						}
						
						List<Role> mentionedRoles = event.getMessage().getMentionedRoles();
						for(int i = 0; i < mentionedRoles.size(); i++)
						{
							event.getChannel().getManager().putPermissionOverride(mentionedRoles.get(i), EnumSet.of(Permission.MESSAGE_WRITE), null).queue();;
						}
					}
					else
					{
						MessageHelper.sendMessage(event.getChannel(), "This command can only be used in the \"appeals/apps/complaints\" category.");
					}
					return;
				}
				else if(msg.toLowerCase().startsWith("!remove"))
				{
					if(event.getGuild().getCategoriesByName("appeals/apps/complaints", true).get(0).getChannels().contains(event.getChannel()))
					{
						
						
						
					}
					else
					{
						MessageHelper.sendMessage(event.getChannel(), "This command can only be used in the \"appeals/apps/complaints\" category.");
					}
					return;
				}
			}
			
			if(msg.toLowerCase().startsWith("!ban appeal"))
			{
				ChannelHelper.createAppealAppComplaint(event, new String[]{"ban appeal", "CKEY", "Ban Length", "Ban Reason", "Explanation"}, false);
				return;
			}
			else if(msg.toLowerCase().startsWith("!player complaint"))
			{
				ChannelHelper.createAppealAppComplaint(event, new String[]{"player complaint", "CKEY of bad person", "IC name of bad person", "Round Number/Time and timezone", "Explanation"}, false);
				return;
			}
			else if(msg.toLowerCase().startsWith("!admin complaint"))
			{
				ChannelHelper.createAppealAppComplaint(event, new String[]{"admin complaint", "CKEY of admin", "Round Number/Time and timezone", "Explanation"}, false);
				return;
			}
			else if(msg.toLowerCase().startsWith("!admin application"))
			{
				ChannelHelper.createAppealAppComplaint(event, new String[]{"admin application", "CKEY", "IC names", "Explanation"}, true);
				return;
			}
			else if(msg.toLowerCase().startsWith("!coder application"))
			{
				ChannelHelper.createAppealAppComplaint(event, new String[]{"coder application", "CKEY", "Github Account", "Explanation"}, true);
				return;
			}
		}
		
		@Override
		public void onMessageUpdate(MessageUpdateEvent event)
		{	
			if(!event.isFromGuild())
				return;
			
			while(!startUpComplete) {try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}} //Hold everything until every part is set and ready to go
			
			Message oldMessage = chc.getPastMessage(event.getChannel().getId(), event.getMessageId());
			
			messageHelper.sendOnUpdateMessage(serverLogsChannel, oldMessage, event.getMessage());
		
			chc.setPastMessage(event.getMessage());
			
		}
		
		@Override
		public void onMessageDelete(MessageDeleteEvent event)
		{
			if(!event.isFromGuild())
				return;
			
			while(!startUpComplete) {try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}} //Hold everything until every part is set and ready to go
			
			Message oldMessage = chc.getPastMessage(event.getChannel().getId(), event.getMessageId());
			
			messageHelper.sendOnDeleteMessage(serverLogsChannel, oldMessage);
			
			chc.removePastMessage(event.getChannel().getId(), event.getMessageId());
			
		}
		
		@Override
		public void onGuildMemberJoin(GuildMemberJoinEvent event)
		{
			while(!startUpComplete) {try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}} //Hold everything until every part is set and ready to go
			
			MessageHelper.sendMessage(serverLogsChannel, "**" + event.getMember() + "** has joined the server.");
		}
		
		@Override
		public void onGuildMemberLeave(GuildMemberLeaveEvent event)
		{
			while(!startUpComplete) {try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}} //Hold everything until every part is set and ready to go
			
			MessageHelper.sendMessage(serverLogsChannel, "**" + event.getMember() + "** has left the server.");
		}
		
		@Override
		public void onTextChannelCreate(TextChannelCreateEvent event)
		{
			while(!startUpComplete) {try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}} //Hold everything until every part is set and ready to go
			
			chc.setIfAddingChannel(true);
			chc.addNewChannel(event.getChannel().getId());
			chc.setIfAddingChannel(false);
		}
		
		@Override
		public void onTextChannelDelete(TextChannelDeleteEvent event)
		{
			while(!startUpComplete) {try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}} //Hold everything until every part is set and ready to go
			
			//FileHelper.exportDeletedChannelToFile(websiteDir + "logs\\deleted_channels\\" + FileHelper.removeBadChars(event.getChannel().getName()) + "_-_" + FileHelper.removeBadOffsetDateTimeChars(event.getChannel().getTimeCreated().toString()) + ".html", event);
		}
		
	}
}
