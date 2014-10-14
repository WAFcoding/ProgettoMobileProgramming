package it.borove.playerborove;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DetailsLibraryActivity extends Activity {
	
	private TextView n_tracks;
	private TextView duration;
	private TextView memory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details_library);
		
		memory= (TextView)findViewById(R.id.text_view_details_library_memory);
		duration= (TextView)findViewById(R.id.text_view_details_library_duration_of_library);
		n_tracks= (TextView)findViewById(R.id.text_view_details_library_num_of_track);
		
		Bundle bundle= getIntent().getExtras();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.details_library, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
