package yahtzee;

public class YahtzeeShared {
	
	private YahtzeeShared sharedObject;
	private String myThreadName;
	private boolean accessing = false;
	private int mySharedVariable;
	private int threadsWaiting = 0;
	private int round1 = 1;
	private int round2 = 1;
	private int round3 = 1;
	private int score1 = 0;
	private int score2 = 0;
	private int score3 = 0;
	private boolean finish1 = false;
	private boolean finish2 = false;
    private boolean finish3 = false;
	
	YahtzeeShared (int SharedVariable)
	{
		mySharedVariable = SharedVariable;
	}

	public synchronized void myTurn () throws InterruptedException
	{
		Thread me = Thread.currentThread();
		System.out.println(me.getName() + " wants a turn!");
		++ threadsWaiting;
		while (accessing)
		{
			System.out.println(me.getName() + " wants to have a go next!");
			wait();
		}
		-- threadsWaiting;
		accessing = true;
		System.out.println(me.getName() + " may now go ahead with their turn!");
	}
	public synchronized void endOfTurn ()
	{
		accessing = false;
		notifyAll();
		Thread me = Thread.currentThread();
		System.out.println(me.getName() + " has finished their turn!");
	}
	public synchronized String processInput(String myThreadName, String theInput)
	{
		String theOutput = null;
		if (theInput.equalsIgnoreCase("Thanks for playing")) //Output from client
		{
			theOutput = "Game over"; //Prints to client prompting closure
		}
		
		if (theInput.equalsIgnoreCase("GAME OVER")) //If server reads game over
		{
			if (myThreadName.equals("YahtzeeServerThread1")) //Player 1
			{
				finish1 = true; //Finish status goes to true
				theOutput = "We are gathering the results. Please wait"; //Prints this message to client until all clients are true
			}
			else if (myThreadName.equalsIgnoreCase("YahtzeeServerThread2")) //Player 2
			{
				finish2 = true;
				theOutput = "We are gathering the results. Please wait";
			}
			else if (myThreadName.equalsIgnoreCase("YahtzeeServerThread3")) //Player 3
			{
				finish3 = true;
				theOutput = "We are gathering the results. Please wait";
			}
			if(finish1 == true && finish2 == true && finish3 == true)
			{
				String winner = winnerDecider();
				
				//Switch statement returns winner depending on who scored more
				switch(winner) 
				{
				case "Player1":
					theOutput = "Winner is Player 1 with " + score1 + " points. Player 2 has " + score2 + " points and Player 3 has " + score3 + " points.";
					System.out.println(theOutput);
					return theOutput;
				case "Player2":
					theOutput = "Winner is Player 2 with " + score2 + " points. Player 1 has " + score1 + " points and Player 3 has " + score3 + " points.";
					System.out.println(theOutput);
					return theOutput;
				case "Player3":
					theOutput = "Winner is Player 3 with " + score3 + " points. Player 1 has " + score1 + " points and Player 2 has " + score2 + " points.";
					System.out.println(theOutput);
					return theOutput;
				}
			}
		}
		System.out.println(myThreadName + " received " + theInput);
//		int input = Integer.parseInt(theInput);
		if (mySharedVariable <= 13)
		{
			if (myThreadName.equals("YahtzeeServerThread1"))
			{
				if (mySharedVariable == round1)
				{
					score1 = Integer.parseInt(theInput); //Recognises the score that the player got
					//System.out.println(round1);
					theOutput = "Round " + round1 + ". Score is currently " + score1 + ". Player 2 is on " + score2 + ", and Player 3 is on " + score3;
					round1 ++; //Increases the round by 1
					//System.out.println(round1);
					roundMatching(); //Checks to see if everyone is on the same round
				}
				else
				{
					theOutput = "Rounds not matching";
				}
			}
			else if (myThreadName.equals("YahtzeeServerThread2"))
			{
				if (mySharedVariable == round2)
				{					
					score2 = Integer.parseInt(theInput); //Recognises the score that the player got
					//System.out.println(round2);
					theOutput = "Round " + round2 + ". Score is currently " + score2 + ". Player 1 is on " + score1 + ", and Player 3 is on " + score3;
					round2 ++; //Increases the round by 1
					//System.out.println(round2);
					roundMatching();
				}
				else
				{
					theOutput = "Rounds not matching";
				}
			}
			else if (myThreadName.equals("YahtzeeServerThread3"))
			{
				if (mySharedVariable == round3)
				{
					score3 = Integer.parseInt(theInput); //Recognises the score that the player got
					//System.out.println(round3);
					theOutput = "Round " + round3 + ". Score is currently " + score3 + ". Player 1 is on " + score1 + ", and Player 2 is on " + score2;
					round3 ++; //Increases the round by 1
					//System.out.println(round3);
					roundMatching();
				}
				else
				{
					theOutput = "Rounds not matching";
				}
			}
			else
			{
				System.out.println("Error - thread call not recognised.");
			}
		}
		
		
		//System.out.println(mySharedVariable);
		System.out.println(theOutput);
		return theOutput;
	}
	public void roundMatching ()
	{
		if (round1 > mySharedVariable && round2 > mySharedVariable && round3 > mySharedVariable)
		{
			mySharedVariable ++;
		}
	}
	
	//Method analyses the scores of each client and then decides on the positioning of each one
	public String winnerDecider ()
	{
		String whoWins = null;	
		if (score1 > score2 && score1 > score3)
		{
			whoWins = "Player1";
		}
		else if (score2 > score1 && score2 > score3)
		{
			whoWins = "Player2";
		}
		else if (score3 > score1 && score3 > score2)
		{
			whoWins = "Player3";
		}
		return whoWins;
	}
}
