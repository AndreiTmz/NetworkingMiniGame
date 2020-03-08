package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ResourceBundle;

public class Main {
	public static void main(String[] args)
	{
		int port = Integer.parseInt(ResourceBundle.getBundle("settings").getString("port"));
		try(Server server = new Server())
		{
			server.start(port);
			System.out.println("Server is up. Tpye 'exit' to shut it down!");
			System.out.println("Current number: " + Server.getNumber());
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			while(true)
			{
				String command = reader.readLine();
				if(command == null || command.equalsIgnoreCase("exit"))
				{
					break;
				}
			}
			
			server.stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Main server exception 1");
		}
	}
}
