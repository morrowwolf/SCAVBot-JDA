package SCAV;

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
		
	}
	
	public void sendOnDeleteMessage(MessageChannel channel, Message oldMessage)
	{
		
	}
	
}
