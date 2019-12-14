package SCAV;

public class Driver 
{
	public static void main(String[] args)
	{
		Bot bot = null;
		
		do
		{
			System.out.println("Bot is being initialized...");
			bot = new Bot();
			System.out.println("Bot initialized.");
		}
		while(bot.go());
		
	}
}
