/**
 * Questa classe è la activity principale, quella che viene lanciata all'avvio dell'applicazione
 */
package it.borove.playerborove;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	
	private Button playlist, library, settings, player;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		playlist= (Button)findViewById(R.id.button1);
		library= (Button)findViewById(R.id.button2);
		settings= (Button)findViewById(R.id.button3);
		player= (Button)findViewById(R.id.button4);
		this.addButtonListener();
	}
	/**
	 * Aggiunge i listener ai pulsanti
	 */
	private void addButtonListener(){
		
		playlist.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, PlaylistActivity.class));
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
				startActivity(new Intent(MainActivity.this, SettingsActivity.class));
				//animazione a comparsa da sinistra
				overridePendingTransition(R.anim.right_in, R.anim.left_out); 
			}
		});
		
		player.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, PlayerActivity.class));
				//animazione a comparsa dal basso
				overridePendingTransition(R.anim.bottom_in, R.anim.top_out); 
				
			}
		});
	}
}
