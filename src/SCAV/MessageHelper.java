package SCAV;

import java.io.File;
import java.util.ArrayList;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class MessageHelper
{
	
	ChannelHistoryController chc = null;
	
	MessageHelper(ChannelHistoryController chc_)
	{
		chc = chc_;
	}
	
	public void sendMessage(MessageChannel channel, String msg)
	{
		
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
			sendMessage(channel, "The following message, authored by **" + oldMessage.getAuthor() + "**, was edited from:\n```" + oldMessage.getContentRaw() + "```To```" + newMessage.getContentRaw() + "```");
		}
	}
	
	public void sendOnDeleteMessage(MessageChannel channel, Message oldMessage)
	{
		if(!oldMessage.getContentRaw().equals(""))
		{
			sendMessage(channel, "The following message, authored by **" + oldMessage.getAuthor() + "**, was deleted:\n```" + oldMessage.getContentRaw() + "```");
		}
		
		File deletedFile = FileHelper.checkForAndGetFile(oldMessage.getId());
		
		if(deletedFile != null)
		{
			sendMessage(channel, "The following attachment, authored by **" + oldMessage.getAuthor() + "**, was deleted:");
			channel.sendFile(deletedFile).queue();
		}
	}
	
}
