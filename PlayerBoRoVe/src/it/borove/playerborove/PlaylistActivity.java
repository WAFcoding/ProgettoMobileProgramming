package it.borove.playerborove;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class PlaylistActivity extends Activity {
	
	private TableLayout table_layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_playlist);
		
		table_layout= (TableLayout)findViewById(R.id.table_playlist);
		
		//il linear layout più esterno
		LinearLayout.LayoutParams layout_container= new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		layout_container.setMargins(2, 2, 2, 2);
		
		LinearLayout container= new LinearLayout(this);
		container.setLayoutParams(layout_container);
		container.setOrientation(LinearLayout.VERTICAL);
		
		container.addView(addTableRow(null, null));
		
		
		//per ogni playlist
		ArrayList<String> tracks= new ArrayList<String>();
		for(int i=0; i<10; i++){
			tracks.add("prova" + i);
		}
		//table_layout= new TableLayout(this);
		//table_layout= (TableLayout) findViewById(R.id.tableLayout1);
		//table_layout.addView(addTableRow("nota.jpg", tracks));
	}
	
	public TableRow addTableRow(String img_name, ArrayList<String> tracks){
		
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
		TextView txt= new TextView(this);
		txt.setText(tracks.get(0));
		scroll.addView(txt);
		/*for(String s : tracks){
			TextView txt= new TextView(this);
			txt.setText(s);
			scroll.addView(txt);
		}*/
		
		row.addView(img);
		row.addView(scroll);
		
		table_row.addView(row);
		
		return table_row;
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event){
		
		if(keyCode == KeyEvent.KEYCODE_BACK){

			finish();
			overridePendingTransition(R.anim.right_out, R.anim.left_in);
			return true;
		}
		
		return super.onKeyDown(keyCode, event); 
	}
}
