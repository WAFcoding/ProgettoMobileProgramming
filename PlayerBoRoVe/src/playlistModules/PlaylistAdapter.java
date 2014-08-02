/**
 * 
 */
package playlistModules;

import it.borove.playerborove.R;

import java.util.ArrayList;

import android.content.ClipData.Item;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author "Pasquale Verlotta - pasquale.verlotta@gmail.com"
 *
 */

public class PlaylistAdapter extends ArrayAdapter<String> {
	
	private ArrayList<PlaylistItem> items;

/**
	 * @param context
	 * @param resource
	 */
	public PlaylistAdapter(Context context, int resource, ArrayList<PlaylistItem> items) {
		super(context, resource);
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
			tmp_cover_image.setImageResource(R.drawable.nota);
			
			//caricare i brani nella scroll view 
			ArrayList<SinglePlaylistItem> tmp_songs= item.getSongs();
			
			HorizontalScrollView tmp_h_scroll= (HorizontalScrollView)v.findViewById(R.id.horizontalScrollView_row_playlist);
			LinearLayout tmp_linear= (LinearLayout)v.findViewById(R.id.layout_row_playlist);
			
			for(SinglePlaylistItem it : tmp_songs){
				LinearLayout tmp_linear_item= (LinearLayout)v.findViewById(R.id.layout_row_element_playlist);
				
				TextView tmp_song_title= (TextView)v.findViewById(R.id.textView_row_element_playlist);
				tmp_song_title.setText(it.getTitle());
				ImageView tmp_cover_song= (ImageView)v.findViewById(R.id.imageView_row_element_playlist);
				//tmp_cover_song.setImageURI(Uri.parse(it.getImagePath()));
				tmp_cover_song.setImageResource(R.drawable.nota);
				
				tmp_linear_item.addView(tmp_song_title);
				tmp_linear_item.addView(tmp_cover_song);
				
				tmp_linear.addView(tmp_linear_item);
			}
			
			tmp_h_scroll.addView(tmp_linear);
		}
		else{
			return null;
		}
		
		return v;
	}

}
