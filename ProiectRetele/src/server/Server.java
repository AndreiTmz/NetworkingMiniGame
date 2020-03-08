package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Server implements AutoCloseable, Runnable {

	private ServerSocket serverSocket;
	private static int number;
	private static int v[] = new int[10];
	
	public void start(int port) throws IOException
	{
//		stop();
		this.serverSocket = new ServerSocket(port);
		number = generateNumber();
		new Thread(this).start();
	}
	public void stop() throws IOException
	{
		if(serverSocket!=null)
		{
			System.out.println("Server closed.");
			serverSocket.close();
			serverSocket = null;
		}
	}
	public static int getNumber()
	{
		return number;
	}
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		stop();
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(this.serverSocket!=null && !this.serverSocket.isClosed())
		{
			try {
				Socket socket = this.serverSocket.accept();
				new Connection(socket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
//				continue;
				break;
//				e.printStackTrace();
//				System.out.println("Server exception 1");
			}
		}
	}
	public static int generateNumber() {
		for(int i=0; i<9; i++)
		{
			v[i] = 0;
		}
		int number = 0;		
		Random rand_generator = new Random();		
		int rand_pos;
		while(number==0 || number<1000 )
		{
			if(number == 0)
			{
				do {
					rand_pos = rand_generator.nextInt(9);
				}
				while(rand_pos == 0);
			}
			else
			{
				do {
					rand_pos = rand_generator.nextInt(9);
				}
				while(v[rand_pos] == 1);
				
			}
			v[rand_pos] = 1;
			number = number*10 + rand_pos;
		}
		Server.number = number;
		return number;
	}

}
