package it.borove.playerborove;

import it.borove.playerborove.R;
import it.borove.playerborove.R.id;
import it.borove.playerborove.R.layout;
import it.borove.playerborove.R.menu;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

public class LibraryDetailsActivity extends Activity {
	
	private TextView total_track, total_memory, total_duration,
					longest_track, biggest_track;
	private AlertDialog.Builder builder;
	private Intent MyCallerIntent;
	private Bundle myBundle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_library_details);
		
		total_track= (TextView)findViewById(R.id.etxt_n_total_tracks);
		total_memory= (TextView)findViewById(R.id.etxt_total_memory);
		total_duration= (TextView)findViewById(R.id.etxt_total_duration);
		longest_track= (TextView)findViewById(R.id.etxt_longest_track);
		biggest_track= (TextView)findViewById(R.id.etxt_biggest_track);
		 
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		 
		builder = new AlertDialog.Builder(LibraryDetailsActivity.this);

		this.MyCallerIntent = getIntent();
		this.myBundle 	= this.MyCallerIntent.getExtras();

		total_track.setText(myBundle.getString("n_tracks"));
		
		total_memory.setText(myBundle.getString("memory_size"));
		total_duration.setText(myBundle.getString("total_duration"));
		total_duration.setSelected(true);
		longest_track.setText(myBundle.getString("longest_file"));
		longest_track.setSelected(true);
		biggest_track.setText(myBundle.getString("bigger_file"));
		biggest_track.setSelected(true);
		
		
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event){
		
		if(keyCode == KeyEvent.KEYCODE_BACK){

			finish();
			overridePendingTransition(R.anim.left_in, R.anim.right_out);
			return true;
		}	
		return super.onKeyDown(keyCode, event); 
	}
}
