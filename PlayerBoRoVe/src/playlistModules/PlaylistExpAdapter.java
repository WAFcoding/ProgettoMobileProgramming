package playlistModules;

import it.borove.playerborove.LibraryActivity;
import it.borove.playerborove.PlaylistActivity;
import it.borove.playerborove.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PlaylistExpAdapter extends BaseExpandableListAdapter {
	private Context context;
	private ArrayList<PlaylistItem> items;
	private HashMap<String, ArrayList<SinglePlaylistItem>> listDataItems;
	private ImageView star1;
	private ImageView star2;
	private ImageView star3;
	private ImageView star4;
	private ImageView star5;
	private Button btn;
	private final int REQUEST_TRACKS		= 50;
	private static final String TAG			="PlaylistExpAdapter";
	
	
	
	public PlaylistExpAdapter(Context context, ArrayList<PlaylistItem> items, HashMap<String, ArrayList<SinglePlaylistItem>> listChildData){
		this.context		= context;
		this.items			= items;
		this.listDataItems 	= listChildData;
		
	}
	
	

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return this.items.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		
		return this.listDataItems.get((this.items.get(groupPosition)).getTitle_playlist()).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
			return this.items.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return this.listDataItems.get((this.items.get(groupPosition)).getTitle_playlist()).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		String namePlaylist = "";
		PlaylistItem item = items.get(groupPosition);
		if(item != null){
			namePlaylist	= item.getTitle_playlist();
			Log.d(TAG, "namePlaylist!!!!!: " + namePlaylist);	
		}
		
		else
			Log.d(TAG, "item è NULL: ");
		
		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView 			= inflater.inflate(R.layout.listrow_group, null);
		}
		
		TextView textPlaylist 	= (TextView)convertView.findViewById(R.id.lblListHeader);
		btn						= (Button)convertView.findViewById(R.id.btnAddTracks);
		btn.setFocusable(false);
		
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(context, "button!!!!!!", Toast.LENGTH_SHORT).show();
				Intent i = new Intent(context, LibraryActivity.class);
				Bundle container = new Bundle();
				context.startActivity(i, container);
				
			}
		});
		
		textPlaylist.setTypeface(null, Typeface.BOLD);
		textPlaylist.setText(namePlaylist);
		
	
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		final SinglePlaylistItem track = (SinglePlaylistItem)getChild(groupPosition, childPosition);
		String txtTrack			= track.getTitle();
		String txtAuthorTrack	= track.getSinger_name();
		
		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.listrow_details, null);		
		}
		ImageView cover = (ImageView)convertView.findViewById(R.id.imageView_list_item);
		Bitmap b = track.getBitmapCover();
		if(b != null)
			cover.setImageBitmap(b);
		else
			cover.setImageResource(R.drawable.icon);
		TextView textNameTrack 	= (TextView)convertView.findViewById(R.id.lblListItem);
		TextView textAuthor		= (TextView)convertView.findViewById(R.id.authorItem);
		
		final String vote				= track.getVote();
		
		star1	= (ImageView)convertView.findViewById(R.id.star10);
		star2 	= (ImageView)convertView.findViewById(R.id.star20);
		star3 	= (ImageView)convertView.findViewById(R.id.star30);
		star4 	= (ImageView)convertView.findViewById(R.id.star40);
		star5 	= (ImageView)convertView.findViewById(R.id.star50);
		
		
		if(!vote.equals(null)){
			if(vote.equals("0")){
				star1.setImageResource(R.drawable.star_silver);
				star2.setImageResource(R.drawable.star_silver);
				star3.setImageResource(R.drawable.star_silver);
				star4.setImageResource(R.drawable.star_silver);
				star5.setImageResource(R.drawable.star_silver);
				
			}
			if(vote.equals("1")){
				star1.setImageResource(R.drawable.star_gold);
				star2.setImageResource(R.drawable.star_silver);
				star3.setImageResource(R.drawable.star_silver);
				star4.setImageResource(R.drawable.star_silver);
				star5.setImageResource(R.drawable.star_silver);
			}
			else if(vote.equals("2")){
				star1.setImageResource(R.drawable.star_gold);
				star2.setImageResource(R.drawable.star_gold);
				star3.setImageResource(R.drawable.star_silver);
				star4.setImageResource(R.drawable.star_silver);
				star5.setImageResource(R.drawable.star_silver);
				
			}
			else if(vote.equals("3")){
				star1.setImageResource(R.drawable.star_gold);
				star2.setImageResource(R.drawable.star_gold);
				star3.setImageResource(R.drawable.star_gold);
				star4.setImageResource(R.drawable.star_silver);
				star5.setImageResource(R.drawable.star_silver);
			}
			else if(vote.equals("4")){
				star1.setImageResource(R.drawable.star_gold);
				star2.setImageResource(R.drawable.star_gold);
				star3.setImageResource(R.drawable.star_gold);
				star4.setImageResource(R.drawable.star_gold);
				star5.setImageResource(R.drawable.star_silver);
			}
			else if(vote.equals("5")){
				star1.setImageResource(R.drawable.star_gold);
				star2.setImageResource(R.drawable.star_gold);
				star3.setImageResource(R.drawable.star_gold);
				star4.setImageResource(R.drawable.star_gold);
				star5.setImageResource(R.drawable.star_gold);
			}
			
		}

		textNameTrack.setText(txtTrack);
		textAuthor.setText(txtAuthorTrack);
		

		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

}
