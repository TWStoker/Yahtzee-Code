package yahtzee;

import java.net.*;
import java.io.*;

public class YahtzeeServer {
	
	public static void main (String args[]) throws IOException
	{
		ServerSocket yahtzeeServerSocket = null;
		boolean listening = true;
		String yahtzeeServerName = "YahtzeeServer";
		int yahtzeeServerNumber = 4422;
		
		int SharedVariable = 1;
		
		YahtzeeShared ourYahtzeeSharedObject = new YahtzeeShared(SharedVariable);
		
		try
		{
			yahtzeeServerSocket = new ServerSocket(yahtzeeServerNumber);
		}
		catch (IOException e)
		{
			System.err.println("Could not start " + yahtzeeServerName + " specified port.");
			System.exit(-1);
		}
		System.out.println(yahtzeeServerName + " started");
		
		while (listening)
		{
			new YahtzeeServerThread(yahtzeeServerSocket.accept(), "YahtzeeServerThread1", ourYahtzeeSharedObject).start();
			new YahtzeeServerThread(yahtzeeServerSocket.accept(), "YahtzeeServerThread2", ourYahtzeeSharedObject).start();
			new YahtzeeServerThread(yahtzeeServerSocket.accept(), "YahtzeeServerThread3", ourYahtzeeSharedObject).start();
			System.out.println("New " + yahtzeeServerName + " thread started.");
		}
		yahtzeeServerSocket.close();
		System.exit(0);
	}
}
