package it.borove.playerborove;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SettingsActivity extends Activity{
	private static final String SETTINGS = "SETTINGS";
	private int valueFadeIn;
	private int valueFadeOut;
	private int pos;
	private TextView textViewFadeIn;
	private TextView textViewFadeOut;
	private EditText editText;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);
		final SeekBar seekBarFadeIn=(SeekBar) findViewById(R.id.seekBar1);
		final SeekBar seekBarFadeOut=(SeekBar) findViewById(R.id.seekBar2);
		
		textViewFadeIn=(TextView) findViewById(R.id.textViewValue1);
		textViewFadeOut=(TextView) findViewById(R.id.textViewValue2);
		editText=(EditText)findViewById(R.id.editText1);
		
		SharedPreferences prefs=getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);

		valueFadeIn=prefs.getInt("FadeIn", 0);
		valueFadeOut=prefs.getInt("FadeOut", 0);
		pos=prefs.getInt("Pos", 0);
		
		textViewFadeIn.setText(" "+valueFadeIn+"/"+seekBarFadeIn.getMax());
		textViewFadeOut.setText(" "+valueFadeOut+"/"+seekBarFadeOut.getMax());
		editText.setText(Integer.toString(pos));
		
		seekBarFadeIn.setProgress(valueFadeIn);
		seekBarFadeOut.setProgress(valueFadeOut);
		seekBarFadeIn.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				valueFadeIn=progress;
				
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				textViewFadeIn.setText(" "+valueFadeIn+"/"+seekBarFadeIn.getMax());
			}
			
		});
		
		seekBarFadeOut.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				valueFadeOut=progress;
				
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				textViewFadeOut.setText(" "+valueFadeOut+"/"+seekBarFadeIn.getMax());
			}
			
		});
				
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		SharedPreferences prefs=getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor=prefs.edit();
		editor.putInt("FadeIn", valueFadeIn);
		editor.putInt("FadeOut", valueFadeOut);
		pos=Integer.parseInt(editText.getText().toString());
		editor.putInt("Pos", pos);
		editor.commit();
		Log.d("destroy", "destroy");
	}

}
