package yahtzee;

import java.io.*;
import java.net.*;

public class YahtzeeClient1 {
	
	@SuppressWarnings("resource")
	public static void main (String args[]) throws IOException
	{
		Socket YahtzeeClientSocket = null;
		boolean accessing = true;
		PrintWriter out = null;
		BufferedReader in = null;
		int YahtzeeSocketNumber = 4422;
		String YahtzeeServerName = "localhost";
		String YahtzeeClientID = "YahtzeePlayer1";
		
		try
		{
			YahtzeeClientSocket = new Socket(YahtzeeServerName, YahtzeeSocketNumber);
			out = new PrintWriter(YahtzeeClientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(YahtzeeClientSocket.getInputStream()));
		}
		catch (UnknownHostException e)
		{
			System.err.println("Don't know about host: localhost");
			System.exit(1);
		}
		catch (IOException e)
		{
			System.err.println("Couldn't get I/O for the connection to " + YahtzeeSocketNumber);
			System.exit(1);
		}
		
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		String fromServer;
		String fromUser;
		
		System.out.println("Initialised " + YahtzeeClientID + " client and IO connections");
		
		int[][] currentScoreRecord = new int[][] {{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0}};
		int[][] canScoreThisRound = new int[][] {{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0}};
	    int currentScore = 0;

	    int[] theDice = new int[] {0, 0, 0, 0, 0 };// dice scores
	    int noRolls = 0;
	    int temp = 0;
	    boolean reroll = true;
	    int[] rerollDice = new int[5];
	    int rerollDie = 0;
		
	    int round = 1;
	    
		while (accessing)
		{
			if (round <= 13)
			{
				System.out.println("_________________________");
				System.out.println("Round " + round + " of 4" );
		    	System.out.println("Your current scoring status is:");
		    	currentScore = YahtzeeSinglePlayer.showCurrentScore(currentScoreRecord);
		    	
		    	//Roll the dice  
		    	for (int i = 0; i < 5; i++) {
		    		theDice[i] = YahtzeeSinglePlayer.die();// sets the dice values
		    	}

		    	//See what we have rolled
		    	YahtzeeSinglePlayer.showDice(theDice);
		      
		    	//Check rerolls - three dice to reroll
		    	System.out.println("Three chances to reroll");
		    	noRolls = 0;
		    	reroll = true;
		    	rerollDie = 1;
		    	while (reroll){
		    	  noRolls++;
		    	  if (rerollDie > 0) {
		    		  rerollDie = YahtzeeSinglePlayer.inputInt("How many dice do you want to reroll? (1-5 - 0 for no dice)");
		    		  if (rerollDie > 0) {
		    			  for (int i=0; i<rerollDie;i++) {
		    				  temp = YahtzeeSinglePlayer.inputInt("Select a die (1-5)");
		    				  rerollDice[i] = temp - 1; //adjust for array index
		    			  }
		    			  for (int i=0; i<rerollDie;i++) {
		    				  theDice[rerollDice[i]] = YahtzeeSinglePlayer.die();
		    			  }
		    			  YahtzeeSinglePlayer.showDice(theDice);
		    		  }
		    	  }else {
		    		  reroll = false;
		    	  }
		    	  if (noRolls == 3) {
		    		  reroll = false;
		    	  }
		    	}//while
		      
		    	//What can be scored?
		    	
		    	canScoreThisRound = YahtzeeSinglePlayer.whatCanBeScored(currentScoreRecord, theDice);

		    	//User chooses

		    	currentScoreRecord = YahtzeeSinglePlayer.chooseWhatToScore(currentScoreRecord, canScoreThisRound);
		      
		    	//Now print total score so far
		    	
		    	//YahtzeeSinglePlayer.showCurrentScore(currentScoreRecord);
			}	
			currentScore = YahtzeeSinglePlayer.showCurrentScore(currentScoreRecord);

			System.out.println(YahtzeeClientID + " sending to YahtzeeServer");
			out.println(currentScore);
			
			fromServer = in.readLine();
	
			while (fromServer.equals("Rounds not matching"))
			{
				System.out.println("Please wait. Other players have now taken their turn yet.");
				fromUser = stdIn.readLine();
				out.println(YahtzeeSinglePlayer.showCurrentScore(currentScoreRecord));
				fromServer = in.readLine();
			}
			System.out.println(fromServer);
			round ++;
			
			while (round >= 14)
			{
				out.println(currentScore);
				fromServer = in.readLine();
				System.out.println("All rounds completed. Press Enter to continue");
				fromUser = stdIn.readLine();
				out.println("GAME OVER");
				fromServer = in.readLine();
				System.out.println(fromServer);
				if (fromServer.contains("Winner"))
				{
					out.println("Thanks for playing");
					fromServer = in.readLine();
					System.out.println(fromServer);
					out.close();
					in.close();
					stdIn.close();
					YahtzeeClientSocket.close();
					System.exit(0);
				}
			}		
		}
	}
}
