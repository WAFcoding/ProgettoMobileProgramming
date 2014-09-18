package it.borove.playerborove;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import playlistModules.PlaylistAdapter;
import playlistModules.PlaylistItem;
import playlistModules.SinglePlaylistItem;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class PlaylistActivity extends Activity {
	
	private String[] choices;
	private DrawerLayout drawer;
	private ListView drawer_list_view;
	private ActionBarDrawerToggle drawer_toggle;
	private CharSequence title, drawer_title;
	
	private ArrayList<PlaylistItem> items;
	private PlaylistAdapter m_adapter;
	private ListView m_listview;
	private Cursor playlistCursor;
	//private HashMap<String,String> map = new HashMap<String,String>();
	private AlbumMapper mapper;
	
	
	private static final String TAG = "PLAYLISTACTIVITY";
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_playlist_2);
		
		playlistCursor 	= PlayerController.getCursorPlaylist();
		items			= new ArrayList<PlaylistItem>();
		mapper 			= new AlbumMapper();
		
		
		
		
		//_id ---> album_id (brano)
		/*HashMap<String,String> tracks = new HashMap<String,String>();
		if(playlistCursor != null){
			playlistCursor.moveToFirst();
			while(!playlistCursor.isAfterLast()){
				
				tracks.put(playlistCursor.getString(playlistCursor.getColumnIndex("pid")), playlistCursor.getString(playlistCursor.getColumnIndex("albumId")));
				playlistCursor.moveToNext();
			}
		}
		*/
		ArrayList<String> id_p = null;
		if(playlistCursor != null){
			id_p = new ArrayList<String>();
			playlistCursor.moveToFirst();
			while(!playlistCursor.isAfterLast()){
				/*if(!id_p.contains(playlistCursor.getString(0)))
					id_p.add(playlistCursor.getString(0));
				if(!map.containsKey(playlistCursor.getString(1))){
					if(!map.containsValue(playlistCursor.getString(7))){
						map.put(playlistCursor.getString(1), playlistCursor.getString(7));
						Log.d(TAG, "HashMap<>  key: " + playlistCursor.getString(1) + " value: " + playlistCursor.getString(7));
					}
					else{
						map.put(playlistCursor.getString(1), "-1");
						Log.d(TAG, "HashMap<>  key: " + playlistCursor.getString(1) +" value: -1");
					}
				}
				*/
				if(!id_p.contains(playlistCursor.getString(0))){
					id_p.add(playlistCursor.getString(0));
				}
				
				mapper.setIdTrackToContentTitle(playlistCursor.getString(2), playlistCursor.getString(7));
				mapper.setIdTrackToIdAlbum(playlistCursor.getString(2), playlistCursor.getString(8));
				
				
				
				playlistCursor.moveToNext();
			}
		}
		
		/*
		//la cover
		SinglePlaylistItem tmp_cover= new SinglePlaylistItem("cover", " ");
		//********************************************************
		playlistCursor.moveToFirst();
		SinglePlaylistItem tmp_cover2 = new SinglePlaylistItem("cover", " ", playlistCursor.getString(7), this);
		
		//*********************************************************
	
		//la scrollview
		ArrayList<SinglePlaylistItem> tmp_songs= new ArrayList<SinglePlaylistItem>();
		if(playlistCursor != null)
			playlistCursor.moveToFirst();
		//for(int i=0; i<playlistCursor.getCount() && !playlistCursor.isAfterLast(); i++){
		while(!playlistCursor.isAfterLast()){
			SinglePlaylistItem tmp_pl_item= new SinglePlaylistItem("canzone", " ", playlistCursor.getString(7), this);
			
			playlistCursor.moveToNext();
			tmp_songs.add(tmp_pl_item);
		}
		//la listview
		playlistCursor.moveToFirst();
		//for(int i=0;i<6;i++){
		for(int i=0; i <id_p.size(); i++){
			//while(!playlistCursor.isAfterLast()){
				PlaylistItem tmp_play= new PlaylistItem(tmp_cover2, tmp_songs);
				items.add(tmp_play);
				playlistCursor.moveToNext();
			//}
		}
		*/
		//SinglePlaylistItem tmp_cover2 = null;
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
							if(!coverUsed){
								name_playlist = playlistCursor.getString(1);								
								coverUsed = true;
							}
							
							//la scrollview
							//String album_id = "-1";
							//if(!playlistCursor.getString(7).equals(null))
							//album_id = map.get(playlistCursor.getString(1));
							String album_id = mapper.getIdAlbumFromIdTrack(playlistCursor.getString(2));
							//String path_track = playlistCursor.getString(8);
							//album_id = playlistCursor.getString(7);
							SinglePlaylistItem tmp_pl_item= new SinglePlaylistItem(title, name_singer, kind, album_id, path_track, this);
							tmp_songs.add(tmp_pl_item);		
						}			
						playlistCursor.moveToNext();
					}
					
					PlaylistItem tmp_play= new PlaylistItem(name_playlist, tmp_songs);
					items.add(tmp_play);	
				}
				m_adapter= new PlaylistAdapter(this, R.layout.playlist_layout, items);
				m_listview= (ListView)findViewById(R.id.listview_playlist);
				m_listview.setAdapter(m_adapter);
		        registerForContextMenu(m_listview);
		}
		
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
    }*/
	private class DrawerItemClickListener implements ListView.OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			//Add Playlist
			if(position == 0){
				startActivity(new Intent(PlaylistActivity.this, PlaylistAddActivity.class));
			}
			//Update
			else if(position == 1){
				
			}
			drawer.closeDrawer(drawer_list_view);
			Toast.makeText(parent.getContext(), "selezionato elemento " + position, Toast.LENGTH_SHORT).show();
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
	
	/*public void getAlbumId(Cursor cursor){
		//String album_id = "-1";
		Cursor localCursor = cursor;
		localCursor.moveToFirst();
		while(!localCursor.isAfterLast()){
			if(!map.containsKey(localCursor.getString(1))){
				if(!map.containsValue(localCursor.getString(7)))
					map.put(localCursor.getString(1), localCursor.getString(7));
				else
					map.put(localCursor.getString(1), "-1");
			}
			else{
				if(!map.get(localCursor.getString(1)).equals("-1"))
						map.put(localCursor.getString(1), localCursor.getString(7));
			}
			
			playlistCursor.moveToNext();
		}
		
		//if(!idFromDb.equals(null))
		//	album_id = map.get(idFromDb);
		
		
		
		//return album_id;
	}
	*/
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
			super.onCreateContextMenu(menu, v, menuInfo);
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.popup_menu_playlist, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    switch (item.getItemId()) {
	        case R.id.playlist_popup_menu_choice1:
	            
	            return true;
	        case R.id.playlist_popup_menu_choice2:
	            
	            return true;
	        case R.id.playlist_popup_menu_choice3:
	            
	            return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}

}