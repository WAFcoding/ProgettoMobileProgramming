/**
 * 
 */
package playlistModules;

import it.borove.playerborove.R;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author "Pasquale Verlotta - pasquale.verlotta@gmail.com"
 *
 */

public class PlaylistAdapter extends ArrayAdapter<PlaylistItem> {
	
	private ArrayList<PlaylistItem> items;
	//private final static String TAG= "PlaylistAdapter"; 

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
		
		View v= convertView;

		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.row_playlist, null);
		}
		
		PlaylistItem item= items.get(position);
		
		if(item != null){

			//visualizzare la copertina
			SinglePlaylistItem tmp_cover= item.getCover();
			TextView tmp_title= (TextView)v.findViewById(R.id.textView_row_playlist);
			tmp_title.setText(tmp_cover.getTitle());
			ImageView tmp_cover_image= (ImageView)v.findViewById(R.id.imageView_row_playlist);
			//tmp_cover_image.setImageURI(Uri.parse(tmp_cover.getImagePath()));
			tmp_cover_image.setImageResource(R.drawable.nota_original);
			
			//caricare i brani nella scroll view 
			ArrayList<SinglePlaylistItem> tmp_songs= item.getSongs();
			
			HorizontalScrollView tmp_h_scroll= (HorizontalScrollView)v.findViewById(R.id.horizontalScrollView_row_playlist);
			//LinearLayout tmp_linear= (LinearLayout)v.findViewById(R.id.layout_row_playlist);
			LinearLayout tmp_linear= (LinearLayout) tmp_h_scroll.getChildAt(0);
			
			for(SinglePlaylistItem it : tmp_songs){

				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View vv=  inflater.inflate(R.layout.row_element_playlist, null);
				
				LinearLayout tmp_linear_item= (LinearLayout)vv.findViewById(R.id.layout_row_element_playlist);
				
				//TextView tmp_song_title= (TextView)vv.findViewById(R.id.textView_row_element_playlist);
				TextView tmp_song_title=(TextView) tmp_linear_item.getChildAt(0);
				tmp_song_title.setText(it.getTitle());
				//ImageView tmp_cover_song= (ImageView)vv.findViewById(R.id.imageView_row_element_playlist);
				ImageView tmp_cover_song= (ImageView) tmp_linear_item.getChildAt(1);
				//tmp_cover_song.setImageURI(Uri.parse(it.getImagePath()));
				tmp_cover_song.setImageResource(R.drawable.nota_small);
				
				tmp_linear.addView(tmp_linear_item);
			}
		}
		else{
			return null;
		}
		
		return v;
	}

}
