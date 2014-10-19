package library_stuff;

import it.borove.playerborove.R;
import it.borove.playerborove.R.drawable;
import it.borove.playerborove.R.id;
import it.borove.playerborove.R.layout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
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
	private String albumName;
	private String duration;
	private AlertDialog.Builder builder;
	
	private Button exitTrack;
	private EditText edtFileName,edtAuthor,edtAlbum, edtKind;
	private TextView txtViewDuration;
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
		setContentView(R.layout.layout_info_song_test);
		
		exitTrack	=(Button)findViewById(R.id.exitButton);
		
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
		 txtViewDuration	= (TextView)findViewById(R.id.duration1);
		 
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
			this.albumName		= this.myBundle.getString("albumName");
			this.duration		= this.myBundle.getString("duration");
			this.voteTrack		= Integer.parseInt(this.myBundle.getString("vote"));
			
			Log.d(TAG, "duration: -> " + duration);
			
			long sec 	= Long.parseLong(duration) / 1000;
			long min 	= sec / 60;
			sec = sec % 60;
			long hour	= min / 60;
			min = min % 60; 
			String length 	= "";
			String minutes 	= "";
			String seconds 	= "";
			if(sec < 10)
				seconds = "0" + sec;
			else
				seconds = "" + sec;
			if(min < 10)
				minutes = "0" + min;
			else
				minutes	= "" + min;
							
			if(hour <= 0){
				
				length = "" + minutes + ":"+ seconds;
			}
				
			else
				length = hour + ":" + minutes + ":"+ seconds;

				
			 edtFileName.setText(title);
			 edtAuthor.setText(author);
			 edtAlbum.setText(albumName);
			 edtKind.setText(kind);
			 txtViewDuration.setText(length);
			
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
			Log.d(TAG, "myBundle � NULL");
		
		
		edtFileName.setInputType(EditorInfo.TYPE_CLASS_TEXT);
		edtFileName.setImeOptions(EditorInfo.IME_ACTION_DONE);
		edtFileName.setImeActionLabel("boh", KeyEvent.KEYCODE_ENTER);
		
		starsListeners();
		
	}
	
	public void starsListeners(){
		Log.d(TAG, "starsListeners!");
		
		 edtFileName.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				exitTrack.setText("Save");
			}
		});
		
		edtAuthor.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				exitTrack.setText("Save");
			}
		});
		
		edtAlbum.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				exitTrack.setText("Save");
				
			}
		});
		
		exitTrack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				myBundle.putString("fileName", edtFileName.getText().toString());
				myBundle.putString("author", edtAuthor.getText().toString());
				myBundle.putString("albumName", edtAlbum.getText().toString());
				myBundle.putString("kind", edtKind.getText().toString());		
				myBundle.putInt("valueTrack", voteTrack);
				myBundle.putString("duration", duration);
		
				MyCallerIntent.putExtras(myBundle);
				
				setResult(Activity.RESULT_OK, MyCallerIntent);
				finish();
			}
			
		});
		
		star1.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
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
				
				if(actionId == EditorInfo.IME_ACTION_DONE){
					Log.d(TAG, "action done catturato!");
					if(!edtFileName.getText().toString().equals(title))
						exitTrack.setText("Save");	
					return true;
				}
				return false;
			}
		});
		
		edtAuthor.setOnEditorActionListener(new OnEditorActionListener() {		
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(!edtAuthor.getText().toString().equals(author))
					exitTrack.setText("Save");			
				return false;
			}
		});
		edtAlbum.setOnEditorActionListener(new OnEditorActionListener() {		
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(!edtAlbum.getText().toString().equals(albumName))
					exitTrack.setText("Save");			
				return false;
			}
		});
	
		
		edtKind.setOnTouchListener(new OnTouchListener() {	
			@Override
			public boolean onTouch(View v, MotionEvent event) {
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
				
				final String[] array = {"Ambient","Blues","Classic", "Dance", "Electro","Etnic",
			 				"House","Jazz","Metal", "Pop", "Rock","Swing","Techno"};
	 
				int id = getResources().getIdentifier("row_builder", "layout", getPackageName());
				ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(TrackActivity.this, id, R.id.textViewList, array);
				
				builder.setAdapter(arrayAdapter, null);
				builder.setItems(array, new DialogInterface.OnClickListener() {	
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						edtKind.setText(array[which]);
						if(!edtKind.getText().toString().equals(kind))
							exitTrack.setText("Save");
		
					}
				});
							
				AlertDialog alert = builder.create();
				alert.show();
		
			}
		});
		
		albumArt.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				/*if(albums != null){
					
					
					CustomListBitmapAdapter adapter = new CustomListBitmapAdapter(albumsArt);
					builder.setAdapter(adapter, null);
					AlertDialog alert = builder.create();
					alert.show();
				
					
				}
				 */
			}
		});
		
	
	
	}
	
	public class CustomListBitmapAdapter extends BaseAdapter{
		LayoutInflater inflater;
		ArrayList<Bitmap> albums = new ArrayList<Bitmap>();
		
		public CustomListBitmapAdapter(ArrayList<Bitmap> images){
			albums = images;
		
			inflater = LayoutInflater.from(TrackActivity.this);
			
		}

		@Override
		public int getCount() {
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Bitmap album = albums.get(position);
			ImageView imageAlbum;
			
			ViewHolder holder = new ViewHolder();

						
			convertView = inflater.inflate(R.layout.albums_list, null);
			holder.iv = (ImageView)convertView.findViewById(R.id.imageView_list_item);
			convertView.setTag(holder);
			
			
			holder.iv.setImageBitmap(album);
			
			return convertView;
		}
		class ViewHolder{
			ImageView iv;
		}
		
	}
}
