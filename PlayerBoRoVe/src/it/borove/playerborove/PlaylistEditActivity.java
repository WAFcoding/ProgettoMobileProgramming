package it.borove.playerborove;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class PlaylistEditActivity extends Activity {
	
	private EditText title;
	private Button add, remove, modify_name;
	private AlertDialog.Builder builder;
	private Intent MyCallerIntent;
	private Bundle myBundle;
	
	private String idPlaylist= "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_playlist_edit);
		
		title= (EditText)findViewById(R.id.etxt_edit_playlist_name);
		modify_name= (Button)findViewById(R.id.btn_edit_playlist_name);
		add= (Button)findViewById(R.id.btn_edit_playlist_add_track);
		remove= (Button)findViewById(R.id.btn_edit_playlist_remove_track);
		
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		 
		builder = new AlertDialog.Builder(PlaylistEditActivity.this);

		this.MyCallerIntent = getIntent();
		this.myBundle 	= this.MyCallerIntent.getExtras();
		
		String title_playlist= myBundle.getString("title");
		Cursor playlist= PlayerController.getExactlyPlaylistByName(title_playlist);
		idPlaylist= playlist.getString(0);
		
		title.setText(title_playlist);
		
		modify_name.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				PlayerController.editPlaylistName(idPlaylist, title.getText().toString());
			}
		});
		
		add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//PlayerController.open_add_track_to_playlist();
				startActivityForResult(new Intent(PlaylistEditActivity.this, PlaylistAddActivity.class), 800);
			}
		});
		
		remove.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PlayerController.open_playlist_tracks();
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode,int resultCode, Intent data){
		
		if(requestCode==800 && resultCode== RESULT_OK){
			
		}
	}
}
