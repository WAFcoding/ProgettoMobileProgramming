package it.borove.playerborove;

import it.borove.playerborove.R;
import it.borove.playerborove.R.id;
import it.borove.playerborove.R.layout;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class MenuPlaylist extends Activity{
	private Intent myCallerIntent;
	private TextView textViewDetails;
	private TextView textViewErase;
	private TextView textViewPreview;
	private TextView textViewEdit;
	
	private final int RESULT_CODE_DETAILS 	= 400;
	private final int RESULT_CODE_ERASE		= 402;
	private final int RESULT_CODE_PREVIEW	= 401;
	private final int RESULT_CODE_EDIT	= 403;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_playlist);
		
		textViewDetails		= (TextView)findViewById(R.id.detailsPlaylist);
		textViewErase		= (TextView)findViewById(R.id.erasePlaylist);
		textViewEdit	= (TextView)findViewById(R.id.addTrackPlaylist);
		textViewPreview		= (TextView)findViewById(R.id.previewPlaylist);
		
		this.myCallerIntent = getIntent();
		
		listener();
	}

	private void listener() {
		
		textViewEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				setResult(RESULT_CODE_EDIT, myCallerIntent);
				finish();
				
			}
		});
		textViewEdit.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				textViewEdit.setBackgroundColor(Color.parseColor("#F5DEB3"));
				return false;
			}
		});
		
		textViewDetails.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				
				setResult(RESULT_CODE_DETAILS, myCallerIntent);
				finish();
				
			}
		});
		textViewDetails.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				textViewDetails.setBackgroundColor(Color.parseColor("#F5DEB3"));
				return false;
			}
		});
		
		textViewErase.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				
				setResult(RESULT_CODE_ERASE, myCallerIntent);
				finish();
				
			}
		});
		textViewErase.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				textViewErase.setBackgroundColor(Color.parseColor("#F5DEB3"));
				return false;
			}
		});
		
		textViewPreview.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				
				setResult(RESULT_CODE_PREVIEW, myCallerIntent);
				finish();
				
			}
		});
		textViewPreview.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				textViewPreview.setBackgroundColor(Color.parseColor("#F5DEB3"));
				return false;
			}
		});
		
		
	}

}
