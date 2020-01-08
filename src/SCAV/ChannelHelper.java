package SCAV;

import java.util.EnumSet;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;

public class ChannelHelper
{	
	//ChannelHelper.createAppealAppComplaint(event, new String[] = {"nameOfType", "FirstFormQuestion", "SecondFormQuestion", ...}, boolean openToEveryone);
	
	public static void createAppealAppComplaint(GuildMessageReceivedEvent event, String[] args, boolean openToEveryone)
	{
		String[] splitMessage = event.getMessage().getContentDisplay().split(":");
		
		if(splitMessage.length == 1)
		{
			String questions = "Copy and paste the following and fill in your answers to create your " + args[0] + ":\n```!" + args[0] + "\n\n";
			for(int i = 1; i < args.length; i++)
			{
				if(i == args.length - 1)
					questions += args[i] + ":";
				else
					questions += args[i] + ":\n\n";
			}
			
			questions += "```";
			
			MessageHelper.sendMessage(event.getChannel(), questions);
			return;
		}
		
		String splitRegex = "";
		
		for(int i = 1; i < args.length; i++)
		{
			if(i == args.length - 1)
				splitRegex += args[i] + ":";
			else
				splitRegex += args[i] + ":|";
		}

		splitMessage = event.getMessage().getContentDisplay().split(splitRegex);
		
		if(splitMessage.length != args.length)
		{
			MessageHelper.sendMessage(event.getChannel(), "Your filled out form is in an unreadable state.  Please maintain the basic copyable format.");
		}
		
		for(int i = 0; i < splitMessage.length; i++)
		{
			if(splitMessage[i].isBlank())
			{
				MessageHelper.sendMessage(event.getChannel(), "Please fill out every section.");
			}
		}
		
		String channelName = args[0] + splitMessage[1];
		
		if(channelName.length() > 100)
		{
			channelName = channelName.substring(0, 99);
		}
		
		ChannelAction<TextChannel> CA = event.getGuild().createTextChannel(channelName);
		
		CA.setParent(event.getGuild().getCategoriesByName("appeals/apps/complaints", true).get(0));
		
		EnumSet<Permission> write = EnumSet.of(Permission.MESSAGE_WRITE);
	
		if(!openToEveryone)
		{
			CA.addPermissionOverride(event.getJDA().getRolesByName("@everyone", true).get(0), null, write);
		
			CA.addPermissionOverride(event.getMember(), write, null);
		}
		
		CA.addPermissionOverride(event.getJDA().getRoleById(Bot.adminRoleID), write, null);
		
		String topic = "";
		
		for(int i = 1; i < args.length; i++)
		{
			if(i == args.length - 1)
				topic += "**" + args[i] + "**:" + splitMessage[i];
			else
				topic += "**" + args[i] + "**:" + splitMessage[i] + "\n";
		}
		
		CA.setTopic(topic);
		
		CA.queue();
	}
}
