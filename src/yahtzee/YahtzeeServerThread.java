package yahtzee;

import java.net.*;
import java.io.*;

public class YahtzeeServerThread extends Thread {
	
	private Socket yahtzeeSocket = null;
	private YahtzeeShared mySharedYahtzeeStateObject;
	private String myYahtzeeServerThreadName;
	private int mySharedValue;
	
	public YahtzeeServerThread(Socket yahtzeeSocket, String YahtzeeServerThreadName, YahtzeeShared SharedObject)
	{
		super(YahtzeeServerThreadName);
		this.yahtzeeSocket = yahtzeeSocket;
		mySharedYahtzeeStateObject = SharedObject;
		myYahtzeeServerThreadName = YahtzeeServerThreadName;
	}	
	public void run()
	{
		try
		{
			System.out.println(myYahtzeeServerThreadName + " initialising.");
			PrintWriter out = new PrintWriter(yahtzeeSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(yahtzeeSocket.getInputStream()));
			String inputLine;
			String outputLine;
			
			while ((inputLine = in.readLine()) != null)
			{
				try
				{
					mySharedYahtzeeStateObject.myTurn();
					outputLine = mySharedYahtzeeStateObject.processInput(myYahtzeeServerThreadName, inputLine);
					out.println(outputLine);
					mySharedYahtzeeStateObject.endOfTurn();
				}
				catch (InterruptedException e)
				{
					System.err.println("Failed to get lock when reading: " + e);
				}
			}
			out.close();
			in.close();
			yahtzeeSocket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
