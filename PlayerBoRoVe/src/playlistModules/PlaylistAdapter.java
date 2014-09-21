/**
 * 
 */
package playlistModules;

import it.borove.playerborove.R;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;
import android.widget.ExpandableListAdapter;
/**
 * @author "Pasquale Verlotta - pasquale.verlotta@gmail.com"
 *
 */

public class PlaylistAdapter extends ArrayAdapter<PlaylistItem> {
	
	private ArrayList<PlaylistItem> items;
	private View m_v, m_vv;
	private final static String TAG= "PlaylistAdapter"; 
	
	private String playlist_name;
	private String song_name;
	private String author_name;
	private HorizontalScrollView tmp_h_scroll;

	//TODO per il menu di modifica delle playlist scorrere il dito sulla copertina
	//e far comparire i pulsanti 

	/**
	 * @param context
	 * @param resource
	 */
	public PlaylistAdapter(Context context, int resource, ArrayList<PlaylistItem> items) {
		super(context, resource, items);
		this.items= items;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		m_v= convertView;
		ViewHolder holder;
		if (m_v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			m_v = inflater.inflate(R.layout.playlist_layout, null);
			
			tmp_h_scroll= (HorizontalScrollView)m_v.findViewById(R.id.horizontalScrollView_row_playlist1);
			//horizon = (CustomScrollView)m_v.findViewById(R.id.horizontalScrollView_row_playlist1);
			//horizon.removeAllViews();
			tmp_h_scroll.removeAllViews();
			
			m_vv=  inflater.inflate(R.layout.row_element_playlist2, null);
		
			holder= new ViewHolder();
			
			holder.layout= new LinearLayout(parent.getContext());
			LinearLayout.LayoutParams layout_item_params= new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			layout_item_params.leftMargin 	= 10;
			layout_item_params.topMargin 	= 10;
			layout_item_params.bottomMargin = 5;
			
			holder.layout.setLayoutParams(layout_item_params);
			holder.layout.setOrientation(LinearLayout.HORIZONTAL);
			holder.layout.setVisibility(View.VISIBLE);
			tmp_h_scroll.setPadding(3, 3, 3, 3);
			tmp_h_scroll.addView(holder.layout);
			
			m_vv.setTag(holder);
		}
		else{
			holder= (ViewHolder)m_vv.getTag();
		}
		
		PlaylistItem item= items.get(position);
		
		if(item != null){

			//visualizzare la copertina
			TextView playlist_title			= (TextView)m_v.findViewById(R.id.textPlaylist_layout);
			LinearLayout.LayoutParams params= new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			params.leftMargin	= 10;
			params.topMargin	= 10;
			params.bottomMargin	= 8;
			playlist_title.setGravity(Gravity.LEFT);
			playlist_title.setTypeface(null, Typeface.BOLD);
			playlist_title.setTextSize(20.0f);
			playlist_title.setTextColor(Color.parseColor("#c0c0c0"));
			playlist_name		= item.getTitle_playlist();
			playlist_title.setText(playlist_name);
			

			//serve per il menu contestuale
			playlist_title.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					Toast.makeText(getContext(), " long_click su nome playist", Toast.LENGTH_SHORT).show();
					return false;
				}
			});
			//caricare i brani nella scroll view 
			ArrayList<SinglePlaylistItem> tmp_songs= item.getSongs();
			
			int size = tmp_songs.size();
			//holder.layout.removeAllViews();
			for(int i=0;i<size;i++){
				holder.layout.addView(new LinearLayout(parent.getContext()));
				holder.layout.setVisibility(View.VISIBLE);
			}
			
			for(int i=0;i<size;i++){
				
				SinglePlaylistItem it= tmp_songs.get(i);
				if(it == null){
					//holder.layout.setVisibility(View.GONE);
					//m_vv.setVisibility(View.GONE);
					//tmp_h_scroll.setVisibility(View.GONE);
					
				}
				LinearLayout layout_item= (LinearLayout)holder.layout.getChildAt(i);
				
				layout_item.setOrientation(LinearLayout.VERTICAL);
				layout_item.removeAllViews();
				
				TextView song_title= new TextView(parent.getContext());
				LinearLayout.LayoutParams song_title_params= new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				//LinearLayout.LayoutParams song_title_params= new LinearLayout.LayoutParams(11, 5);
				song_title_params.leftMargin= 5;
				song_title_params.topMargin= 1;
				song_title_params.rightMargin= 5;
				song_title_params.bottomMargin= 1;
				song_title.setTextColor(Color.parseColor("#00ffff"));
				song_title.setGravity(Gravity.CENTER);
				song_title.setTypeface(null, Typeface.BOLD);
				song_title.setLayoutParams(song_title_params);
				song_name= it.getTitle();	
				if(song_name.length() > 20)
					song_name = song_name.substring(0, 20);				
				song_title.setText(song_name);
							
				TextView author= new TextView(parent.getContext());
				LinearLayout.LayoutParams author_params= new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				//LinearLayout.LayoutParams author_params= new LinearLayout.LayoutParams(60, 60);
				author_params.leftMargin= 5;
				author_params.rightMargin= 5;
				author_params.bottomMargin= 7;
				author.setTextColor(Color.parseColor("#008080"));
				author.setGravity(Gravity.CENTER);
				author.setTypeface(null, Typeface.ITALIC);
				author.setLayoutParams(author_params);
				author_name= it.getSinger_name();	
				if(author_name.length() > 12)
					author_name = author_name.substring(0, 12);	
				author.setText(author_name);
				
				/*ImageView song_cover= new ImageView(parent.getContext());
				LinearLayout.LayoutParams song_cover_params= new LinearLayout.LayoutParams(200, 200);
				song_cover_params.leftMargin= 10;
				song_cover_params.topMargin= 10;
				song_cover_params.rightMargin= 10;
				song_cover_params.bottomMargin= 10;
				song_cover.setLayoutParams(song_cover_params);
				song_cover.setScaleType(ScaleType.FIT_XY);
				song_cover.setLayoutParams(song_cover_params);
				

				Bitmap bit2 = it.getBitmapCover();
				//m_vv.setVisibility(View.VISIBLE);
				//tmp_h_scroll.setVisibility(View.VISIBLE);

				if(bit2 != null){
					song_cover.setImageBitmap(bit2);
				}
				else{
					song_cover.setImageResource(R.drawable.icon);
				}
				
				
				song_cover.setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						
						//TODO inserire qui la chiamata al controllore per attivare la riproduzione del brano
						
						Toast.makeText(getContext(), "anteprima brano: " + song_name, Toast.LENGTH_SHORT).show();
						
						return false;
					}
				});
				
				layout_item.addView(song_cover);
				*/
				layout_item.addView(song_title);
				layout_item.addView(author);

			}
			
		}
		
		return m_v;
	}
	
	private static class ViewHolder{
		public LinearLayout layout;
	}
	
	

}