package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;

public class Client implements Runnable {

	protected Shell shell;
	private Text text;
	private String host;
	private int port;
	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	private List list;
	
	public Client(String host, int port) {
		// TODO Auto-generated constructor stub
		this.host = host;
		this.port = port;
	}

	public static void main(String[] args) {
		try {
			String host = ResourceBundle.getBundle("settings").getString("host");
			int port = Integer.parseInt(ResourceBundle.getBundle("settings").getString("port"));
			Client window = new Client(host, port);
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void open() {
		try {
			Display display = Display.getDefault();
			createContents();
			shell.open();
			shell.layout();
			this.socket = new Socket(host, port);
			this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.writer = new PrintWriter(socket.getOutputStream());
			new Thread(this).start();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
			socket.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	protected void createContents() {
		shell = new Shell();
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				writer.println("close");
				writer.flush();
				try {
					socket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		shell.setSize(450, 300);
		shell.setText("Centrate");
		
		list = new List(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		list.setBounds(10, 10, 412, 201);
		
		text = new Text(shell, SWT.BORDER);
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.keyCode == SWT.CR && text.getText().trim().length() > 0)
				{
					String message = text.getText().trim();
					
					writer.println(message);
					writer.flush();
					text.setText("");
					
					if(message.equalsIgnoreCase("close"))
					{
						System.exit(0);
					}
				}
			}
		});
		text.setBounds(10, 217, 412, 26);

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(!socket.isClosed())
		{
			try {
				final String message = reader.readLine();
				if(message!=null)
				{
					Display.getDefault().asyncExec(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							list.add(message);
						}
					});
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				break;
//				e.printStackTrace();
			} 
		}
	}
}
