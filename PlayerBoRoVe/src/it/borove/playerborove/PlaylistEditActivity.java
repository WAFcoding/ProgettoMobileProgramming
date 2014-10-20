package it.borove.playerborove;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class PlaylistEditActivity extends Activity {
	
	EditText title;
	Button add, remove;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_playlist_edit);
		
		title= (EditText)findViewById(R.id.etxt_edit_playlist_name);
		add= (Button)findViewById(R.id.btn_edit_playlist_add_track);
		remove= (Button)findViewById(R.id.btn_edit_playlist_remove_track);
		
		add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PlayerController.open_add_track_to_playlist();
			}
		});
		
		remove.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PlayerController.open_playlist_tracks();
			}
		});
	}
}
