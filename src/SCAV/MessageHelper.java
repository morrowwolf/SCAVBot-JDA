package SCAV;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

public class MessageHelper
{
	
	ChannelHistoryController chc = null;
	
	MessageHelper(ChannelHistoryController chc_)
	{
		chc = chc_;
	}
	
	public static void sendMessage(MessageChannel channel, String msg)
	{
		
		if(msg == null || msg.isBlank())
		{
			return;
		}
		
		ArrayList<String> cutMessage = new ArrayList<String>();
		
		while(msg.length() > 1023)
		{
			cutMessage.add(msg.substring(0, 1023));
			msg = msg.substring(1024);
		}
		
		if(cutMessage.isEmpty())
		{
			channel.sendMessage(msg).queue();
		}
		else
		{
			cutMessage.add(msg);
			for(int i = 0; i < cutMessage.size(); i++)
			{
				channel.sendMessage(cutMessage.get(i)).queue();
			}
		}
	}
	
	public void sendOnUpdateMessage(MessageChannel channel, Message oldMessage, Message newMessage)
	{
		if(!oldMessage.getContentRaw().equals(""))
		{
			sendMessage(channel, "The following message, authored by **" + oldMessage.getAuthor() + "**, was edited from:\n```" + formatMessageContents(oldMessage) + "```To```" + formatMessageContents(newMessage) + "```");
		}
	}
	
	public void sendOnDeleteMessage(MessageChannel channel, Message oldMessage)
	{
		if(!oldMessage.getContentRaw().equals(""))
		{
			sendMessage(channel, "The following message, authored by **" + oldMessage.getAuthor() + "**, was deleted:\n```" + formatMessageContents(oldMessage) + "```");
		}
		
		File deletedFile = FileHelper.checkForAndGetFile(oldMessage.getId());
		
		if(deletedFile != null)
		{
			sendMessage(channel, "The following attachment, authored by **" + oldMessage.getAuthor() + "**, was deleted:");
			channel.sendFile(deletedFile).queue();
		}
	}
	
	public static String formatMessageContents(Message message)
	{
		
		List<IMentionable> mentions = message.getMentions();
		
		String formattedContent = message.getContentRaw();
		
		for(int i = 0; i < mentions.size(); i++)
		{
			if(mentions.get(i) instanceof Role)
			{
				formattedContent = formattedContent.replace("<@&" + mentions.get(i).getId() + ">", "@" + mentions.get(i).toString());
			}
			else if(mentions.get(i) instanceof User)
			{
				formattedContent = formattedContent.replace("<@" + mentions.get(i).getId() + ">", "@" + mentions.get(i).toString());
				formattedContent = formattedContent.replace("<@!" + mentions.get(i).getId() + ">", "@" + mentions.get(i).toString());
			}
			else if(mentions.get(i) instanceof GuildChannel)
			{
				formattedContent = formattedContent.replace("<#" + mentions.get(i).getId() + ">", "#" + mentions.get(i).toString());
			}
		}
		
		return formattedContent;
	}
	
}
