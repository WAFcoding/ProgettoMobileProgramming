package it.borove.playerborove;

import java.util.ArrayList;

import db.SQLiteConnect;
import playlistModules.PlaylistItem;
import playlistModules.SinglePlaylistItem;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class PlaylistTracks extends Activity{
	private PlaylistTracksAdapter listTracksAdapter;
	private ListView listTracks;
	private ArrayList<SinglePlaylistItem> listOfTracks;
	private Intent MyCallerIntent;
	private Bundle bundle;
	private String id_playlist;
	
	private final int REQUEST_DETAILS_TRACK = 500;
	private int selectedTrack = -1;
	private final ArrayList<Boolean> isGroupSelected = new ArrayList<Boolean>();
	
	private DrawerLayout drawer;
	private ListView drawer_list_view;
	private ActionBarDrawerToggle drawer_toggle;
	private CharSequence title, drawer_title;
	private String[] choices;
	
	private static final String TAG = "PlaylistTrack";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playlisttracks_activity);
		this.listTracks 	= (ListView)findViewById(R.id.playlistTracksId1);
		this.MyCallerIntent = getIntent();
		this.bundle			= MyCallerIntent.getExtras();
		this.id_playlist	= bundle.getString("id_playlist");
		
		//il navigation drawer
		title			= drawer_title = getTitle();
		choices			= getResources().getStringArray(R.array.drawer_choice_library);
		drawer			= (DrawerLayout)findViewById(R.id.drawer_tracks_playlist);
		drawer_toggle	= new ActionBarDrawerToggle(this, drawer, R.drawable.ic_launcher, R.string.drawer_open, R.string.drawer_close){
		//richiamata quando il drawer è completamente chiuso
		public void onDrawerClosed(View view){
			super.onDrawerClosed(view);
			getActionBar().setTitle(title);
			invalidateOptionsMenu();
		}
							
		//richiamata quando il drawer è completamente aperto
		public void onDrawerOpended(View view){
			super.onDrawerOpened(view);
			getActionBar().setTitle(drawer_title);
			invalidateOptionsMenu();
		}
		};
		drawer.setDrawerListener(drawer_toggle);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);	
		drawer_list_view = (ListView)findViewById(R.id.left_drawer_track);
		drawer_list_view.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, choices)); 
		drawer_list_view.setOnItemClickListener(new DrawerItemClickListener());
		
		listOfTracks = bundle.getParcelableArrayList("list");
		
		for(int i=0; i < listOfTracks.size(); i++){
        	isGroupSelected.add(false);
        }
		
		if(listOfTracks != null)
			setListTracks(listOfTracks);
		
		
		this.listTracks.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				
				if(!isGroupSelected.get(position)){
					view.setBackgroundColor(Color.parseColor("#c0c0c0"));
					isGroupSelected.set(position, true);
					selectedTrack = position;
				}
				else{				
					view.setBackgroundColor(Color.TRANSPARENT);
					isGroupSelected.set(position, false);
					selectedTrack = -1;
				}	
				
				
			}
		});
	
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK){
			finish();
			overridePendingTransition(R.anim.left_in, R.anim.right_out);
			return true;
		}
		return super.onKeyDown(keyCode, event); 
	}

	private class PlaylistTracksAdapter extends ArrayAdapter<SinglePlaylistItem>{
		private ArrayList<SinglePlaylistItem> tracks;	
		private ImageView cover;
		private ImageView star1;
		private ImageView star2;
		private ImageView star3;
		private ImageView star4;
		private ImageView star5;
		private TextView title, author;
		
		private View ViewTrack;
		
		public PlaylistTracksAdapter(Context context, int resource, ArrayList<SinglePlaylistItem> tracks) {
			super(context, resource, tracks);
			this.tracks = tracks;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			ViewTrack = convertView;
			if(ViewTrack == null){
				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				//ViewTrack 					= inflater.inflate(R.layout.item_listview, null);
				ViewTrack 					= inflater.inflate(R.layout.listrow_details, null);
			}
			
			//cover 	= (ImageView)ViewTrack.findViewById(R.id.txtImmagine);
			cover 	= (ImageView)ViewTrack.findViewById(R.id.imageView_list_item);
			//title	= (TextView)ViewTrack.findViewById(R.id.txtTitolo);
			title	= (TextView)ViewTrack.findViewById(R.id.lblListItem);
			//author	= (TextView)ViewTrack.findViewById(R.id.txtAutore);
			author	= (TextView)ViewTrack.findViewById(R.id.authorItem);
			
			star1	= (ImageView)ViewTrack.findViewById(R.id.star10);
			star2 	= (ImageView)ViewTrack.findViewById(R.id.star20);
			star3 	= (ImageView)ViewTrack.findViewById(R.id.star30);
			star4 	= (ImageView)ViewTrack.findViewById(R.id.star40);
			star5 	= (ImageView)ViewTrack.findViewById(R.id.star50);
			
			SinglePlaylistItem track = tracks.get(position);
			
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

	private void setListTracks(ArrayList<SinglePlaylistItem> list){
		this.listTracksAdapter = new PlaylistTracksAdapter(this, R.layout.listrow_details, list);
		this.listTracks.setAdapter(listTracksAdapter);
		this.listTracks.invalidateViews();
		this.listTracksAdapter.notifyDataSetChanged();
	}
	
	
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			//Add Track
			if(position == 0){
				//clearData();
				//startActivityForResult(new Intent(PlaylistActivity.this, PlaylistAddActivity.class), ADDPLAYLIST);
			}
			
			//Remove Track
			else if(position == 1){
				if(selectedTrack >= 0){
					SinglePlaylistItem trackDeleted = listOfTracks.remove(selectedTrack);
					Cursor playlist = PlayerController.getCursorPlaylist();
					if(playlist != null){
						String name_playlist = "";
						playlist.moveToFirst();
						while(!playlist.isAfterLast()){
							if(playlist.getString(0).equals(id_playlist)){
								name_playlist = playlist.getString(1);
								break;
								
							}					
							playlist.moveToNext();
						}
						
						PlayerController.setPlaylistOnDb(name_playlist, trackDeleted.getId(), true);
					}				
					setListTracks(listOfTracks);
					
				}
		
			}
			
			//Details Track
			else if(position == 2){
				if(selectedTrack >= 0){
					SinglePlaylistItem track = listOfTracks.get(selectedTrack);
					Intent trackActivity	= new Intent(PlaylistTracks.this, TrackActivity.class);
					Bundle infoTrack 		= new Bundle();
					
					String nameTrack 	= track.getnameFile();
					String singerName	= track.getSinger_name();
					String kind			= track.getKind();
					String vote			= track.getVote();
					String titleTrack	= track.getTitle();
					String albumName	= track.getAlbumName();
					String duration		= track.getDuration();
					Bitmap albumId		= track.getBitmapCover();
					
					infoTrack.putString("nameTrack", nameTrack);
					infoTrack.putString("singerName", singerName);
					infoTrack.putString("kind", kind);
					infoTrack.putString("vote", vote);
					infoTrack.putString("titleTrack", titleTrack);
					infoTrack.putString("albumName", albumName);
					infoTrack.putString("duration", duration);
					
					trackActivity.putExtra("imageAlbum", albumId);
					trackActivity.putExtras(infoTrack);
				
					
					startActivityForResult(trackActivity, REQUEST_DETAILS_TRACK);
				}
			}
			
		}
	}
	@Override
	protected void onActivityResult(int requestCode,int resultCode, Intent data){
		if(resultCode == RESULT_OK){
			Bundle bundle2 = data.getExtras();
			
			String fileNameTrack	= bundle2.getString("fileName");
			String authorName		= bundle2.getString("author");
			String albumName		= bundle2.getString("albumName");
			String kind				= bundle2.getString("kind");
			int valueOfTrack 		= bundle2.getInt("valueTrack");
			String duration			= bundle2.getString("duration");
			
			SinglePlaylistItem track = listOfTracks.get(selectedTrack);
			String idTrack		= track.getId();
			String nameTrack 	= track.getnameFile();
			String singerName	= track.getSinger_name();
			String oldkind		= track.getKind();
			String vote			= track.getVote();
			String titleTrack	= track.getTitle();
			String oldAlbumName	= track.getAlbumName();
			
			if(!nameTrack.equals(fileNameTrack) || !singerName.equals(authorName) || !oldkind.equals(kind) 
					|| !vote.equals(String.valueOf(valueOfTrack)) || !oldAlbumName.equals(albumName)){
				PlayerController.setTagTrackFromActivityLibrary(Integer.parseInt(idTrack),fileNameTrack,authorName,kind,valueOfTrack,albumName,duration);		
				Cursor cursorTrack = PlayerController.getCursorTracks();
				Log.d(TAG, "DOPO CURSOR");
				
				if(cursorTrack != null){
					cursorTrack.moveToFirst();
					boolean found = false;
					while(!cursorTrack.isAfterLast()){
						if(cursorTrack.getString(0).equals(idTrack)){
							found= true;
							Log.d(TAG, "TRACK TROVATA!!");
							track.setAlbumName(cursorTrack.getString(8));
							track.setVote(cursorTrack.getString(4));
							track.setnameFile(cursorTrack.getString(1));
							track.setTitle(cursorTrack.getString(5));
							track.setSinger_name(cursorTrack.getString(2));
							track.setKind(cursorTrack.getString(3));
							
							listOfTracks.set(selectedTrack, track);
							break;
						}
	
						cursorTrack.moveToNext();
					}
					if(found){
						Log.d(TAG, "setListPlaylist chiamata!!");
						setListTracks(listOfTracks);
					}
			
				}
				
			
				
			}
			
			
			
			
			
			
			
		}
	}
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawer_toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawer_toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawer_toggle.onOptionsItemSelected(item)) {
          return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
