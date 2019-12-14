package SCAV;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;

public class FileHelper
{
	public static void exportIndexedChannelToFile(String exportLocation, TextChannelDeleteEvent event)
	{
		ArrayList<List<Message>> tempMessageHolder = new ArrayList<List<Message>>();
		tempMessageHolder.add(0, event.getChannel().getHistoryFromBeginning(100).complete().getRetrievedHistory());
		
		while(!tempMessageHolder.get(0).get(0).getId().equals(event.getChannel().getLatestMessageId()))
		{
			tempMessageHolder.add(0, event.getChannel().getHistoryAfter(tempMessageHolder.get(0).get(0), 100).complete().getRetrievedHistory());
		}
		
		File exportFile = new File(exportLocation);
		exportFile.mkdirs();
		
		PrintWriter printWriter = null;
		
		try 
		{
			printWriter = new PrintWriter(exportFile, "UTF-8");
			printWriter.println("<h3><b>The final recorded state of \"" + event.getChannel().getName() + "\" that was created on the following date: " + event.getChannel().getTimeCreated().toString() + "</b></h3><br><br><br>");
			for(int i = 0; i < tempMessageHolder.size(); i++)
			{
				for(int j = 0; j < tempMessageHolder.get(i).size(); j++)
				{
					Message tempMessage = tempMessageHolder.get(i).get(j);
					String tempMessageContent = tempMessage.getContentRaw().replace("\n", "<br>");
					printWriter.println("<b>" + tempMessage.getAuthor().getName() + "</b>" + tempMessage.getTimeCreated() + (tempMessage.isEdited() ? " (Edited)" : "") + "<br>" + tempMessageContent + "<br>");
				}
			}
			
			printWriter.println("<br><br><br><h3><b>END OF FILE</b></h3>");
		} 
		catch (FileNotFoundException | UnsupportedEncodingException e) {e.printStackTrace();}
		finally{if(printWriter != null){printWriter.close();}}
		
		
		
	}
	
	public static String removeBadChars(String str)  ///\:*?|"<>. 
	{
		return str.replace(" ", "ReSpace")
				.replace("/", "ReFSlash")
				.replace("\\", "ReBSlash")
				.replace(":", "ReColon")
				.replace("*", "ReAster")
				.replace("?", "ReQuest")
				.replace("|", "RePipe")
				.replace("\"", "ReQuote")
				.replace("<", "ReLesTn")
				.replace(">", "ReGrtTn");
	}
	
	public static String removeBadOffsetDateTimeChars(String str)
	{
		return str.replaceFirst(":", "").replaceFirst(":", "_").replaceFirst(":", "");
	}
}
