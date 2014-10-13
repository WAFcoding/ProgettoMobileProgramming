/**
 * Questa classe è la activity principale, quella che viene lanciata all'avvio dell'applicazione
 */
package it.borove.playerborove;

import db.ServiceFileObserver;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MainActivity extends Activity {
	
	private Button playlist, library, settings, player;
	private SQLiteDatabase db;
	private final static String TAG = "MainActivity";
	private Intent i;
	private BroadcastReceiver rec;
	private ServiceFileObserver serviceObserver;
	private PlayerController controller;
	
	private static final String DELETE		= "delete";
	private static final String CREATE		= "create";
	private static final String MODIFYFROM	= "modifyfrom";
	private static final String MODIFYTO	= "modifyto";
	private String value;
	private String origValue,newValue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		playlist= (Button)findViewById(R.id.button1);
		library= (Button)findViewById(R.id.button2);
		settings= (Button)findViewById(R.id.button3);
		player= (Button)findViewById(R.id.button4);
		
		controller = new PlayerController(this,this);
		controller.onCreate(db);
		
		serviceObserver = new ServiceFileObserver(this.getApplicationContext());
		i = new Intent(this, ServiceFileObserver.class);
		ComponentName b = startService(i);
		Log.d(TAG, "SERVIZIO PARTITO!!");
		
		serviceObserver.onStart(i, 100);
				
		IntentFilter inf =new IntentFilter("it.borove.playerborove.SERVICE");
		rec = new BroadcastReceiver(){
				@Override
				public void onReceive(Context context, Intent intent) {
					// TODO Auto-generated method stub
					
					if(intent.getExtras().containsKey(MainActivity.CREATE)){
						
						value = intent.getExtras().getString(MainActivity.CREATE);
						//if(!value.equals(alreadyReceivedPath)){
						Log.d(TAG, "onReceive() ---> "+ value);
							//alreadyReceivedPath = value;
						controller.getInfoMetaMp3(getApplicationContext(), value);
						//}				
					}
					else if(intent.getExtras().containsKey(MainActivity.DELETE)){
						value = intent.getExtras().getString(MainActivity.DELETE);
						controller.getInfoMetaMp3(getApplicationContext(), value);
					}
					else if(intent.getExtras().containsKey(MainActivity.MODIFYFROM))
						origValue = intent.getExtras().getString(MainActivity.MODIFYFROM);
					else if(intent.getExtras().containsKey(MainActivity.MODIFYTO))
						newValue = intent.getExtras().getString(MainActivity.MODIFYTO);
					
					if(origValue != null && newValue != null){
						String concValue = origValue + "$" + newValue;
						controller.getInfoMetaMp3(getApplicationContext(), concValue);
						origValue= null;
						newValue=null;
					}
					
					//Log.d(TAG, "onReceive() ---> "+ value);

				}
			};
		registerReceiver(rec,inf);
		
		this.addButtonListener();	
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		try{
			stopService(i);
			Log.d(TAG, "service stoppato!");
			//serviceObserver.onDestroy();
			unregisterReceiver(rec);
		}catch(Exception e){Log.e(TAG,"app destroy>>> " + e.getMessage());}
		
	}

	/**
	 * Aggiunge i listener ai pulsanti
	 */
	private void addButtonListener(){
		
		playlist.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, PlaylistActivity2.class));
				//animazione a comparsa da sinistra
				overridePendingTransition(R.anim.right_in, R.anim.left_out); 
			}
		});
		
		library.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, LibraryActivity.class));
				//animazione a comparsa da destra
				overridePendingTransition(R.anim.left_in, R.anim.right_out); 
			}
		});
		
		settings.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				controller.open_settings();
				//animazione a comparsa da sinistra
				overridePendingTransition(R.anim.right_in, R.anim.left_out); 
			}
		});
		
		player.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				controller.open_player();
				//animazione a comparsa dal basso
				overridePendingTransition(R.anim.bottom_in, R.anim.top_out); 
				
			}
		});

	}
}
