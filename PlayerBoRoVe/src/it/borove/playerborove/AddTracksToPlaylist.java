package it.borove.playerborove;

import java.util.ArrayList;

import playlistModules.PlaylistItem;
import playlistModules.SinglePlaylistItem;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class AddTracksToPlaylist extends Activity {
	private static final String TAG = "AddTracksToPlaylist";
	
	private Cursor cursorTracks;
	private ListView listViewTracks;
	private Button exit;
	private Intent myCallerIntent;
	private Bundle bundle;
	private ArrayList<String> id_trackToAdd;
	private final ArrayList<Boolean> isGroupSelected = new ArrayList<Boolean>();
	
	ArrayList<SinglePlaylistItem> listTracks;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addtrackstoplaylist_activity);
		
		id_trackToAdd 	= new ArrayList<String>();
		myCallerIntent 	= getIntent();
		
		listViewTracks		= (ListView)findViewById(R.id.listViewAddTrack);
		exit				= (Button)findViewById(R.id.btnExitListView);
		
		
		setListTracks();
		
		for(int i=0; i < listTracks.size(); i++){
        	isGroupSelected.add(false);
        }
		
		listViewTracks.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			
				if(!isGroupSelected.get(position)){
					view.setBackgroundColor(Color.parseColor("#c0c0c0"));
					isGroupSelected.set(position, true);
					//selectedTrack = position;
				}
				else{				
					view.setBackgroundColor(Color.TRANSPARENT);
					isGroupSelected.set(position, false);
					//selectedTrack = -1;
				}	
			}
			
		});
		
		exit.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				
				for(int i=0; i < isGroupSelected.size(); i++){
					if(isGroupSelected.get(i)){
						String id = listTracks.get(i).getId();
						id_trackToAdd.add(id);
						Log.d(TAG, "id: "+ id);
					}
				}
				bundle = new Bundle();
				
				bundle.putStringArrayList("idListTracks", id_trackToAdd);
				myCallerIntent.putExtras(bundle);
				setResult(800, myCallerIntent);
				finish();
			}
		});
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private class TracksAdapter extends ArrayAdapter<SinglePlaylistItem>{
		private ArrayList<SinglePlaylistItem> arrayTracks;
		private ImageView cover;
		private ImageView star1;
		private ImageView star2;
		private ImageView star3;
		private ImageView star4;
		private ImageView star5;
		private TextView title, author;
		
		private View ViewTrack;

		public TracksAdapter(Context context, int resource, ArrayList<SinglePlaylistItem> tracks) {
			super(context, resource, tracks);
			 arrayTracks = tracks;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			ViewTrack = convertView;
			if(ViewTrack == null){
				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				ViewTrack 					= inflater.inflate(R.layout.listrow_details, null);
			}
			
			cover 	= (ImageView)ViewTrack.findViewById(R.id.imageView_list_item);			
			title	= (TextView)ViewTrack.findViewById(R.id.lblListItem);
			author	= (TextView)ViewTrack.findViewById(R.id.authorItem);		
			star1	= (ImageView)ViewTrack.findViewById(R.id.star10);
			star2 	= (ImageView)ViewTrack.findViewById(R.id.star20);
			star3 	= (ImageView)ViewTrack.findViewById(R.id.star30);
			star4 	= (ImageView)ViewTrack.findViewById(R.id.star40);
			star5 	= (ImageView)ViewTrack.findViewById(R.id.star50);
			
			SinglePlaylistItem track = arrayTracks.get(position);
			
			final String vote				= track.getVote();
			
			Bitmap bt = track.getBitmapCover();			
			if(bt!= null)
				cover.setImageBitmap(bt);
			else
				cover.setImageResource(R.drawable.icon);
			
			String titleTrack = track.getTitle();
			String nameAuthor = track.getSinger_name();
			Log.d(TAG, "titletrack: " + titleTrack + " nameAuthor: " + nameAuthor);
			title.setText(titleTrack);
			author.setText(nameAuthor);
			
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
			}
			return ViewTrack;
		}	
	}
	
	private void setListTracks(){
		cursorTracks = PlayerController.getCursorTracks();
		
		if(cursorTracks != null){
			listTracks = new ArrayList<SinglePlaylistItem>();
			
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
						path_track, albumName, duration, AddTracksToPlaylist.this);
				
				listTracks.add(singleTrack);
				
				
				cursorTracks.moveToNext();
			}	
		}
		
		
		
		
		TracksAdapter adapter = new TracksAdapter(this, R.layout.listrow_details, listTracks);
		listViewTracks.setAdapter(adapter);
		listViewTracks.invalidateViews();
		adapter.notifyDataSetChanged();
		
	}

	
}
