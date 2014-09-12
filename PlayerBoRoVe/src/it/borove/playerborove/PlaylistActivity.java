package it.borove.playerborove;

import java.util.ArrayList;

import playlistModules.PlaylistAdapter;
import playlistModules.PlaylistItem;
import playlistModules.SinglePlaylistItem;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class PlaylistActivity extends Activity {
	
	private String[] choices;
	private DrawerLayout drawer;
	private ListView drawer_list_view;
	private ActionBarDrawerToggle drawer_toggle;
	private CharSequence title, drawer_title;
	
	private ArrayList<PlaylistItem> items;
	private PlaylistAdapter m_adapter;
	private ListView m_listview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_playlist_2);
		
		items= new ArrayList<PlaylistItem>();
		//la cover
		SinglePlaylistItem tmp_cover= new SinglePlaylistItem("cover", " ");
		//la scrollview
		ArrayList<SinglePlaylistItem> tmp_songs= new ArrayList<SinglePlaylistItem>();
		for(int i=0;i<15;i++){
			SinglePlaylistItem tmp_pl_item= new SinglePlaylistItem("canzone", " ");
			tmp_songs.add(tmp_pl_item);
		}
		//la listview
		for(int i=0;i<6;i++){
			PlaylistItem tmp_play= new PlaylistItem(tmp_cover, tmp_songs);
			items.add(tmp_play);
		}

		
		m_adapter= new PlaylistAdapter(this, R.layout.row_playlist, items);
		
		m_listview= (ListView)findViewById(R.id.listview_playlist);
		m_listview.setAdapter(m_adapter);
		
		
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
		
		
        drawer_list_view= (ListView)findViewById(R.id.left_drawer);
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
}