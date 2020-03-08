package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Connection implements Runnable {

	private static List<Connection> CONNECTIONS = new ArrayList<Connection>();
	
	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	private int serverNumber;
	private int number;
	private String name = "";
	
	public Connection(Socket socket) throws IOException
	{
		this.socket = socket;
		this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.writer =  new PrintWriter(socket.getOutputStream());
		new Thread(this).start();
	}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			synchronized (CONNECTIONS) {
				CONNECTIONS.add(this);
			}
			serverNumber = Server.getNumber();
			writer.println("Firstly,enter your name :)");
			writer.flush();
			while(!socket.isClosed())
			{
				try {
					boolean uniqueName = true;
					String message = reader.readLine();
					//check input from client
					if(message != null)
					{
						if(this.name == "")
						{
							for(Connection c : CONNECTIONS)
							{
								if(c!=this)
								{
									if(message.equalsIgnoreCase(c.name))
									{
										writer.println("This name already exists! Choose another one");
										writer.flush();
										uniqueName = false;
										break;
									}
								}
							}
							if(uniqueName)
							{
								if(validName(message))
								{
									this.name = message;
									System.out.println(this.name + " connected");
									writer.println("Welcome, " + this.name);
									writer.flush();
									broadcast(this.name + " is online!");
								}
								else
								{
									writer.println("This name is invalid. Name must contain only letters.Try again!");
									writer.flush();
								}
								
							}	
						}
						else if(message.equalsIgnoreCase("close"))
						{
							System.out.println(this.name + " disconnected!");
							writer.println("Goodbye! :)");
							writer.flush();
							broadcast(this.name + " disconnected.");
							socket.close();
						}
						else
						{
							number = Integer.parseInt(message);
							if(!validNumber(number))
							{
								writer.println("Number must have 4 different digits!");
								writer.flush();
							}
							else if(number == serverNumber)
							{
								broadcast(this.name + " guessed the number: " + number + ". Number changed ");
								writer.println("You guessed it. Number was " + number + ". Number changed!");
								writer.flush();
								serverNumber = Server.generateNumber();
								for(Connection c : CONNECTIONS) {
									c.serverNumber = serverNumber;
								}
								System.out.println("Current number: " + Server.getNumber());
							}
							else
							{
								int centered = 0,uncentered = 0;
								int serverNumberCopy = serverNumber;
								while(serverNumberCopy!=0 && number!=0)
								{
									if(number%10 == serverNumberCopy%10)
									{
										centered++;
									}
									else if(checkDigits(serverNumber,number%10))
									{
										uncentered++;
									}
									serverNumberCopy /= 10;
									number /= 10;
								}
								writer.println("Centrate: " + centered + " Necentrate: " + uncentered);
								writer.flush();
							}							
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
//					break;
					writer.println("You must enter a number!");
					writer.flush();
				} 
			}
			synchronized (CONNECTIONS) {
				CONNECTIONS.remove(this);
			}
		}
		
		private boolean validName(String name) {
			// TODO Auto-generated method stub			
			 return name.matches("[a-zA-Z]+");
		}
		private boolean checkDigits(int number,int digit)
		{
			while(number != 0)
			{
				if(number%10 == digit)
				{
					return true;
				}
				number /= 10;
			}
			return false;
		}
		
		private boolean validNumber(int number) {
			if(number <= 999 || number > 9999)
			{
				return false;
			}
			int arr[] = new int[10];
			while(number != 0)
			{
				arr[number%10]++;
				number /= 10;
			}
			for(int i=0;i<arr.length;i++)
			{
				if(arr[i] > 1)
				{
					return false;
				}
			}
			return true;
		}
		private void broadcast(String message) {
			// TODO Auto-generated method stub
			synchronized (CONNECTIONS) {
				for(Connection c : CONNECTIONS)
				{
					if(c!=this)
					{
						c.writer.println(message);
						c.writer.flush();
					}
				}
			}
		}
	
}
