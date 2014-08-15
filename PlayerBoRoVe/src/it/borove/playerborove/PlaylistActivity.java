package it.borove.playerborove;

import java.util.ArrayList;

import playlistModules.PlaylistAdapter;
import playlistModules.PlaylistItem;
import playlistModules.SinglePlaylistItem;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ListView;

public class PlaylistActivity extends Activity {
	
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
