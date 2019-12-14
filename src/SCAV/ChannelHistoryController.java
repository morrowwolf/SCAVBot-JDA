package SCAV;

import java.util.ArrayList;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class ChannelHistoryController
{
	private ArrayList<ChannelMessages> messageHistoryByChannel = new ArrayList<ChannelMessages>();
	private boolean addingChannel = true;
	
	ChannelHistoryController(ArrayList<MessageChannel> messageChannels)
	{
		for(int i = 0; i < messageChannels.size(); i++)
		{
			messageHistoryByChannel.add(new ChannelMessages(messageChannels.get(i)));
		}
		addingChannel = false;
	}
	
	public void addPastMessage(String channelID, Message message)
	{
		while(addingChannel) {try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}} //Hold everything until the new channel is added
		
		getChannelMessages(channelID).addMessage(message);
	}
	
	public void addNewChannel(String channelID)
	{
		messageHistoryByChannel.add(new ChannelMessages(channelID));
	}
	
	public void setPastMessage(Message message)
	{
		while(addingChannel) {try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}} //Hold everything until the new channel is added
		
		getChannelMessages(message.getChannel().getId()).setMessage(message);
	}
	
	public void setIfAddingChannel(boolean bool)
	{
		addingChannel = bool;
	}
	
	
	public void removePastMessage(String channelID, String messageID)
	{
		while(addingChannel) {try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}} //Hold everything until the new channel is added
		
		getChannelMessages(channelID).removeMessage(messageID);
	}
	
	public Message getPastMessage(String channelID, String messageID)
	{
		while(addingChannel) {try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}} //Hold everything until the new channel is added
			
		return getChannelMessages(channelID).getMessageByID(messageID);
	}
	
	public ChannelMessages getChannelMessages(String channelID)
	{
		while(addingChannel) {try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}} //Hold everything until the new channel is added
		
		for(int i = 0; i < messageHistoryByChannel.size(); i++)
		{
			if(messageHistoryByChannel.get(i).getChannelID().equals(channelID))
			{
				return messageHistoryByChannel.get(i);
			}
		}
		
		return null;

	}
}
