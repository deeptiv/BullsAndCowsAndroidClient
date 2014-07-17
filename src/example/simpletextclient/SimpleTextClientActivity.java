package example.simpletextclient;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import example.simpletextclient.R;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * This is a simple Android mobile client
 * This application read any string massage typed on the text field and 
 * send it to the server when the Send button is pressed
 *
 *
 */
public class SimpleTextClientActivity extends Activity {

	private Socket client;
	private PrintWriter printwriter;
	private BufferedReader in;
	private Button button;
	private String messsage;

	EditText textOut;
	TextView textIn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simple_text_client);


		textOut = (EditText)findViewById(R.id.textout);
		button = (Button)findViewById(R.id.send);
		textIn = (TextView)findViewById(R.id.textin);
		
		 
		// Button press event listener
		button.setOnClickListener(new View.OnClickListener() {
 
			public void onClick(View v) {
				messsage = textOut.getText().toString(); // get the text message on the text field
				textOut.setText(""); // Reset the text field to blank
				SendMessage sendMessageTask = new SendMessage();
				sendMessageTask.execute();
			}
		});


	}

	
		private class SendMessage extends AsyncTask<Void, Void, Void> {

			String textResult;


			@Override
			protected Void doInBackground(Void... params) {
				try {
					
					client = new Socket("10.0.2.2", 8901); // connect to the server
					printwriter = new PrintWriter(client.getOutputStream(), true);
					printwriter.println(messsage); // write the message to output stream
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							textIn.setText("test");
							
						}
					});
					 
					in = new BufferedReader(new InputStreamReader(client.getInputStream()));

			//	while(true){
					String StringBuffer;
			         String stringText = "";
			         while ((StringBuffer = in.readLine()) != null) {
			          stringText += StringBuffer;   
			         }
//			         if((StringBuffer = in.readLine()) != null) {
//				          stringText += StringBuffer;   
//				         }
			
			         textResult = stringText;

			         runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								textIn.setText(textResult);
								
							}
						});
			//	}   
			         

				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						in.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			         printwriter.flush();
					 printwriter.close();
			         

					try {
						client.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} // closing the connection
				}
				return null;
			}
//			  @Override
//			    protected void onPostExecute(Void result) {
//				  
//			    textIn.setText(textResult);
//			    
//			     
//			     super.onPostExecute(result);   
//			    }

		}
		
		

		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.simple_text_client, menu);
			return true;
		}

}