/**
 * 
 */
package playlistModules;

import it.borove.playerborove.R;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
			m_v = inflater.inflate(R.layout.row_playlist, null);
			
			HorizontalScrollView tmp_h_scroll= (HorizontalScrollView)m_v.findViewById(R.id.horizontalScrollView_row_playlist);
			tmp_h_scroll.removeAllViews();
			
			m_vv=  inflater.inflate(R.layout.row_element_playlist, null);
			//tmp_linear_item= (LinearLayout)m_vv.findViewById(R.id.layout_row_element_playlist);
			
			
			holder= new ViewHolder();
			
			holder.layout= new LinearLayout(parent.getContext());
			LinearLayout.LayoutParams layout_item_params= new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
			holder.layout.setLayoutParams(layout_item_params);
			holder.layout.setOrientation(LinearLayout.HORIZONTAL);
			
			tmp_h_scroll.addView(holder.layout);
			
			m_vv.setTag(holder);
		}
		else{
			holder= (ViewHolder)m_vv.getTag();
		}
		
		PlaylistItem item= items.get(position);
		
		if(item != null){

			//visualizzare la copertina
			SinglePlaylistItem tmp_cover= item.getCover();
			TextView tmp_title= (TextView)m_v.findViewById(R.id.textView_row_playlist);
			playlist_name= tmp_cover.getTitle();
			tmp_title.setText(playlist_name);
			//FIXME forse tocca usare la bitmap image per settare l'imageview
			ImageView tmp_cover_image= (ImageView)m_v.findViewById(R.id.imageView_row_playlist);
			//tmp_cover_image.setImageURI(Uri.parse(tmp_cover.getImagePath()));
			tmp_cover_image.setImageResource(R.drawable.nota_original);
			tmp_cover_image.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {

					//TODO inserire qui la chiamata al controllore per attivare la riproduzione del brano
					Toast.makeText(getContext(), "anteprima playlist : " + playlist_name, Toast.LENGTH_SHORT).show();
					
					return false;
				}
			});
		
			//caricare i brani nella scroll view 
			ArrayList<SinglePlaylistItem> tmp_songs= item.getSongs();
			
			int size = tmp_songs.size();
			//holder.layout.removeAllViews();
			for(int i=0;i<size;i++){
				holder.layout.addView(new LinearLayout(parent.getContext()));
			}
			
			for(int i=0;i<size;i++){
				
				SinglePlaylistItem it= tmp_songs.get(i);
				//LinearLayout layout_item= new LinearLayout(parent.getContext());
				LinearLayout layout_item= (LinearLayout)holder.layout.getChildAt(i);
				LinearLayout.LayoutParams layout_item_params= new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				layout_item_params.leftMargin= 3;
				layout_item_params.topMargin= 2;
				layout_item_params.rightMargin= 3;
				layout_item.setLayoutParams(layout_item_params);
				layout_item.setOrientation(LinearLayout.VERTICAL);
				layout_item.removeAllViews();
				
				TextView song_title= new TextView(parent.getContext());
				LinearLayout.LayoutParams song_title_params= new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				//song_title_params.leftMargin= 3;
				//song_title_params.topMargin= 2;
				//song_title_params.rightMargin= 3;
				song_title.setLayoutParams(song_title_params);
				song_name= it.getTitle();
				song_title.setText(song_name);
				layout_item.addView(song_title);
				
				ImageView song_cover= new ImageView(parent.getContext());
				LinearLayout.LayoutParams song_cover_params= new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				//song_cover_params.leftMargin= 3;
				//song_cover_params.topMargin= 2;
				//song_cover_params.rightMargin= 3;
				song_cover.setLayoutParams(song_cover_params);
				song_cover.setScaleType(ScaleType.FIT_XY);
				song_cover.setImageResource(R.drawable.nota_small);
				
				song_cover.setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						
						//TODO inserire qui la chiamata al controllore per attivare la riproduzione del brano
						
						Toast.makeText(getContext(), "anteprima brano: " + song_name, Toast.LENGTH_SHORT).show();
						
						return false;
					}
				});
				
				layout_item.addView(song_cover);

				//holder.layout.addView(layout_item);
			}
			/*
			for(SinglePlaylistItem it : tmp_songs){
				LinearLayout layout_item= new LinearLayout(parent.getContext());
				LinearLayout.LayoutParams layout_item_params= new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				layout_item.setLayoutParams(layout_item_params);
				layout_item.setOrientation(LinearLayout.VERTICAL);
				layout_item.setWeightSum(1.0f);
				
				TextView song_title= new TextView(parent.getContext());
				LinearLayout.LayoutParams song_title_params= new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				song_title.setLayoutParams(song_title_params);
				song_title.setText(it.getTitle());
				layout_item.addView(song_title);
				
				ImageView song_cover= new ImageView(parent.getContext());
				LinearLayout.LayoutParams song_cover_params= new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				song_cover.setLayoutParams(song_cover_params);
				song_cover.setScaleType(ScaleType.FIT_XY);
				song_cover.setImageResource(R.drawable.nota_small);
				layout_item.addView(song_cover);
				
				tmp_linear.addView(layout_item);
				
			}*/
		}
		
		return m_v;
	}
	
	private static class ViewHolder{
		//private ArrayList<LinearLayout> layouts;
		public LinearLayout layout;
	}

}