package it.borove.playerborove;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TrackActivity extends Activity{
	private Intent MyCallerIntent;
	private Bundle myBundle;
	
	private String title;
	private String author;
	private String titleTrack;
	private String vote;
	private String kind;
	private Bitmap albumCover;
	
	private Button exitTrack;
	private TextView textViewTitle;
	private TextView textViewAuthor;
	private TextView textViewTitleTrack;
	private TextView textViewKind;
	private ImageButton star1,star2,star3,star4,star5;
	private boolean stateStar1;
	private boolean stateStar2;
	private boolean stateStar3;
	private boolean stateStar4;
	private boolean stateStar5;
	private int voteTrack;
	private ImageView albumArt;
	
	private final static String TAG = "TRACKACTIVITY"; 
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_info_song);
		
		exitTrack	=(Button)findViewById(R.id.exitButton);
		
		 textViewTitle 		= (TextView)findViewById(R.id.titleTrack);
		 textViewAuthor 	= (TextView)findViewById(R.id.songArtist);
		 textViewTitleTrack = (TextView)findViewById(R.id.albumName);
		 textViewKind		= (TextView)findViewById(R.id.kind);
		 star1 				= (ImageButton)findViewById(R.id.star1);
		 star2 				= (ImageButton)findViewById(R.id.star2);
		 star3 				= (ImageButton)findViewById(R.id.star3);
		 star4 				= (ImageButton)findViewById(R.id.star4);
		 star5 				= (ImageButton)findViewById(R.id.star5);
		 albumArt	 		= (ImageView)findViewById(R.id.albumArt);
		 voteTrack			= 0;
		 stateStar1=stateStar2=stateStar3=stateStar4=stateStar5=false;

		this.MyCallerIntent = getIntent();
		this.myBundle 	= this.MyCallerIntent.getExtras();
		if(this.myBundle != null){
			this.title 			= this.myBundle.getString("nameTrack");
			this.author 		= this.myBundle.getString("singerName");
			this.titleTrack 	= this.myBundle.getString("titleTrack");
			this.albumCover 	= this.MyCallerIntent.getParcelableExtra("imageAlbum");
			this.kind			= this.myBundle.getString("kind");
			this.vote 			= this.myBundle.getString("vote");
			
			textViewTitle.setText(title);
			textViewAuthor.setText(author);
			textViewTitleTrack.setText(titleTrack);
			textViewKind.setText(kind);
			
			if(albumCover != null)
				albumArt.setImageBitmap(albumCover);
			else
				albumArt.setImageResource(R.drawable.icon);
			
		}
		else
			Log.d(TAG, "myBundle è NULL");

		starsListeners();
		
	}
	
	public void starsListeners(){
		exitTrack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myBundle.putInt("valueTrack", voteTrack);
				MyCallerIntent.putExtras(myBundle);
				
				setResult(Activity.RESULT_OK, MyCallerIntent);
				finish();
			}
			
		});
		
		star1.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				voteTrack = 1;
				if(!stateStar1){
					star1.setImageResource(R.drawable.star_gold);
					stateStar1 = true;	
				}
				else{
					if(!stateStar2){
						star1.setImageResource(R.drawable.star_silver);
						stateStar1 = false;
						voteTrack = 0;
					}
					star2.setImageResource(R.drawable.star_silver);
					star3.setImageResource(R.drawable.star_silver);
					star4.setImageResource(R.drawable.star_silver);
					star5.setImageResource(R.drawable.star_silver);
					stateStar2 = false;
					stateStar3 = false;
					stateStar4 = false;
					stateStar5 = false;	
				}			
			}
		});
		star2.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				voteTrack = 2;
				if(!stateStar2){
					star1.setImageResource(R.drawable.star_gold);
					star2.setImageResource(R.drawable.star_gold);
					stateStar1 = true;
					stateStar2 = true;		
				}
				else{
					if(!stateStar3){
						star2.setImageResource(R.drawable.star_silver);
						stateStar2 = false;
						voteTrack = 2;	
					}
					star3.setImageResource(R.drawable.star_silver);
					star4.setImageResource(R.drawable.star_silver);
					star5.setImageResource(R.drawable.star_silver);
					stateStar3 = false;
					stateStar4 = false;
					stateStar5 = false;		
				}		
			}
		});
		star3.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				voteTrack = 3;
				if(!stateStar3){
					star1.setImageResource(R.drawable.star_gold);
					star2.setImageResource(R.drawable.star_gold);
					star3.setImageResource(R.drawable.star_gold);
					stateStar1 = true;
					stateStar2 = true;
					stateStar3 = true;			
				}
				else{
					if(!stateStar4){
						star3.setImageResource(R.drawable.star_silver);
						stateStar3 = false;
						voteTrack = 2;
						
					}
					star4.setImageResource(R.drawable.star_silver);
					star5.setImageResource(R.drawable.star_silver);
					stateStar4 = false;
					stateStar5 = false;
				}		
			}
		});
		star4.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				voteTrack = 4;
				if(!stateStar4){
					star1.setImageResource(R.drawable.star_gold);
					star2.setImageResource(R.drawable.star_gold);
					star3.setImageResource(R.drawable.star_gold);
					star4.setImageResource(R.drawable.star_gold);
					stateStar1 = true;
					stateStar2 = true;
					stateStar3 = true;
					stateStar4 = true;			
				}
				else{
					if(!stateStar5){
						star4.setImageResource(R.drawable.star_silver);
						stateStar4 = false;
						voteTrack = 3;
						
					}
					star5.setImageResource(R.drawable.star_silver);
					stateStar5 = false;
				}	
			}
		});
		star5.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				voteTrack = 5;
				if(!stateStar5){
					star1.setImageResource(R.drawable.star_gold);
					star2.setImageResource(R.drawable.star_gold);
					star3.setImageResource(R.drawable.star_gold);
					star4.setImageResource(R.drawable.star_gold);
					star5.setImageResource(R.drawable.star_gold);
					stateStar1 = true;
					stateStar2 = true;
					stateStar3 = true;
					stateStar4 = true;
					stateStar5 = true;				
				}
				else{
					star5.setImageResource(R.drawable.star_silver);
					stateStar5 = false;
					voteTrack = 4;
				}
			}
		});	
	}
}
