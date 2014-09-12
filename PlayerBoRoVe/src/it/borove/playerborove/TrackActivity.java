package it.borove.playerborove;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class TrackActivity extends Activity{
	private Intent MyCallerIntent;
	private Bundle myBundle;
	
	private String title;
	private String author;
	private String titleTrack;
	private String vote;
	private String kind;
	private Bitmap albumCover;
	private AlertDialog.Builder builder;
	
	private Button exitTrack;
	//private TextView textViewFileName;
	//private TextView textViewAuthor;
	//private TextView textViewTitleTrack;
	//private TextView textViewKind;
	private EditText edtFileName,edtAuthor,edtAlbum, edtKind;
	private ImageButton star1,star2,star3,star4,star5;
	private boolean stateStar1;
	private boolean stateStar2;
	private boolean stateStar3;
	private boolean stateStar4;
	private boolean stateStar5;
	private int voteTrack;
	private ImageView albumArt;
	private KeyListener originalKeyListener;
	private ArrayAdapter<String> arrayAdapter;
	private String[] listItems;
	
	private final static String TAG = "TRACKACTIVITY"; 
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_info_song_test);
		
		exitTrack	=(Button)findViewById(R.id.exitButton);
		
		// textViewFileName	= (TextView)findViewById(R.id.fileNameTrack);
		 //textViewAuthor 	= (TextView)findViewById(R.id.songArtist);
		// textViewTitleTrack = (TextView)findViewById(R.id.albumName);
		 //textViewKind		= (TextView)findViewById(R.id.kind);
		 star1 				= (ImageButton)findViewById(R.id.star1);
		 star2 				= (ImageButton)findViewById(R.id.star2);
		 star3 				= (ImageButton)findViewById(R.id.star3);
		 star4 				= (ImageButton)findViewById(R.id.star4);
		 star5 				= (ImageButton)findViewById(R.id.star5);
		 albumArt	 		= (ImageView)findViewById(R.id.albumArt);
		 edtFileName		= (EditText)findViewById(R.id.fileNameTrack);
		 edtAuthor			= (EditText)findViewById(R.id.songArtist);
		 edtAlbum			= (EditText)findViewById(R.id.albumName);
		 edtKind			= (EditText)findViewById(R.id.kind);
		 
         getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		 
		 stateStar1=stateStar2=stateStar3=stateStar4=stateStar5=false;
		 
		 builder = new AlertDialog.Builder(TrackActivity.this);

		this.MyCallerIntent = getIntent();
		this.myBundle 	= this.MyCallerIntent.getExtras();
		if(this.myBundle != null){
			this.title 			= this.myBundle.getString("nameTrack");
			this.author 		= this.myBundle.getString("singerName");
			this.titleTrack 	= this.myBundle.getString("titleTrack");
			this.albumCover 	= this.MyCallerIntent.getParcelableExtra("imageAlbum");
			this.kind			= this.myBundle.getString("kind");
			this.vote 			= this.myBundle.getString("vote");
			this.voteTrack		= Integer.parseInt(this.myBundle.getString("vote"));
			 edtFileName.setText(title);
			 edtAuthor.setText(author);
			 edtAlbum.setText(titleTrack);
			 edtKind.setText(kind);
			 
			
			if(albumCover != null)
				albumArt.setImageBitmap(albumCover);
			else
				albumArt.setImageResource(R.drawable.icon);
			
			if(vote.equals("1")){
				star1.setImageResource(R.drawable.star_gold);
			}
			else if(vote.equals("2")){
				star1.setImageResource(R.drawable.star_gold);
				star2.setImageResource(R.drawable.star_gold);
			}
			else if(vote.equals("3")){
				star1.setImageResource(R.drawable.star_gold);
				star2.setImageResource(R.drawable.star_gold);
				star3.setImageResource(R.drawable.star_gold);
			}
			else if(vote.equals("4")){
				star1.setImageResource(R.drawable.star_gold);
				star2.setImageResource(R.drawable.star_gold);
				star3.setImageResource(R.drawable.star_gold);
				star4.setImageResource(R.drawable.star_gold);
			}
			else if(vote.equals("5")){
				star1.setImageResource(R.drawable.star_gold);
				star2.setImageResource(R.drawable.star_gold);
				star3.setImageResource(R.drawable.star_gold);
				star4.setImageResource(R.drawable.star_gold);
				star5.setImageResource(R.drawable.star_gold);
			}
					
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

				myBundle.putString("fileName", edtFileName.getText().toString());
				myBundle.putString("author", edtAuthor.getText().toString());
				myBundle.putString("album", edtAlbum.getText().toString());
				myBundle.putString("kind", edtKind.getText().toString());		
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
				exitTrack.setText("Save");
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
				exitTrack.setText("Save");
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
				exitTrack.setText("Save");
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
				exitTrack.setText("Save");
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
				exitTrack.setText("Save");
			}
		});	
		
		
		edtFileName.setOnEditorActionListener(new OnEditorActionListener() {		
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				if(!edtFileName.getText().toString().equals(title))
					exitTrack.setText("Save");			
				return false;
			}
		});
		edtAuthor.setOnEditorActionListener(new OnEditorActionListener() {		
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				if(!edtAuthor.getText().toString().equals(author))
					exitTrack.setText("Save");			
				return false;
			}
		});
		edtAlbum.setOnEditorActionListener(new OnEditorActionListener() {		
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				if(!edtAlbum.getText().toString().equals(titleTrack))
					exitTrack.setText("Save");			
				return false;
			}
		});
	
		
		edtKind.setOnTouchListener(new OnTouchListener() {	
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				int type = edtKind.getInputType();
				edtKind.setInputType(InputType.TYPE_NULL);
				edtKind.onTouchEvent(event);
				edtKind.setInputType(type);
			
				return true;
			}
		});
		
		
		edtKind.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				final String[] array = {"Ambient","Blues","Classic", "Dance", "Electro","Etnic",
			 				"House","Jazz","Metal", "Pop", "Rock","Swing","Techno"};
	 
				int id = getResources().getIdentifier("row_builder", "layout", getPackageName());
				ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(TrackActivity.this, id, R.id.textViewList, array);
				
				builder.setAdapter(arrayAdapter, null);
				builder.setItems(array, new DialogInterface.OnClickListener() {	
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
						edtKind.setText(array[which]);
						if(!edtKind.getText().toString().equals(kind))
							exitTrack.setText("Save");
		
					}
				});
							
				AlertDialog alert = builder.create();
				alert.show();
		
			}
		});
		
	
	
	}
}
