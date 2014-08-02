package it.borove.playerborove;

import java.util.ArrayList;

import playlistModules.PlaylistAdapter;
import playlistModules.PlaylistItem;
import playlistModules.SinglePlaylistItem;
import android.app.Activity;
import android.app.ListActivity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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
		SinglePlaylistItem tmp_cover= new SinglePlaylistItem("cover", "asd");
		//la scrollview
		ArrayList<SinglePlaylistItem> tmp_songs= new ArrayList<SinglePlaylistItem>();
		for(int i=0;i<15;i++){
			tmp_songs.add(new SinglePlaylistItem("canzone", "asd"));
		}
		//la listview
		for(int i=0;i<3;i++){
			PlaylistItem tmp_play= new PlaylistItem(tmp_cover, tmp_songs);
			items.add(tmp_play);
		}
		
		///FIXME fare debug, possibile che il fatto di aggiungere roba al layout e poi aggiungere il layout alla scrollview sia il problema
		//inserire un po' di stampe per localizzarlo meglio comunque
		
		
		m_adapter= new PlaylistAdapter(this, R.layout.row_playlist, items);
		
		m_listview= (ListView)findViewById(R.id.listview_playlist);
		m_listview.setAdapter(m_adapter);
		
	}
	
	public TableRow addTableRow(String img_name, ArrayList<String> tracks){
		//l'oggetto TableRow che rappresenta una singola riga da inserire
		TableRow table_row= new TableRow(this);
		table_row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 120));
		table_row.setWeightSum(1.0f);
		
			//il layout che contiene la copertina della playlist e il nome
			LinearLayout.LayoutParams layout_row= new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 0.7f);
			LinearLayout row= new LinearLayout(this);
			row.setLayoutParams(layout_row);
			row.setOrientation(LinearLayout.VERTICAL);
			row.setWeightSum(1.0f);
			
				//La TextView che contiene il nome della playlist
				LinearLayout.LayoutParams layout_txt= new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 0.8f);
				TextView txt_pl_name= new TextView(this);
				txt_pl_name.setLayoutParams(layout_txt);
				txt_pl_name.setText("nome playlist");
				
				//L'ImageView che contiene la copertina della playlist
				LinearLayout.LayoutParams img_view= new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0.2f);
				ImageView img_pl= new ImageView(this);
				img_pl.setLayoutParams(img_view);
				int resId= getResources().getIdentifier("nota", "drawable", getPackageName());
				img_pl.setImageResource(resId);
				img_pl.setVisibility(ImageView.VISIBLE);
		
			//La scroll view orizzontale per i brani nella playlist
			LinearLayout.LayoutParams layout_scroll= new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0.3f);
			HorizontalScrollView h_scroll= new HorizontalScrollView(this);
			h_scroll.setLayoutParams(layout_scroll);
			
				//il layout contenitore
				LinearLayout container_scroll= new LinearLayout(this);
				container_scroll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				container_scroll.setOrientation(LinearLayout.VERTICAL);
				
					//for(int i=0;i<10;i++){
						
						//Il layout della singola entry nella scroll view
						LinearLayout.LayoutParams layout_container_entry= new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
						layout_container_entry.setMargins(2, 0, 0, 0);
						LinearLayout container_entry= new LinearLayout(this);
						container_entry.setLayoutParams(layout_container_entry);
						container_entry.setWeightSum(1.0f);
						container_entry.setOrientation(LinearLayout.VERTICAL);
						//La TextView del titolo del singolo brano
						LinearLayout.LayoutParams layout_txt_entry= new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 0.7f);
						TextView txt_entry= new TextView(this);
						txt_entry.setLayoutParams(layout_txt_entry);
						txt_entry.setText("titolo brano");
						//La ImageView del singolo brano
						LinearLayout.LayoutParams layout_img_entry= new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 0.3f);
						ImageView img_entry= new ImageView(this);
						img_entry.setLayoutParams(layout_img_entry);
						int resId_entry= getResources().getIdentifier("nota", "drawable", getPackageName());
						img_entry.setImageResource(resId_entry);
						img_entry.setVisibility(ImageView.VISIBLE);
						
						//aggiungo i brani alla scrollview
						container_entry.addView(txt_entry);
						container_entry.addView(img_entry);
						container_scroll.addView(container_entry);
					//}
			
			h_scroll.addView(container_scroll);

		//aggiungo titolo e copertina della playlist al layout
		row.addView(txt_pl_name);
		row.addView(img_pl);
		
		//aggiungo i la scroll view
		row.addView(h_scroll);
		
		//aggiungo la riga completa
		table_row.addView(row);
		
		return table_row;
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
