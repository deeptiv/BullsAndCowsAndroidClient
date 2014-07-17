package example.simpletextclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TextClient extends Activity {
	private Socket socket;
	private static final int SERVERPORT = 8901;
	//private static final String SERVER_IP = "10.0.2.2";
	private static final String SERVER_IP = "192.168.1.5";
	
	String textResult = "";
	EditText textOut;
	TextView textIn;
	PrintWriter out = null;
	BufferedReader in = null;
	private String message;
	String prefix = null; 
	Handler updateConversationHandler;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_text_client); 
		textOut = (EditText)findViewById(R.id.textout);

		textIn = (TextView)findViewById(R.id.textin);

		Button button = (Button)findViewById(R.id.send);
		new Thread(new ClientThread()).start();

		// Button press event listener
		button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {

				//	new Thread(new ClientThread()).start();
				message = textOut.getText().toString();
				textOut.setText("");
				new Thread(new CommunicationThread()).start();
			}

		});
	}


	class CommunicationThread implements Runnable {

		@Override
		public void run(){
			if (message.equals("maker")){
				prefix = "MakerInput";
			}else if (message.equals("breaker")){
				prefix = "BreakerInput";
			}
			if( (prefix != null) && !message.equals("maker") && !message.equals("breaker") ){
				message = prefix + message;
			}

			out.println(message + "\n");

			boolean run = true;
			while(run){
				String response = null;
				try {
					response = in.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(response != null){
					textResult = textResult   + response+ "\n";
				}else {
					textResult = "response null";
				}

//					updateConversationHandler.post(new updateUIThread(textResult));		
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						textIn.setText(textResult);
					}
				});

				if((response != null) &&  response.startsWith("input a four letter") || response.startsWith("Word Breaker Player guess the four letter")){
					run = false;
				}
			} 

		}

	}

	class ClientThread implements Runnable {

		@Override
		public void run() {
			try {
				InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
				socket = new Socket(serverAddr, SERVERPORT);
				try {
					out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	class updateUIThread implements Runnable {

		private String msg;

		public updateUIThread(String str) {
			this.msg = str;
		}
		@Override
		public void run() {
			textIn.setText( msg);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			in.close();
			out.close();
			socket.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		socket = null;
	}


}

