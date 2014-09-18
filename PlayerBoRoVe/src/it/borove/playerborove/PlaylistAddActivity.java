package it.borove.playerborove;

import java.util.ArrayList;
import java.util.HashMap;

import db.SQLiteConnect;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class PlaylistAddActivity extends Activity {

	//navigation drawer
	private String[] choices;
	private DrawerLayout drawer;
	private ListView drawer_list_view;
	private ActionBarDrawerToggle drawer_toggle;
	private CharSequence title, drawer_title;
	//----------------------------------
	private ListView m_list_view;
	private EditText m_edit_text;
	private static MySimpleCursorAdapter adapter;
	private Cursor cursor;
	private HashMap<String,String> map = new HashMap<String,String>();
	private ArrayList<String> playlist_tracks;
	private ArrayList<Integer> playlist_tracks_id;
	
	private final static String TAG= "PlaylistAddActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_playlist_add);
		
		m_list_view= (ListView)findViewById(R.id.listview_add_playlist);
		m_edit_text= (EditText)findViewById(R.id.edit_text_add_playlist);
		
		playlist_tracks= new ArrayList<String>();
		playlist_tracks_id= new ArrayList<Integer>();
		/*
		 * TODO implementare la gestione del doppio nome
		 */
		
		cursor= PlayerController.getCursorTracks();
		if(cursor != null){
			cursor.moveToFirst();
			while(!cursor.isAfterLast()){
				if(!map.containsKey(cursor.getString(0))){
					if(!map.containsValue(cursor.getString(6))){
						map.put(cursor.getString(0), cursor.getString(6));
					}
					else
						map.put(cursor.getString(0), "-1");
				}
				Log.d(TAG, "HashMap<>  key: " + cursor.getString(0) + " value: " + cursor.getString(6));
				cursor.moveToNext();
			}
		}
		
		setAdapter(cursor);
		
		m_list_view.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				int cursor_position= cursor.getPosition();
				int bacward_different= (cursor_position - position)*-1;
				if(cursor.move(bacward_different)){
					
					playlist_tracks.add(cursor.getString(5));
					playlist_tracks_id.add(cursor.getPosition());
					Log.d(TAG, "attuale playlist" + playlist_tracks.toString());
					Toast.makeText(parent.getContext(), "selezionato: " + cursor.getString(5), Toast.LENGTH_SHORT).show();
				}
			}
			
		});

		//il navigation drawer
		title= drawer_title = getTitle();
		
		choices= getResources().getStringArray(R.array.drawer_choice_playlist);
		drawer= (DrawerLayout)findViewById(R.id.drawer_add_playlist);
		
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
		
		
        drawer_list_view= (ListView)findViewById(R.id.left_drawer_add_playlist);
        drawer_list_view.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, choices)); 
        drawer_list_view.setOnItemClickListener(new DrawerItemClickListener());
        
        registerForContextMenu(m_list_view);
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
	/*
	public boolean onKeyDown(int keyCode, KeyEvent event){
		
		if(keyCode == KeyEvent.KEYCODE_BACK){

			finish();
			overridePendingTransition(R.anim.left_in, R.anim.right_out);
			return true;
		}
		
		return super.onKeyDown(keyCode, event); 
	}*/
	
	public void setAdapter(Cursor cursor){
		if(cursor != null){
			cursor.moveToFirst();
			String[] from = new String[]{SQLiteConnect.COLUMN_TITLE};
			int[] to = new int[]{R.id.text_view_add_playlist};
			
			adapter = new MySimpleCursorAdapter(this, R.layout.item_listview_add_playlist, cursor, from, to, 0);
			m_list_view.setAdapter(adapter);
		}
		
	}
	
	public class MySimpleCursorAdapter extends SimpleCursorAdapter{

		private Cursor mCursor;

		public MySimpleCursorAdapter(Context context, int layout, Cursor c,
				String[] from, int[] to, int flags) {
			super(context, layout, c, from, to, flags);
			mCursor = c;
			
			mCursor.moveToFirst();
			while(!mCursor.isAfterLast()){
				if(!map.containsKey(mCursor.getString(0))){
					if(!map.containsValue(mCursor.getString(6)))
						map.put(mCursor.getString(0), mCursor.getString(6));
					else
						map.put(mCursor.getString(0), "-1");
				}
				else{
					if(!map.get(mCursor.getString(0)).equals("-1"))
							map.put(mCursor.getString(0), mCursor.getString(6));
				}
				
				mCursor.moveToNext();
			}
		}
	}
	
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
