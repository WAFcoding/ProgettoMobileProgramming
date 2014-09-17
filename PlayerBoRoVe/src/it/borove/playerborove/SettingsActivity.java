package it.borove.playerborove;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TimePicker;

public class SettingsActivity extends Activity{
	private static final String SETTINGS = "SETTINGS";
	private int valueFadeIn;
	private int valueFadeOut;
	private int pos;
	private int durationPreview;
	private int nLoopPlaylist;
	private boolean randomPlayback;
	private TextView textViewFadeIn;
	private TextView textViewFadeOut;
	private TextView textViewPreview;
	private EditText editTextDisplayTime;
	private EditText editTextNLoopPlaylist;
	private CheckBox infiniteLoopCheckBox;
	private RadioGroup radioGroup;
	static final int TIME_DIALOG_ID = 0;
	
	
            
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);
		final SeekBar seekBarFadeIn=(SeekBar) findViewById(R.id.seekBar1);
		final SeekBar seekBarFadeOut=(SeekBar) findViewById(R.id.seekBar2);
		final SeekBar seekBarPreview=(SeekBar) findViewById(R.id.seekBarPreview);
		
		textViewFadeIn=(TextView) findViewById(R.id.textViewValue1);
		textViewFadeOut=(TextView) findViewById(R.id.textViewValue2);
		textViewPreview=(TextView) findViewById(R.id.textViewValuePreview);
		editTextDisplayTime=(EditText)findViewById(R.id.editText1);
		editTextNLoopPlaylist=(EditText)findViewById(R.id.editTextNLoopPlaylist);
		infiniteLoopCheckBox=(CheckBox) findViewById(R.id.infiniteLoopCheckBox);
		radioGroup=(RadioGroup) findViewById(R.id.radioGroup);
		 getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		
		SharedPreferences prefs=getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);

		valueFadeIn=prefs.getInt("FadeIn", 0);
		valueFadeOut=prefs.getInt("FadeOut", 0);
		pos=prefs.getInt("Pos", 0);
		durationPreview=prefs.getInt("Duration Preview",0);
		nLoopPlaylist=prefs.getInt("NLoopPlaylist", 1);
		randomPlayback=prefs.getBoolean("Random Playback", false);
			
		
		textViewFadeIn.setText(" "+valueFadeIn+"/"+seekBarFadeIn.getMax());
		textViewFadeOut.setText(" "+valueFadeOut+"/"+seekBarFadeOut.getMax());
		textViewPreview.setText(" "+(durationPreview+15)+"/"+(seekBarPreview.getMax()+14));
		editTextDisplayTime.setText(Integer.toString(pos));
		
		if(nLoopPlaylist==1000)
			{
				editTextNLoopPlaylist.setText("");
				editTextNLoopPlaylist.setEnabled(false);
				infiniteLoopCheckBox.setChecked(true);
			}
		else
		editTextNLoopPlaylist.setText(Integer.toString(nLoopPlaylist));
		
		if(randomPlayback)
			radioGroup.check(R.id.radioButtonRandom);
		else
			radioGroup.check(R.id.radioButtonSequential);
	
		
		seekBarFadeIn.setProgress(valueFadeIn);
		seekBarFadeOut.setProgress(valueFadeOut);
		seekBarPreview.setProgress(durationPreview);
		
		
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
		
		seekBarPreview.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			int step=15;

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
			 progress=((int)Math.round(progress/step ))*step;
			 seekBar.setProgress(progress);
			 textViewPreview.setText(" "+(progress+15)+"/"+(seekBarPreview.getMax()+14));
				// TODO Auto-generated method stub
				durationPreview=progress;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		infiniteLoopCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked){
					if(!editTextNLoopPlaylist.getText().toString().equals(""))
						nLoopPlaylist=Integer.parseInt(editTextNLoopPlaylist.getText().toString());
					else
						nLoopPlaylist=1;
					editTextNLoopPlaylist.setEnabled(false);
					editTextNLoopPlaylist.setText("");
				}
				else
				{
					
					editTextNLoopPlaylist.setEnabled(true);
					if(nLoopPlaylist!=1000)
					editTextNLoopPlaylist.setText(Integer.toString(nLoopPlaylist));
				}
				
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
		if(!editTextDisplayTime.getText().toString().equals(""))
			pos=Integer.parseInt(editTextDisplayTime.getText().toString());
		else
			pos=0;
		
		editor.putInt("Pos", pos);
		editor.putInt("Duration Preview", durationPreview);
		if(infiniteLoopCheckBox.isChecked())
			nLoopPlaylist=1000;
		else
			if(!editTextNLoopPlaylist.getText().toString().equals(""))
				nLoopPlaylist=Integer.parseInt(editTextNLoopPlaylist.getText().toString());
			else
				nLoopPlaylist=1;
		editor.putInt("NLoopPlaylist", nLoopPlaylist);
		if(radioGroup.getCheckedRadioButtonId()==R.id.radioButtonRandom)
		{
			editor.putBoolean("Random Playback", true);
			Log.d("true","true");
		}
		else {
			editor.putBoolean("Random Playback", false);
			Log.d("false","false");
		}
		editor.commit();
		Log.d("destroy", "destroy");
	}

}
