package it.borove.playerborove;

import it.borove.playerborove.R;
import it.borove.playerborove.R.array;
import it.borove.playerborove.R.drawable;
import it.borove.playerborove.R.id;
import it.borove.playerborove.R.layout;
import it.borove.playerborove.R.menu;
import it.borove.playerborove.R.string;

import java.util.ArrayList;
import java.util.HashMap;

import db.SQLiteConnect;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class PlaylistAddActivity extends Activity {

	private ListView m_list_view;
	private EditText m_edit_text;
	private Button btnCreatePlaylist;
	private static MySimpleCursorAdapter adapter;
	private Cursor cursorTracks, cursorPlaylist;
	
	private final static String TAG= "PlaylistAddActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_playlist_add);
		
		m_list_view			= (ListView)findViewById(R.id.listview_add_playlist);
		m_edit_text			= (EditText)findViewById(R.id.edit_text_add_playlist);
		btnCreatePlaylist	= (Button)findViewById(R.id.button_add_playlist);
		
		m_edit_text.setTextColor(Color.parseColor("#ff0000"));
		
		cursorTracks	= PlayerController.getCursorTracks();
		cursorPlaylist	= PlayerController.getCursorPlaylist();
		
		final ArrayList<Boolean> itemColorChanger = new ArrayList<Boolean>();
		if(cursorTracks != null){
				cursorTracks.moveToLast();
			for(int i=0; i< cursorTracks.getCount(); i++)
				itemColorChanger.add(false);

			setAdapter(cursorTracks);
		}
		else
			Toast.makeText(PlaylistAddActivity.this, getResources().getString(R.string.playlist_add_activity_warning1), Toast.LENGTH_SHORT).show();
		
		m_list_view.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				if(!itemColorChanger.get(position)){			
					view.setBackgroundColor(Color.parseColor("#F5DEB3"));
					itemColorChanger.set(position, true);
				}
				else{
					view.setBackgroundColor(Color.TRANSPARENT);
					itemColorChanger.set(position, false);
				}

			}
			
		});
		
		btnCreatePlaylist.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				String namePlaylist = m_edit_text.getText().toString();
				ArrayList<String> idTracksSelected = new ArrayList<String>();
				
				for(int i=0; i < itemColorChanger.size(); i++){
					if(itemColorChanger.get(i)){
						cursorTracks.moveToPosition(i);
						//cursorTracks.move(i);
						
						String idTrack	= cursorTracks.getString(0);
						if(!idTracksSelected.contains(idTrack))
							idTracksSelected.add(idTrack);
						Log.d(TAG, "idTrack: " + idTrack);
					}
				}
				if(idTracksSelected.size() >= 1){
					if(cursorPlaylist != null){
						boolean duplicatedNamePlaylist = false;
						cursorPlaylist.moveToFirst();
						while(!cursorPlaylist.isAfterLast()){
							if(namePlaylist.equals(cursorPlaylist.getString(1))){
								duplicatedNamePlaylist = true;
								break;
							}
							cursorPlaylist.moveToNext();
						}

						if(!duplicatedNamePlaylist){
							Log.d(TAG, "!duplicatedNamePlaylist--> " + namePlaylist);
							PlayerController.addPlaylistToDb(namePlaylist, idTracksSelected);
							setResult(Activity.RESULT_OK);
							finish();
							overridePendingTransition(R.anim.left_in, R.anim.right_out);
						}
						else{
							PlayerController.printToast(getResources().getString(R.string.playlist_add_activity_error1));
						}
					}
					else{

						PlayerController.addPlaylistToDb(namePlaylist, idTracksSelected);
						setResult(Activity.RESULT_OK);	
						finish();
						overridePendingTransition(R.anim.left_in, R.anim.right_out);
					}
				}
			}
		});
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event){
		
		if(keyCode == KeyEvent.KEYCODE_BACK){

			finish();
			overridePendingTransition(R.anim.left_in, R.anim.right_out);
			return true;
		}
		
		return super.onKeyDown(keyCode, event); 
	}
	
	public void setAdapter(Cursor cursor){
		if(cursor != null){
			cursor.moveToFirst();
			String[] from = new String[]{SQLiteConnect.COLUMN_TITLE, SQLiteConnect.COLUMN_SINGER_NAME};
			int[] to = new int[]{R.id.title_textView_add_playlist, R.id.author_textView_add_playlist};	
			adapter = new MySimpleCursorAdapter(this, R.layout.item_listview_add_playlist, cursor, from, to, 0);
			m_list_view.setAdapter(adapter);
		}
		
	}
	
	public class MySimpleCursorAdapter extends SimpleCursorAdapter{
		public MySimpleCursorAdapter(Context context, int layout, Cursor c,
				String[] from, int[] to, int flags) {
			super(context, layout, c, from, to, flags);
			
		}
	}
}
