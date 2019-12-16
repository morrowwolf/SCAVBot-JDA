package SCAV;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class FileHelper
{
	public static void exportIndexedChannelToFile(String exportLocation, MessageChannel channel)
	{
		ArrayList<List<Message>> tempMessageHolder = new ArrayList<List<Message>>();
		tempMessageHolder.add(0, channel.getHistoryFromBeginning(100).complete().getRetrievedHistory());
		
		while(!tempMessageHolder.get(0).get(0).getId().equals(channel.getLatestMessageId()))
		{
			tempMessageHolder.add(0, channel.getHistoryAfter(tempMessageHolder.get(0).get(0), 1).complete().getRetrievedHistory());
		}
		
		File exportDirFile = new File(exportLocation + FileHelper.removeBadChars(channel.getName()) + "-_-" + FileHelper.removeBadOffsetDateTimeChars(channel.getTimeCreated().toString()));
		
		exportDirFile.mkdirs();
		
		File exportFile = new File(exportDirFile.getPath() + "\\info.html");
		
		BufferedWriter bufferedWriter = null;
		
		try
		{
			bufferedWriter = new BufferedWriter(new FileWriter(exportFile));
			bufferedWriter.append("<h3><b>The final recorded state of \"" + channel.getName() + "\" that was created on the following date: " + channel.getTimeCreated().toString() + "</b></h3><br><br><br>");
			bufferedWriter.newLine();
			bufferedWriter.flush();
			for(int i = tempMessageHolder.size() - 1; i >= 0; i--)
			{
				for(int j = tempMessageHolder.get(i).size() - 1; j >= 0; j--)
				{
					Message tempMessage = tempMessageHolder.get(i).get(j);
					String tempMessageContent = MessageHelper.formatMessageContents(tempMessage).replace("\n", "<br>");
					bufferedWriter.append("<b>" + tempMessage.getAuthor().getName() + "</b>" + tempMessage.getTimeCreated() + (tempMessage.isEdited() ? " (Edited)" : "") + "<br>" + tempMessageContent);
					bufferedWriter.newLine();
					bufferedWriter.flush();
					
					if(checkForAndRipFile(tempMessage, exportDirFile) != null)
					{
						String fileEnd = tempMessage.getAttachments().get(0).getFileName().substring(tempMessage.getAttachments().get(0).getFileName().lastIndexOf("."));
						
						String[] pathSections = exportDirFile.getPath().split("\\\\");
						
						String filePointer = pathSections[pathSections.length - 3] + "\\" + pathSections[pathSections.length - 2] + "\\" + pathSections[pathSections.length - 1] + "\\" + tempMessage.getId() + fileEnd;
						
						bufferedWriter.append("<a href=\"" + filePointer + "\">" + tempMessage.getId() + "</a>");
						bufferedWriter.newLine();
						bufferedWriter.flush();
					}
					
					bufferedWriter.append("<br><br>");
					bufferedWriter.newLine();
					bufferedWriter.flush();
				}
			}
			
			bufferedWriter.append("<br><br><br><h3><b>END OF FILE</b></h3>");
			bufferedWriter.newLine();
		} 
		catch (IOException e) {e.printStackTrace();}
		finally{if(bufferedWriter != null){try {bufferedWriter.close();} catch (IOException e) {e.printStackTrace();}}}
		
		String[] pathSections = exportDirFile.getPath().split("\\\\");
		
		MessageHelper.sendMessage(Bot.serverLogsChannel, channel + " was indexed.\n" + Bot.websiteAddress + "/" + pathSections[pathSections.length - 3] + "/" + pathSections[pathSections.length - 2] + "/" + pathSections[pathSections.length - 1] + "/info.html");
		
		
		
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
	
	public static void cleanFiles()
	{
		File file = new File(Bot.websiteDir + "logs\\temp_files\\");
		
		String[] filesToBeDeleted = file.list();
		
		for(int i = 0; i < filesToBeDeleted.length; i++)
		{
			File deleteFile = new File(file.getPath() + "\\" + filesToBeDeleted[i]);
			deleteFile.delete();
		}
	}
	
	public static File checkForAndRipFile(Message message)
	{
		if(message.getAttachments().isEmpty())
		{
			return null;
		}
		
		File file = new File(Bot.websiteDir + "logs\\temp_files\\");
		
		file.mkdirs();
		
		String fileEnd = message.getAttachments().get(0).getFileName().substring(message.getAttachments().get(0).getFileName().lastIndexOf("."));
		
		file = new File(Bot.websiteDir + "logs\\temp_files\\" + message.getId() + fileEnd);
		
		message.getAttachments().get(0).downloadToFile(file);
		return file;
	}
	
	public static File checkForAndRipFile(Message message, File exportDirFile)
	{
		if(message.getAttachments().isEmpty())
		{
			return null;
		}
		
		exportDirFile.mkdirs();
		
		String fileEnd = message.getAttachments().get(0).getFileName().substring(message.getAttachments().get(0).getFileName().lastIndexOf("."));
		
		File file = new File(exportDirFile.getPath() + "\\" + message.getId() + fileEnd);
		
		message.getAttachments().get(0).downloadToFile(file);
		return file;
	}
	
	public static void checkForAndRemoveFile(String messageID)
	{
		File deleteFile = checkForAndGetFile(messageID);
		if(deleteFile != null)
			deleteFile.delete();
	}
	
	public static File checkForAndGetFile(String messageID)
	{
		File file = new File(Bot.websiteDir + "logs\\temp_files\\");
		
		String[] fileNames = file.list();
		
		for(int i = 0; i < fileNames.length; i++)
		{
			if(fileNames[i].startsWith(messageID))
			{
				return new File(Bot.websiteDir + "logs\\temp_files\\" + fileNames[i]);
			}
		}
		
		return null;
	}
}
