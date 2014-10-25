package it.borove.playerborove;

import it.borove.playerborove.R;
import it.borove.playerborove.R.anim;
import it.borove.playerborove.R.id;
import it.borove.playerborove.R.layout;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

public class PlaylistSingleDetails extends Activity {
	
	private TextView n_track_in_playlist, total_duration,
					longest_track_in_playlist;
	private AlertDialog.Builder builder;
	private Intent MyCallerIntent;
	private Bundle myBundle;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_playlist_single_details);
		
		n_track_in_playlist= (TextView)findViewById(R.id.etxt_total_track_in_playlist);
		total_duration= (TextView)findViewById(R.id.etxt_total_single_playlist_duration);
		longest_track_in_playlist= (TextView)findViewById(R.id.etxt_longest_track_in_playlist);
		
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		 
		builder = new AlertDialog.Builder(PlaylistSingleDetails.this);

		this.MyCallerIntent = getIntent();
		this.myBundle 	= this.MyCallerIntent.getExtras();
		
		n_track_in_playlist.setText(myBundle.getString("n_track_in_playlist"));
		total_duration.setText(myBundle.getString("total_duration"));
		longest_track_in_playlist.setText(myBundle.getString("longest_track_in_playlist"));
		longest_track_in_playlist.setSelected(true);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event){
		
		if(keyCode == KeyEvent.KEYCODE_BACK){

			finish();
			overridePendingTransition(R.anim.bottom_in, R.anim.top_out);
			return true;
		}	
		return super.onKeyDown(keyCode, event); 
	}
}
