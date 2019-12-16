package SCAV;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageHistory;

public class ChannelMessages
{

	private ArrayList<Message> messages = new ArrayList<Message>();
	
	private String channelID = null;
	
	ChannelMessages(MessageChannel channel)
	{
		channelID = channel.getId();
		
		
		List<Message> tempMessageHolder = null;
		try
		{
			tempMessageHolder = MessageHistory.getHistoryBefore(channel, channel.getLatestMessageId()).limit(100).complete().getRetrievedHistory();
		}
		catch(IllegalStateException e)
		{
			System.out.println(channel.getName() + " has no messages!");
			return;
		}
		
		System.out.println("Out of a limit of 101, " + (tempMessageHolder.size() + 1) + " messages were logged from " + channel.getName() + "!");

		if(!tempMessageHolder.isEmpty())
		{
			try
			{
				Message latestMessage = MessageHistory.getHistoryAfter(channel, tempMessageHolder.get(0).getId()).limit(1).complete().getRetrievedHistory().get(0);
				
				messages.add(latestMessage);
				
				FileHelper.checkForAndRipFile(latestMessage);
			}
			catch(IndexOutOfBoundsException e)
			{
				System.out.println("Last message sent in \"" + channel.getName() + "\" was deleted before/during startup.");
			}
		}
		else
		{
			tempMessageHolder = MessageHistory.getHistoryFromBeginning(channel).limit(60).complete().getRetrievedHistory();  //THIS ELSE IS FOR CASES WHERE ONLY 1 COMMENT IS IN A CHANNEL
		}
		
		for(int i = 0; i < tempMessageHolder.size(); i++)
		{
			messages.add(tempMessageHolder.get(i));
			FileHelper.checkForAndRipFile(tempMessageHolder.get(i));
		}
	}
	
	ChannelMessages(String channelID_)
	{
		channelID = channelID_;
	}
	
	public void addMessage(Message message)
	{
		messages.add(0, message);
		FileHelper.checkForAndRipFile(message);
	}
	
	public void setMessage(Message message)
	{
		messages.set(getIndexOfMessageByID(message.getId()), message);
	}
	
	public void removeMessage(String messageID)
	{
		messages.remove(getIndexOfMessageByID(messageID));
		FileHelper.checkForAndRemoveFile(messageID);
	}
	
	public String getChannelID()
	{
		return channelID;
	}
	
	public Message getMessageByID(String messageID)
	{
		for(int i = 0; i < messages.size(); i++)
		{
			if(messages.get(i).getId().equals(messageID))
			{
				return messages.get(i);
			}
		}
		
		return null;
	}
	
	public int getIndexOfMessageByID(String messageID)
	{
		for(int i = 0; i < messages.size(); i++)
		{
			if(messages.get(i).getId().equals(messageID))
			{
				return i;
			}
		}
		
		return -1;
	}
}
