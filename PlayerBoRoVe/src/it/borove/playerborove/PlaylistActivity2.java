package it.borove.playerborove;

import java.util.ArrayList;
import java.util.HashMap;

import playlistModules.PlaylistItem;
import playlistModules.SinglePlaylistItem;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;
import android.widget.ExpandableListAdapter;


public class PlaylistActivity2 extends Activity{
	private final String TAG	= "PlaylistActivity2";
	
	private final int ADDPLAYLIST			= 270;
	private final int REQUEST_INFO_TRACK 	= 400;
	protected static final int PREVIEW 		= 0;
	
	private Button btnListTracks;
	
	private DrawerLayout drawer;
	private ListView drawer_list_view;
	private ActionBarDrawerToggle drawer_toggle;
	private CharSequence title, drawer_title;
	private String[] choices;
	
	private SinglePlaylistItem track;
	private final ArrayList<Boolean> isGroupSelected = new ArrayList<Boolean>();
	private Cursor playlistCursor;
	private ListView listview;
	private PlaylistAdapter adapter;
	private ArrayList<PlaylistItem> listPlaylistItem;
	private ArrayList<SinglePlaylistItem> tmp_songs;
	private AlbumMapper mapperPlaylist;

	private ArrayList<String> id_p;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playlist_layout2);

		//tmp_songs			= new ArrayList<SinglePlaylistItem>();
		listview			= (ListView)findViewById(R.id.listView_playlist_activity);
		
		mapperPlaylist		= new AlbumMapper();
			
		//il navigation drawer
		title			= drawer_title = getTitle();
		choices			= getResources().getStringArray(R.array.drawer_choice_playlist);
		drawer			= (DrawerLayout)findViewById(R.id.drawer_playlist1);
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
		drawer_list_view= (ListView)findViewById(R.id.left_drawer_playlist);
		drawer_list_view.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, choices)); 
		drawer_list_view.setOnItemClickListener(new DrawerItemClickListener());
	
		setListPlaylist();
		
		listview.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				PlayerController.previewPlaylist((PlaylistItem) parent.getItemAtPosition(position));
				return true;
			}
			
		});
		
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				if(!isGroupSelected.get(position)){
					view.setBackgroundColor(Color.parseColor("#c0c0c0"));
					isGroupSelected.set(position, true);
				}
				else{				
					view.setBackgroundColor(Color.TRANSPARENT);
					isGroupSelected.set(position, false);
				}	
			}
		});

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(playlistCursor != null){
			playlistCursor.close();
			PlayerController.closeConnectionDB();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(playlistCursor != null){
			playlistCursor.close();
			PlayerController.closeConnectionDB();
		}
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		if(playlistCursor != null){
			playlistCursor.close();
			PlayerController.closeConnectionDB();
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK){
			finish();
			overridePendingTransition(R.anim.left_in, R.anim.right_out);
			return true;
		}
		return super.onKeyDown(keyCode, event); 
	}

	
	private class PlaylistAdapter extends ArrayAdapter<PlaylistItem>{
		private ArrayList<PlaylistItem> namePlaylists;
		private ArrayList<SinglePlaylistItem> listOfTracks;
		private View myView;
		private Button btn1,btn2;
		private Context m_context;
		
		public PlaylistAdapter(Context context, int resource, ArrayList<PlaylistItem> namePlaylists) {
			super(context, resource, namePlaylists);
			this.namePlaylists = namePlaylists;
			this.m_context = context;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			myView	= convertView;
			if (myView == null) {
				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				myView 					= inflater.inflate(R.layout.playlist_activity_item_list_view, null);		
					
			}
			final PlaylistItem playlistSelected = this.namePlaylists.get(position);
			String playlist_name = playlistSelected.getTitle_playlist();
			Log.d(TAG, "dentro getView(): " + playlist_name); 
	
			if(playlist_name != null){
				TextView playlist_title = (TextView)myView.findViewById(R.id.text_view_item);
				playlist_title.setText(playlist_name);
			}
			
			btn1	= (Button)myView.findViewById(R.id.button_pl1);
			btn2	= (Button)myView.findViewById(R.id.button_pl2);
			
			btn1.setFocusable(false);
			btn2.setFocusable(false);
			
			final String id_playlist = id_p.get(position);
			
			btn1.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					PlayerController.playPlaylist(playlistSelected);
				}
			});

			btn2.setOnClickListener(new OnClickListener() {			
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					listOfTracks = playlistSelected.getSongs();
					Intent i = new Intent(m_context, PlaylistTracks.class);
					Bundle b = new Bundle();
					ArrayList<String> id_tracks = playlistSelected.getIdTracks();
					b.putString("id_playlist", id_playlist);
					b.putStringArrayList("id_tracks", id_tracks);
					
					i.putExtras(b);
					
					m_context.startActivity(i);
								
				}
			});

			return myView;
		}
		public PlaylistItem getPlaylistItem(int position){
			if(namePlaylists == null)
				return null;	
			return namePlaylists.get(position);
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
    
    private class DrawerItemClickListener implements ListView.OnItemClickListener{
		private ArrayList<String> delNameP = new ArrayList<String>();

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			if(position == 0){
				startActivityForResult(new Intent(PlaylistActivity2.this, PlaylistAddActivity.class), ADDPLAYLIST);
			}
			
			//Remove Playlist
			else if(position == 1){
				for(int i=0; i < isGroupSelected.size(); i++){
					if(isGroupSelected.get(i)){					
						String name = adapter.getPlaylistItem(i).getTitle_playlist();
						Log.d(TAG, "name: " + name);
						delNameP.add(name);				
					}			
				}	
				if(delNameP.size() >= 1){
					for(int i=0; i< delNameP.size(); i++)
						Log.d(TAG, "delNameP.get(i): " + delNameP.get(i));
					PlayerController.deletePlaylist(delNameP);
					delNameP.clear();
					clearData();
					setListPlaylist();
				}
				else{
					Toast.makeText(PlaylistActivity2.this, "Not any playlist selected!", Toast.LENGTH_SHORT).show();
				}
				
			
			}
			//Update list of Playlist
			else if(position == 2){
				clearData();
				setListPlaylist();
				Toast.makeText(PlaylistActivity2.this, "list of Playlists updated!", Toast.LENGTH_SHORT).show();
			}
			
			//Settings
			else if(position == 3){
				
			}
			
			
			drawer.closeDrawer(drawer_list_view);
		}
	
	}
    
    @Override
	protected void onActivityResult(int requestCode,int resultCode, Intent data){
		if(requestCode == ADDPLAYLIST && resultCode == RESULT_OK){	
			clearData();
			setListPlaylist();
			Toast.makeText(this, "new playlist added!", Toast.LENGTH_SHORT).show();
		}
		
		if(requestCode == ADDPLAYLIST && resultCode == RESULT_CANCELED){
			Toast.makeText(this, "Name playlist duplicated! Please change name of new playlist", Toast.LENGTH_SHORT).show();
		}
		
		if(requestCode == REQUEST_INFO_TRACK && resultCode == RESULT_OK){	
			if(this.track != null){
				Bundle bundle2 = data.getExtras();
				String fileNameTrack	= bundle2.getString("fileName");
				String authorName		= bundle2.getString("author");
				String albumName		= bundle2.getString("albumName");
				String kind				= bundle2.getString("kind");
				int valueOfTrack 		= bundle2.getInt("valueTrack");
				
				
				int idTrack				= Integer.parseInt(track.getId());
				String nameTrack		= track.getnameFile();
	        	String singerTrack		= track.getSinger_name();
	        	String kindTrack		= track.getKind();
	        	String voteTrack		= track.getVote();        	
	        	String albumNameTrack	= track.getAlbumName();
	        	String duration			= track.getDuration();
	        	
	        	if(!nameTrack.equals(fileNameTrack) || !singerTrack.equals(authorName) || !kindTrack.equals(kind) 
						|| !voteTrack.equals(String.valueOf(valueOfTrack)) || !albumNameTrack.equals(albumName)){
							
	        		PlayerController.setTagTrackFromActivityLibrary(idTrack,fileNameTrack,authorName,kind,valueOfTrack,albumName,duration);
	        		clearData();
	        		setListPlaylist();
	        		Toast.makeText(this, "Track's Tags updated!", Toast.LENGTH_SHORT).show();
				}		
			}

		}
		
		
	}
 
    private void setListPlaylist(){
    	playlistCursor 	= PlayerController.getCursorPlaylist();
    	listPlaylistItem= new ArrayList<PlaylistItem>();
		
    	if(playlistCursor != null){
    		id_p = new ArrayList<String>();	
    		playlistCursor.moveToFirst();
			while(!playlistCursor.isAfterLast()){	
				
				if(!id_p.contains(playlistCursor.getString(0))){
					id_p.add(playlistCursor.getString(0));
				}										
				mapperPlaylist.setIdTrackToContentTitle(playlistCursor.getString(2), playlistCursor.getString(7));
				mapperPlaylist.setIdTrackToIdAlbum(playlistCursor.getString(2), playlistCursor.getString(8));
				playlistCursor.moveToNext();			
			}
    	}
    	
    	if(id_p != null){
    		ArrayList<SinglePlaylistItem> tmp_songs;
    		for(int i = 0; i < id_p.size(); i++){
    			playlistCursor.moveToFirst();
				tmp_songs = new ArrayList<SinglePlaylistItem>();
				boolean coverUsed = false;
				String name_playlist 		= "";
				while(!playlistCursor.isAfterLast()){
					if(playlistCursor.getString(0).equals(id_p.get(i))){
						String title		= playlistCursor.getString(7);
						String name_singer 	= playlistCursor.getString(4);
						String kind			= playlistCursor.getString(5);
						String path_track	= playlistCursor.getString(9);
						String _id			= playlistCursor.getString(2);
						String vote			= playlistCursor.getString(6);
						String nameFile		= playlistCursor.getString(3);
						String duration		= playlistCursor.getString(11);
						String albumName	= playlistCursor.getString(10);
						if(!coverUsed){
							name_playlist = playlistCursor.getString(1);
							coverUsed = true;
						}
						String album_id 	= mapperPlaylist.getIdAlbumFromIdTrack(playlistCursor.getString(2));
						SinglePlaylistItem tmp_pl_item= new SinglePlaylistItem(_id, title, name_singer, kind, vote,
								nameFile, album_id, path_track, albumName, duration, this);
						tmp_songs.add(tmp_pl_item);					
					}
					playlistCursor.moveToNext();
				}
				PlaylistItem tmp_play= new PlaylistItem(name_playlist, tmp_songs);
				listPlaylistItem.add(tmp_play);			
    		}
    	}

    	adapter = new PlaylistAdapter(this, R.layout.playlist_activity_item_list_view, listPlaylistItem);
		listview.setAdapter(adapter);
		listview.invalidateViews();
		adapter.notifyDataSetChanged();	
		
		for(int i=0; i < adapter.getCount(); i++){
        	isGroupSelected.add(false);
        }
		
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	clearData();
    	setListPlaylist();
    }
    
    
    
    private void clearData() {
		// TODO Auto-generated method stub
		if(playlistCursor != null){
			playlistCursor.close();
			PlayerController.closeConnectionDB();
		}
		if(listPlaylistItem != null)
			listPlaylistItem.clear();
		if(adapter != null)
			adapter = null;
		if(isGroupSelected != null)
			isGroupSelected.clear();
		if(id_p != null)
			id_p.clear();
	}
	
}
