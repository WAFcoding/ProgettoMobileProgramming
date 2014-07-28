package it.borove.playerborove;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class PlaylistActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_playlist);
		
		//per ogni playlist
		ArrayList<String> tracks= new ArrayList<String>();
		for(int i=0; i<10; i++){
			tracks.add("prova" + i);
		}
		configure("", tracks);
		
	}
	
	public TableRow configure(String img_name, ArrayList<String> tracks){
		
		TableRow table_row= new TableRow(this);
		table_row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		LinearLayout row= new LinearLayout(this);
		row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		row.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout.LayoutParams img_view= new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 0.2f);
		
		ImageView img= new ImageView(this);
		img.setLayoutParams(img_view);
		int resId= getResources().getIdentifier(img_name, "drawable", getPackageName());
		img.setImageResource(resId);
		img.setVisibility(ImageView.VISIBLE);

		LinearLayout.LayoutParams scroll_view= new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 0.8f);
		HorizontalScrollView scroll= new HorizontalScrollView(this);
		scroll.setLayoutParams(scroll_view);
		for(String s : tracks){
			TextView txt= new TextView(this);
			txt.setText(s);
			scroll.addView(txt);
		}
		
		row.addView(img);
		row.addView(scroll);
		
		table_row.addView(row);
		
		return table_row;
		
	}
}
