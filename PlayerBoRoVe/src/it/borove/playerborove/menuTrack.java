package it.borove.playerborove;

import it.borove.playerborove.R;
import it.borove.playerborove.R.id;
import it.borove.playerborove.R.layout;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class menuTrack extends Activity{
	private Intent MyCallerIntent;
	private TextView textViewDetails;
	private TextView textViewErase;
	

	private final int RESULT_CODE_DETAILS 	= 300;
	private final int RESULT_CODE_ERASE		= 310;
	private final int RESULT_CODE_SELECT	= 320;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_track);
		
		textViewDetails 		= (TextView)findViewById(R.id.details);
		textViewErase 			= (TextView)findViewById(R.id.erase);
		
		
		this.MyCallerIntent 	= getIntent();

		starsListeners();
		
	}
	
	public void starsListeners(){
		textViewDetails.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {		
				setResult(RESULT_CODE_DETAILS, MyCallerIntent);
				finish();
			}
		});
		
		textViewErase.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {					
				setResult(RESULT_CODE_ERASE, MyCallerIntent);
				finish();
				
			}	
		});

		
	}

}
