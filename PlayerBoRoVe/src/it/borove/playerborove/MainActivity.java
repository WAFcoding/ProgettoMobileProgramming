package it.borove.playerborove;

import PlayerManager.Duration;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
			
			Duration d= new Duration(13, 59, 55);
			Log.d("DURATION", "Duration iniziale= " + d.getDuration());
			
			d.sum(new Duration(1,20,10));
			
			Log.d("DURATION", "Duration finale= " + d.getDuration());

	}
}
