package it.borove.playerborove;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class PlaylistEditActivity extends Activity {
	
	private EditText title;
	private Button add, remove, done;
	private ListView listview;
	private AlertDialog.Builder builder;
	private Intent MyCallerIntent;
	private Bundle myBundle;
	private Cursor cursorTracks;
	
	private static ArrayList<SinglePlaylistItem> tracks;
	private ArrayList<Boolean> isGroupSelectedAdd;
	private ArrayList<String> tracksSelectedAdd;
	private ArrayList<Boolean> isGroupSelectedRemove;
	private ArrayList<String> tracksSelectedRemove;
	
	private String idPlaylist= "";
	
	private boolean func_add, func_remove;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_playlist_edit);
		
		title= (EditText)findViewById(R.id.etxt_edit_playlist_name);
		done= (Button)findViewById(R.id.btn_edit_playlist_name);
		add= (Button)findViewById(R.id.btn_edit_playlist_add_track);
		remove= (Button)findViewById(R.id.btn_edit_playlist_remove_track);
		listview= (ListView)findViewById(R.id.list_view_edit_playlist);
		
		func_add= false;
		func_remove= false;
		
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		 
		builder = new AlertDialog.Builder(PlaylistEditActivity.this);

		this.MyCallerIntent = getIntent();
		this.myBundle 	= this.MyCallerIntent.getExtras();
		
		String title_playlist= myBundle.getString("title");
		Cursor playlist= PlayerController.getExactlyPlaylistByName(title_playlist);
		
		idPlaylist= playlist.getString(0);
		
		title.setText(title_playlist);
		
		done.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				PlayerController.editPlaylistName(idPlaylist, title.getText().toString());
				
				if(func_add){
					for(int i=0;i<isGroupSelectedAdd.size();i++){
						boolean b= isGroupSelectedAdd.get(i);
						if(b){
							
							if(tracksSelectedAdd == null){
								tracksSelectedAdd= new ArrayList<String>();
							}
							
							tracksSelectedAdd.add(tracks.get(i).getId());
						}
					}
					
					if(tracksSelectedAdd != null)
						for(String id : tracksSelectedAdd){
							PlayerController.setPlaylistOnDb(title.getText().toString(), id, false);
						}
						Log.d("edit activity", "brani selezionati da aggiungere: " + tracksSelectedAdd.size());
				}
				
				if(func_remove){
					for(int i=0;i<isGroupSelectedRemove.size();i++){
						boolean b= isGroupSelectedRemove.get(i);
						if(b){
							
							if(tracksSelectedRemove == null){
								tracksSelectedRemove= new ArrayList<String>();
							}
							
							tracksSelectedRemove.add(tracks.get(i).getId());
						}
					}
					
					if(tracksSelectedRemove != null)
						PlayerController.deleteTracksInPlaylist(idPlaylist, tracksSelectedRemove);
						Log.d("edit activity", "brani selezionati da rimuovere: " + tracksSelectedRemove.size());
				}
				
				finish();
			}
		});
		
		add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				func_add= true;
				setAddAdapter();
			}
		});
		
		remove.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				func_remove= true;
				setRemoveAdapter();
			}
		});
		
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				if(func_add){
					if(isGroupSelectedAdd == null)
						initIsGroupSelectedAdd();
					
					if(!isGroupSelectedAdd.get(position)){
						view.setBackgroundColor(Color.parseColor("#c0c0c0"));
						isGroupSelectedAdd.set(position, true);
					}
					else{				
						view.setBackgroundColor(Color.TRANSPARENT);
						isGroupSelectedAdd.set(position, false);
					}
				}
				else if(func_remove){
					
					if(!isGroupSelectedRemove.get(position)){
						view.setBackgroundColor(Color.parseColor("#c0c0c0"));
						isGroupSelectedRemove.set(position, true);
					}
					else{				
						view.setBackgroundColor(Color.TRANSPARENT);
						isGroupSelectedRemove.set(position, false);
					}
				}	
			}
			
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode,int resultCode, Intent data){
		
		if(requestCode==800 && resultCode== RESULT_OK){
			
		}
	}
	
	private void setAddAdapter(){
		
		cursorTracks = PlayerController.getCursorTracks();
		
		if(cursorTracks != null){
			if(tracks == null)
				tracks = new ArrayList<SinglePlaylistItem>();
			else
				tracks.clear();
			
			cursorTracks.moveToFirst();
			while(!cursorTracks.isAfterLast()){
				String _id 			= cursorTracks.getString(0);
				String title 		= cursorTracks.getString(5);
				String singerName 	= cursorTracks.getString(2);
				String kind 		= cursorTracks.getString(3);
				String vote 		= cursorTracks.getString(4);
				String nameFile 	= cursorTracks.getString(1);
				String album_id 	= cursorTracks.getString(6);
				String path_track	= cursorTracks.getString(7);
				String albumName 	= cursorTracks.getString(8);
				String duration		= cursorTracks.getString(9);
				//Log.d(TAG,"dentro while--> id: " + _id);
				SinglePlaylistItem singleTrack = new SinglePlaylistItem(_id, title, singerName, 
						kind, vote, nameFile, album_id, 
						path_track, albumName, duration, PlaylistEditActivity.this);
				
				tracks.add(singleTrack);
				
				
				cursorTracks.moveToNext();
			}	
		}
		MyAdapter adapter = new MyAdapter(this, R.layout.item_listview_add_playlist, tracks);
		listview.setAdapter(adapter);
		listview.invalidateViews();
		adapter.notifyDataSetChanged();
		
		if(isGroupSelectedAdd == null)
			initIsGroupSelectedAdd();
	}	
	
	private void setRemoveAdapter(){
		
		/*cursorTracks = PlayerController.getCursorTracks();
		
		if(cursorTracks != null){
			if(tracks == null)
				tracks = new ArrayList<SinglePlaylistItem>();
			else
				tracks.clear();
			
			cursorTracks.moveToFirst();
			while(!cursorTracks.isAfterLast()){
				String _id 			= cursorTracks.getString(0);
				String title 		= cursorTracks.getString(5);
				String singerName 	= cursorTracks.getString(2);
				String kind 		= cursorTracks.getString(3);
				String vote 		= cursorTracks.getString(4);
				String nameFile 	= cursorTracks.getString(1);
				String album_id 	= cursorTracks.getString(6);
				String path_track	= cursorTracks.getString(7);
				String albumName 	= cursorTracks.getString(8);
				String duration		= cursorTracks.getString(9);
				//Log.d(TAG,"dentro while--> id: " + _id);
				SinglePlaylistItem singleTrack = new SinglePlaylistItem(_id, title, singerName, 
						kind, vote, nameFile, album_id, 
						path_track, albumName, duration, PlaylistEditActivity.this);
				
				tracks.add(singleTrack);
				
				
				cursorTracks.moveToNext();
			}	
		}*/
		if(tracks == null)
			tracks = new ArrayList<SinglePlaylistItem>();
		else
			tracks.clear();
		
		tracks= PlayerController.getAllTracksInPlaylist(title.getText().toString());
		MyAdapter adapter = new MyAdapter(this, R.layout.item_listview_add_playlist, tracks);
		listview.setAdapter(adapter);
		listview.invalidateViews();
		adapter.notifyDataSetChanged();
	
		if(isGroupSelectedRemove == null)
			initIsGroupSelectedRemove();
	}
	
	private void initIsGroupSelectedAdd(){

		isGroupSelectedAdd = new ArrayList<Boolean>();
		
		for(int i=0; i < tracks.size(); i++){
			isGroupSelectedAdd.add(false);
        }
	}
	
	private void initIsGroupSelectedRemove(){

		isGroupSelectedRemove = new ArrayList<Boolean>();
		
		for(int i=0; i < tracks.size(); i++){
			isGroupSelectedRemove.add(false);
        }
	}

	private class MyAdapter extends ArrayAdapter<SinglePlaylistItem>{
		private ArrayList<SinglePlaylistItem> arrayTracks;
		//private ImageView cover;
		private TextView title, author;
		/*private ImageView star1;
		private ImageView star2;
		private ImageView star3;
		private ImageView star4;
		private ImageView star5;*/
		
		private View ViewTrack;

		public MyAdapter(Context context, int resource, ArrayList<SinglePlaylistItem> tracks) {
			super(context, resource, tracks);
			 arrayTracks = tracks;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			ViewTrack = convertView;
			if(ViewTrack == null){
				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				ViewTrack 					= inflater.inflate(R.layout.item_listview_add_playlist, null);
			}
			
			//cover 	= (ImageView)ViewTrack.findViewById(R.id.imageView_list_item);			
			title	= (TextView)ViewTrack.findViewById(R.id.title_textView_add_playlist);
			author	= (TextView)ViewTrack.findViewById(R.id.author_textView_add_playlist);
			/*
			star1	= (ImageView)ViewTrack.findViewById(R.id.star10);
			star2 	= (ImageView)ViewTrack.findViewById(R.id.star20);
			star3 	= (ImageView)ViewTrack.findViewById(R.id.star30);
			star4 	= (ImageView)ViewTrack.findViewById(R.id.star40);
			star5 	= (ImageView)ViewTrack.findViewById(R.id.star50);
			*/
			SinglePlaylistItem track = tracks.get(position);
			/*
			final String vote				= track.getVote();
			
			Bitmap bt = track.getBitmapCover();			
			if(bt!= null)
				cover.setImageBitmap(bt);
			else
				cover.setImageResource(R.drawable.icon);
			*/
			String titleTrack = track.getTitle();
			String nameAuthor = track.getSinger_name();
			//Log.d(TAG, "titletrack: " + titleTrack + " nameAuthor: " + nameAuthor);
			title.setText(titleTrack);
			author.setText(nameAuthor);
			/*
			if(!vote.equals(null)){
				if(vote.equals("0")){
					star1.setImageResource(R.drawable.star_silver);
					star2.setImageResource(R.drawable.star_silver);
					star3.setImageResource(R.drawable.star_silver);
					star4.setImageResource(R.drawable.star_silver);
					star5.setImageResource(R.drawable.star_silver);
					
				}
				if(vote.equals("1")){
					star1.setImageResource(R.drawable.star_gold);
					star2.setImageResource(R.drawable.star_silver);
					star3.setImageResource(R.drawable.star_silver);
					star4.setImageResource(R.drawable.star_silver);
					star5.setImageResource(R.drawable.star_silver);
				}
				else if(vote.equals("2")){
					star1.setImageResource(R.drawable.star_gold);
					star2.setImageResource(R.drawable.star_gold);
					star3.setImageResource(R.drawable.star_silver);
					star4.setImageResource(R.drawable.star_silver);
					star5.setImageResource(R.drawable.star_silver);
					
				}
				else if(vote.equals("3")){
					star1.setImageResource(R.drawable.star_gold);
					star2.setImageResource(R.drawable.star_gold);
					star3.setImageResource(R.drawable.star_gold);
					star4.setImageResource(R.drawable.star_silver);
					star5.setImageResource(R.drawable.star_silver);
				}
				else if(vote.equals("4")){
					star1.setImageResource(R.drawable.star_gold);
					star2.setImageResource(R.drawable.star_gold);
					star3.setImageResource(R.drawable.star_gold);
					star4.setImageResource(R.drawable.star_gold);
					star5.setImageResource(R.drawable.star_silver);
				}
				else if(vote.equals("5")){
					star1.setImageResource(R.drawable.star_gold);
					star2.setImageResource(R.drawable.star_gold);
					star3.setImageResource(R.drawable.star_gold);
					star4.setImageResource(R.drawable.star_gold);
					star5.setImageResource(R.drawable.star_gold);
				}
				
			}*/
			return ViewTrack;
		}	
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event){
		
		if(keyCode == KeyEvent.KEYCODE_BACK){

			finish();
			overridePendingTransition(R.anim.bottom_in, R.anim.top_out);
			return true;
		}	
		return super.onKeyDown(keyCode, event); 
	}
}
