package it.borove.playerborove;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import playlistModules.Group;
import playlistModules.MyExpandableListAdapter;
import playlistModules.PlaylistAdapter;
import playlistModules.PlaylistExpAdapter;
import playlistModules.PlaylistItem;
import playlistModules.SinglePlaylistItem;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.Gallery;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class PlaylistActivity extends Activity {
	
	private String[] choices;
	private DrawerLayout drawer;
	private ListView drawer_list_view;
	private ActionBarDrawerToggle drawer_toggle;
	private CharSequence title, drawer_title;

	private PlaylistExpAdapter expAdapter;
	private ExpandableListView expListView;
	private ArrayList<PlaylistItem> items;
    private HashMap<String, ArrayList<SinglePlaylistItem>> playlistMap;
    private final ArrayList<Boolean> isGroupSelected = new ArrayList<Boolean>();
    
	private int groupPos;
	private SinglePlaylistItem track;

	private ArrayList<String> id_p = null;
	private PlaylistAdapter m_adapter;
	private ListView m_listview;
	//private ExpandableListView expView;
	private Cursor playlistCursor, cursorTracks;
	//private HashMap<String,String> map = new HashMap<String,String>();
	private AlbumMapper mapper;
	private final int ADDPLAYLIST	= 270;
	MyExpandableListAdapter listAdapter;
	private final int REQUEST_INFO_TRACK = 	400;
	/*private ImageView star1;
	private ImageView star2;
	private ImageView star3;
	private ImageView star4;
	private ImageView star5;
    */
    
	
	
	
	
	private static final String TAG = "PLAYLISTACTIVITY";
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.expandable_list);
		
        expListView = (ExpandableListView) findViewById(R.id.expandableListView1);

		//playlistCursor 	= PlayerController.getCursorPlaylist();
		//playlistMap		= new HashMap<String, ArrayList<SinglePlaylistItem>>();
		//items			= new ArrayList<PlaylistItem>();
		mapper 			= new AlbumMapper();
		
		 
		
		
		setListPlaylist();
		
		
		
		
		/*if(playlistCursor != null){
			id_p = new ArrayList<String>();
			playlistCursor.moveToFirst();
			while(!playlistCursor.isAfterLast()){
				
				if(!id_p.contains(playlistCursor.getString(0))){
					id_p.add(playlistCursor.getString(0));
				}
				
				mapper.setIdTrackToContentTitle(playlistCursor.getString(2), playlistCursor.getString(7));
				mapper.setIdTrackToIdAlbum(playlistCursor.getString(2), playlistCursor.getString(8));

				playlistCursor.moveToNext();
			}
		}
		*/
		/*else{
			
			cursorTracks = PlayerController.getCursorTracks();
			if(cursorTracks != null){
				cursorTracks.moveToFirst();
				while(!cursorTracks.isAfterLast()){
									
					cursorTracks.moveToNext();
				}				
			}

		}
		*/
		/*

			if(id_p != null){
				ArrayList<SinglePlaylistItem> tmp_songs;
				for(int i = 1; i <= id_p.size(); i++){
					Log.d(TAG, "id_p.size()" + id_p.size());
					playlistCursor.moveToFirst();
					boolean coverUsed = false;
					String name_playlist 		= "";
					tmp_songs = new ArrayList<SinglePlaylistItem>();
					while(!playlistCursor.isAfterLast()){
						if(playlistCursor.getString(0).equals(String.valueOf(i))){
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
							//la scrollview
							String album_id = mapper.getIdAlbumFromIdTrack(playlistCursor.getString(2));
							SinglePlaylistItem tmp_pl_item= new SinglePlaylistItem(_id, title, name_singer, kind, vote,
									nameFile, album_id, path_track, albumName, duration, this);
							tmp_songs.add(tmp_pl_item);		
						}			
						playlistCursor.moveToNext();
					}
					PlaylistItem tmp_play= new PlaylistItem(name_playlist, tmp_songs);
					items.add(tmp_play);
					this.playlistMap.put(name_playlist, tmp_songs);			
				}
				
				this.expAdapter = new PlaylistExpAdapter(this, items, playlistMap);
				//m_adapter= new PlaylistAdapter(this, R.layout.playlist_layout, items);
				//m_listview= (ListView)findViewById(R.id.listview_playlist);
				//m_listview.setAdapter(m_adapter);
				expListView.setAdapter(expAdapter);
				registerForContextMenu(expListView);
		        //registerForContextMenu(m_listview);
				
				for(int i=0; i < expAdapter.getGroupCount(); i++){
		        	isGroupSelected.add(false);
		        }
		        
		}
		*/	
		
		//il navigation drawer
		title= drawer_title = getTitle();
		choices= getResources().getStringArray(R.array.drawer_choice_playlist);
		drawer= (DrawerLayout)findViewById(R.id.drawer_playlist);
		drawer_toggle= new ActionBarDrawerToggle(this, drawer, R.drawable.ic_launcher, 
												R.string.drawer_open, R.string.drawer_close){
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

	
        expListView.setOnGroupClickListener(new OnGroupClickListener() {		
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				// TODO Auto-generated method stub
				
				if(!isGroupSelected.get(groupPosition)){
					v.setBackgroundColor(Color.parseColor("#c0c0c0"));
					isGroupSelected.set(groupPosition, true);
				}
				else{
					
					v.setBackgroundResource(R.drawable.ellipse_button);
					isGroupSelected.set(groupPosition, false);
				}
			
				return false;
			}
		});
        
        expListView.setOnChildClickListener(new OnChildClickListener() {		
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
		
				return false;
			}
		});
	
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

    /* Called whenever we call invalidateOptionsMenu() */
    /*@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = drawer.isDrawerOpen(drawer_list_view);
        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
	*/
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener{
		private ArrayList<String> delNameP = new ArrayList<String>();

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
			//Add Playlist
			if(position == 0){
				startActivityForResult(new Intent(PlaylistActivity.this, PlaylistAddActivity.class), ADDPLAYLIST);
			}
			
			//Remove Playlist
			else if(position == 1){
				for(int i=0; i < isGroupSelected.size(); i++){
					if(isGroupSelected.get(i)){					
						String name = ((PlaylistItem)expAdapter.getGroup(i)).getTitle_playlist();
						delNameP.add(name);				
					}
					
				}
				
				if(delNameP.size() >= 1){
					PlayerController.deletePlaylist(delNameP);
					clearData();
					setListPlaylist();
				}
				else{
					Toast.makeText(PlaylistActivity.this, "Not any playlist selected!", Toast.LENGTH_SHORT).show();
				}
				
				
			}
			//Update list of Playlist
			else if(position == 2){
				clearData();
				setListPlaylist();
				Toast.makeText(PlaylistActivity.this, "list of Playlists updated!", Toast.LENGTH_SHORT).show();
			}
			
			//Settings
			else if(position == 3){
				
			}
			
			
			drawer.closeDrawer(drawer_list_view);
			//Toast.makeText(parent.getContext(), "selezionato elemento " + position, Toast.LENGTH_SHORT).show();
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

	
	
	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
			super.onCreateContextMenu(menu, v, menuInfo);
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.popup_menu_playlist, menu);
			
			ExpandableListView.ExpandableListContextMenuInfo info =
				    (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
	
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo();
	    groupPos = 0;
	    int childPos = 0;
	    
	    int type = ExpandableListView.getPackedPositionType(info.packedPosition);
	    if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
		      groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition);
		      childPos = ExpandableListView.getPackedPositionChild(info.packedPosition);
		}
	    String namePlaylist 		= ((PlaylistItem)expAdapter.getGroup(groupPos)).getTitle_playlist();
	    this.track					= (SinglePlaylistItem)expAdapter.getChild(groupPos, childPos);
	    String idTrack				= track.getId();
	    
	    switch (item.getItemId()) {
	        case R.id.playlist_popup_menu_choice1: {          
	        	PlayerController.setPlaylistOnDb(namePlaylist, idTrack, true);
	        	setListPlaylist();
	        	expListView.expandGroup(groupPos);
      	
	            return true;
	        }
	        case R.id.playlist_popup_menu_choice2: {
	        	
	        	Intent toTrackActivity	= new Intent(PlaylistActivity.this, TrackActivity.class);
	        	Bundle infoTrack 		= new Bundle();
	        	
	        	String nameTrack		= track.getTitle();
	        	String singerTrack		= track.getSinger_name();
	        	String kindTrack		= track.getKind();
	        	String nameFileTrack	= track.getnameFile();
	        	String voteTrack		= track.getVote();
	        	String durationTrack	= track.getDuration();	        	
	        	String albumNameTrack	= track.getAlbumName();
	        	Bitmap cover			= track.getBitmapCover();
	        	
	        	infoTrack.putString("nameTrack", nameFileTrack);
				infoTrack.putString("singerName", singerTrack);
				infoTrack.putString("kind", kindTrack);
				infoTrack.putString("vote", voteTrack);
				infoTrack.putString("titleTrack", nameTrack);
				infoTrack.putString("albumName", albumNameTrack);
				infoTrack.putString("duration", durationTrack);
				
				toTrackActivity.putExtra("imageAlbum", cover);
				toTrackActivity.putExtras(infoTrack);			
				startActivityForResult(toTrackActivity, REQUEST_INFO_TRACK);
	        	
	        	
	        	
	        }   
	            return true;
	        case R.id.playlist_popup_menu_choice3:
	            
	            return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}
	

	@Override
	protected void onActivityResult(int requestCode,int resultCode, Intent data){
		if(requestCode == ADDPLAYLIST && resultCode == RESULT_OK){	
			clearData();
			setListPlaylist();
			Toast.makeText(this, "List of Playlists updated!", Toast.LENGTH_SHORT).show();
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
	        		setListPlaylist();
		        	expListView.expandGroup(groupPos);
	        		Toast.makeText(this, "Track's Tags updated!", Toast.LENGTH_SHORT).show();
				}		
			}

		}
		
		
	}
	
	private void clearData() {
		// TODO Auto-generated method stub
		if(playlistCursor != null)
			playlistCursor.close();
		if(items != null)
			items.clear();
		if(playlistMap != null)
			playlistMap.clear();
		if(id_p != null)
			id_p.clear();
		if(isGroupSelected != null)
			isGroupSelected.clear();
		
	}

	private void setListPlaylist(){
		
		
		
		

		playlistCursor 	= PlayerController.getCursorPlaylist();
		items			= new ArrayList<PlaylistItem>();
		playlistMap		= new HashMap<String, ArrayList<SinglePlaylistItem>>();
		
		if(playlistCursor != null){
			id_p = new ArrayList<String>();
			playlistCursor.moveToFirst();
			while(!playlistCursor.isAfterLast()){
		
				if(!id_p.contains(playlistCursor.getString(0))){
					id_p.add(playlistCursor.getString(0));
				}
				
				mapper.setIdTrackToContentTitle(playlistCursor.getString(2), playlistCursor.getString(7));
				mapper.setIdTrackToIdAlbum(playlistCursor.getString(2), playlistCursor.getString(8));

				playlistCursor.moveToNext();
			}
		}
		
		
		if(id_p != null){
			ArrayList<SinglePlaylistItem> tmp_songs;
			for(int i = 1; i <= id_p.size(); i++){
				//Log.d(TAG, "id_p.size()" + id_p.size());
				playlistCursor.moveToFirst();
				boolean coverUsed = false;
				String name_playlist 		= "";
				tmp_songs = new ArrayList<SinglePlaylistItem>();
				while(!playlistCursor.isAfterLast()){
					if(playlistCursor.getString(0).equals(String.valueOf(i))){
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

						//for(int j=0; j< playlistCursor.getColumnCount(); j++)
						//	Log.d(TAG,"**--** " + playlistCursor.getColumnName(j) + ": " + playlistCursor.getString(j));
						//la scrollview

						String album_id = mapper.getIdAlbumFromIdTrack(playlistCursor.getString(2));
						//Log.d(TAG,"album_id ??????????: " + album_id);

						SinglePlaylistItem tmp_pl_item= new SinglePlaylistItem(_id, title, name_singer, kind, vote,
								nameFile, album_id, path_track, albumName, duration, this);
						tmp_songs.add(tmp_pl_item);		
					}			
					playlistCursor.moveToNext();
				}
				
				//Log.d(TAG,"tmp_songs.size(): ---> " + tmp_songs.size());
					PlaylistItem tmp_play= new PlaylistItem(name_playlist, tmp_songs);
					items.add(tmp_play);
					this.playlistMap.put(name_playlist, tmp_songs);

			}
			this.expAdapter = new PlaylistExpAdapter(this, items, playlistMap);
			
			expListView.setAdapter(expAdapter);
			expListView.invalidateViews();
			registerForContextMenu(expListView);
			expAdapter.notifyDataSetChanged();
			
			for(int i=0; i < expAdapter.getGroupCount(); i++){
	        	isGroupSelected.add(false);
	        }

		}
		
		
	}
	
	
	
	
}