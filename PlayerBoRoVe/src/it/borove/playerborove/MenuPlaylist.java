package it.borove.playerborove;

import it.borove.playerborove.R;
import it.borove.playerborove.R.id;
import it.borove.playerborove.R.layout;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MenuPlaylist extends Activity{
	private Intent myCallerIntent;
	private TextView textViewDetails;
	private TextView textViewErase;
	private TextView textViewPreview;
	private TextView textViewAddTrack;
	
	private final int RESULT_CODE_DETAILS 	= 400;
	private final int RESULT_CODE_ERASE		= 402;
	private final int RESULT_CODE_PREVIEW	= 401;
	private final int RESULT_CODE_ADD_TRACK	= 403;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_playlist);
		
		textViewDetails		= (TextView)findViewById(R.id.detailsPlaylist);
		textViewErase		= (TextView)findViewById(R.id.erasePlaylist);
		textViewAddTrack	= (TextView)findViewById(R.id.addTrackPlaylist);
		textViewPreview		= (TextView)findViewById(R.id.previewPlaylist);
		
		this.myCallerIntent = getIntent();
		
		listener();
		
		
		
		
	}

	private void listener() {
		
		textViewAddTrack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				setResult(RESULT_CODE_ADD_TRACK, myCallerIntent);
				finish();
				
			}
		});
		
		textViewDetails.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				
				setResult(RESULT_CODE_DETAILS, myCallerIntent);
				finish();
				
			}
		});
		
		textViewErase.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				
				setResult(RESULT_CODE_ERASE, myCallerIntent);
				finish();
				
			}
		});
		
		textViewPreview.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				
				setResult(RESULT_CODE_PREVIEW, myCallerIntent);
				finish();
				
			}
		});
		
		
		
	}

}
