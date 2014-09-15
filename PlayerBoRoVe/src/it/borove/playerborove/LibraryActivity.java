package it.borove.playerborove;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import db.SQLiteConnect;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("UseSparseArrays")
public class LibraryActivity extends Activity {
	private final static String TAG = "ACTIVITYLIBRARY";
	private final int RESWIDTH				= 250;
	private final int RESHEIGTH				= 250;
	private final int REQUEST_VOTE_TRACK 	= 101;
	private final int MENU_TRACK			= 102;
	private int idTrack;						
	private static  ListView listView;
	private Button btnUpdate;
	private static MySimpleCursorAdapter adapter;
	private HashMap<String,String> map = new HashMap<String,String>();

	private Cursor cursor;

	private int itemPosition;
	private AlertDialog.Builder popup;
	private boolean isChangedAnything;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_library3);
		
		listView			= (ListView)findViewById(R.id.listView1);
		btnUpdate			= (Button)findViewById(R.id.btnUpdateListView);
		isChangedAnything 	= false;
		idTrack				= 0;
		cursor 				= PlayerController.getCursorTracks();
		if(cursor != null){
			cursor.moveToFirst();
			while(!cursor.isAfterLast()){
				if(!map.containsKey(cursor.getString(0))){
					if(!map.containsValue(cursor.getString(6))){
						map.put(cursor.getString(0), cursor.getString(6));
					}
					else
						map.put(cursor.getString(0), "-1");
				}
				Log.d(TAG, "HashMap<>  key: " + cursor.getString(0) + " value: " + cursor.getString(6));
				cursor.moveToNext();
			}
		}
		
		
		
		
		setAdapter(cursor);
		listener();
		
		
		
		
	}
	@Override
	protected void onActivityResult(int requestCode,int resultCode, Intent data){
		if(requestCode == MENU_TRACK && resultCode == 300){
			Intent trackActivity 	= new Intent(LibraryActivity.this, TrackActivity.class);
			Bundle infoTrack 		= new Bundle();
			Cursor tracks 			= adapter.getCursor();
			boolean reachable 		= tracks.moveToPosition(itemPosition);
			if(reachable){
				idTrack				= tracks.getInt(0);
				String nameTrack	= tracks.getString(1);
				String singerName	= tracks.getString(2);
				String kind			= tracks.getString(3);
				String vote 		= tracks.getString(4);
				String titleTrack 	= tracks.getString(5);
				String albumName	= tracks.getString(8);
				String duration		= tracks.getString(9);
				//String uriTrack	= tracks.getString(7);
				Bitmap albumId 		= null;
				
				if(map.containsKey(String.valueOf(idTrack))){
						albumId		= adapter.getArtworkQuick(getApplicationContext(), Integer.parseInt(map.get(String.valueOf(idTrack))),
												RESWIDTH, RESHEIGTH);				
				}

				infoTrack.putString("nameTrack", nameTrack);
				infoTrack.putString("singerName", singerName);
				infoTrack.putString("kind", kind);
				infoTrack.putString("vote", vote);
				infoTrack.putString("titleTrack", titleTrack);
				infoTrack.putString("albumName", albumName);
				infoTrack.putString("duration", duration);
				
				trackActivity.putExtra("imageAlbum", albumId);
				trackActivity.putExtras(infoTrack);			
				startActivityForResult(trackActivity, REQUEST_VOTE_TRACK);
				
			}
		}
		
		if(requestCode == MENU_TRACK && resultCode == 310){
			popup = new AlertDialog.Builder(LibraryActivity.this);
			popup.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Cursor tracks 		= adapter.getCursor();
					boolean reachable 	= tracks.moveToPosition(itemPosition);
					if(reachable){
						idTrack			= tracks.getInt(0);
						PlayerController.deleteRowTrack(idTrack);
						map.remove(String.valueOf(idTrack));
					}
					isChangedAnything = true;
					
					
				}
			});
			popup.setNegativeButton("No", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
				
				}
				
			});			
			popup.setTitle("Conferma cancellazione");
			popup.setMessage("Sei sicuro?");
			popup.show();
				
		}
			
		if(requestCode == REQUEST_VOTE_TRACK && resultCode == RESULT_OK){
			Bundle bundle2 = data.getExtras();
			
			String fileNameTrack	= bundle2.getString("fileName");
			String authorName		= bundle2.getString("author");
			String albumName		= bundle2.getString("albumName");
			String kind				= bundle2.getString("kind");
			int valueOfTrack 		= bundle2.getInt("valueTrack");
			String duration			= bundle2.getString("duration");
			
			Cursor tracks 			= adapter.getCursor();	
			boolean reachable 		= tracks.moveToPosition(itemPosition);
			if(reachable){
				idTrack				= tracks.getInt(0);
				String nameTrack	= tracks.getString(1);
				String singerName	= tracks.getString(2);
				String oldkind		= tracks.getString(3);
				String vote 		= tracks.getString(4);
				String oldAlbumName	= tracks.getString(8);
				
				if(!nameTrack.equals(fileNameTrack) || !singerName.equals(authorName) || !oldkind.equals(kind) 
						|| !vote.equals(String.valueOf(valueOfTrack)) || !oldAlbumName.equals(albumName))
							isChangedAnything = true;
			}
			
			PlayerController.setTagTrackFromActivityLibrary(idTrack,fileNameTrack,authorName,kind,valueOfTrack,albumName,duration);
		}
		
		
	}
	
	protected void listener(){
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent menuTrackActivity 	= new Intent(LibraryActivity.this, menuTrack.class);
				Cursor tracks = adapter.getCursor();
				boolean reachable = tracks.moveToPosition(position);
				if(reachable){
					itemPosition		= position;
					startActivityForResult(menuTrackActivity, MENU_TRACK);	
				}	
				return false;
			}
			
		});
		
		
		
		btnUpdate.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG, "Update bottone");
				Cursor newCursor = PlayerController.getCursorTracks();
				isChangedAnything = false;
				newCursor.moveToFirst();
				while(!newCursor.isAfterLast()){
					boolean found = false;
					//Log.d(TAG, "newCursor.getString(0): " + newCursor.getString(0));
					cursor.moveToFirst();
					while(!cursor.isAfterLast()){
						if(cursor.getString(0).equals(newCursor.getString(0))){
							found = true;		
							break;
						}

						cursor.moveToNext();
					}
					if(!found){
						isChangedAnything = true;
						break;
					}
				
					newCursor.moveToNext();
				}			
				cursor.moveToFirst();
				while(!cursor.isAfterLast()){
					boolean found = false;
					newCursor.moveToFirst();
					while(!newCursor.isAfterLast()){
						if(cursor.getString(0).equals(newCursor.getString(0))){						
							found = true;
							break;
						}
						
						newCursor.moveToNext();
					}
					if(!found){
						map.remove(cursor.getString(0));
						isChangedAnything = true;
						break;
					}

					cursor.moveToNext();
				}

				if(!isChangedAnything){
					Log.d(TAG, "!isChangedAnything");
					Toast.makeText(LibraryActivity.this, "Il Db è già aggiornato", Toast.LENGTH_SHORT).show();		
				}
				else{
					LibraryActivity.adapter.swapCursor(newCursor);
					setAdapter(newCursor);
					adapter.notifyDataSetChanged();
					isChangedAnything = false;
				}
			}
		});
		
	}

	
	public boolean onKeyDown(int keyCode, KeyEvent event){
		
		if(keyCode == KeyEvent.KEYCODE_BACK){

			finish();
			overridePendingTransition(R.anim.right_in, R.anim.left_out);
			return true;
		}
		
		return super.onKeyDown(keyCode, event); 
	}
	

	
	
	public class MySimpleCursorAdapter extends SimpleCursorAdapter{
		private Context m_context;
		private final int DIM_HEIGHT 	= 70;
		private final int DIM_WIDTH 	= 70;
		private final Uri ART_CONTENT_URI = Uri.parse("content://media/external/audio/albumart");
		private final BitmapFactory.Options sBitmapOptionsCache = new BitmapFactory.Options();

		private Cursor mCursor;

		public MySimpleCursorAdapter(Context context, int layout, Cursor c,
				String[] from, int[] to, int flags) {
			super(context, layout, c, from, to, flags);
			mCursor = c;
			
			mCursor.moveToFirst();
			while(!mCursor.isAfterLast()){
				if(!map.containsKey(mCursor.getString(0))){
					if(!map.containsValue(mCursor.getString(6)))
						map.put(mCursor.getString(0), mCursor.getString(6));
					else
						map.put(mCursor.getString(0), "-1");
				}
				else{
					if(!map.get(mCursor.getString(0)).equals("-1"))
							map.put(mCursor.getString(0), mCursor.getString(6));
				}
				
				mCursor.moveToNext();
			}
	
			// TODO Auto-generated constructor stub
			 m_context = context;
		}

		@Override
		public void setViewImage(ImageView v, String id){
			String album_id = "-1";
			if(!id.equals(null))
				album_id = map.get(id);
			
				Bitmap bitmap = getArtworkQuick(m_context, Integer.parseInt(album_id), DIM_WIDTH, DIM_HEIGHT);
				if(bitmap != null){
					v.setImageBitmap(bitmap);
					v.setAdjustViewBounds(true);	
				}
				else{		    
					v.setImageResource(R.drawable.icon);
					v.setAdjustViewBounds(true);
				}			
		}

		 private Bitmap getArtworkQuick(Context context, int album_id, int w, int h) {
			 
			 if(album_id == -1)
				 return null;
		        w -= 2;
		        h -= 2;
		        
		        ContentResolver res = context.getContentResolver();
		        Uri uri = ContentUris.withAppendedId(ART_CONTENT_URI, album_id);
		        if (uri != null) {
		        	 //Log.e(TAG, "dentro getArtworkQuick: uri: " + uri.toString());
		            ParcelFileDescriptor fd = null;
		            try {
		                fd = res.openFileDescriptor(uri, "r");
		                int sampleSize = 1;
		                // Compute the closest power-of-two scale factor 
		                // and pass that to sBitmapOptionsCache.inSampleSize, which will
		                // result in faster decoding and better quality
		                sBitmapOptionsCache.inJustDecodeBounds = true;
		                BitmapFactory.decodeFileDescriptor(
		                        fd.getFileDescriptor(), null, sBitmapOptionsCache);
		                int nextWidth = sBitmapOptionsCache.outWidth >> 1;
		                int nextHeight = sBitmapOptionsCache.outHeight >> 1;
		                while (nextWidth>w && nextHeight>h) {
		                    sampleSize <<= 1;
		                    nextWidth >>= 1;
		                    nextHeight >>= 1;
		                }

		                sBitmapOptionsCache.inSampleSize = sampleSize;
		                sBitmapOptionsCache.inJustDecodeBounds = false;
		                Bitmap b = BitmapFactory.decodeFileDescriptor(
		                        fd.getFileDescriptor(), null, sBitmapOptionsCache);
		                if (b != null) {
		                    // finally rescale to exactly the size we need
		                    if (sBitmapOptionsCache.outWidth != w || sBitmapOptionsCache.outHeight != h) {
		                        Bitmap tmp = Bitmap.createScaledBitmap(b, w, h, true);
		                        b.recycle();
		                        b = tmp;
		                    }
		                }
		                else{
		                	//Log.d(TAG, "dentro getArtworkQuick: b è NULL");
		                }
		                
		                return b;
		            } catch (FileNotFoundException e) {
		            	 //Log.e(TAG, "FileNotFoundException: " + uri.toString() + " non trovato");
		            } finally {
		                try {
		                    if (fd != null)
		                        fd.close();
		                } catch (IOException e) {
		                }
		            }
		        }
		        else 
		        	Log.e(TAG, "dentro getArtworkQuick: Uri è NULL");
		        return null;
		    }
		
		 
	}
	
	public void setAdapter(Cursor cursor){
		if(cursor != null){
			cursor.moveToFirst();
			/*while(!cursor.isAfterLast()){
				for(int i=0; i< cursor.getColumnCount(); i++){
					Log.d(TAG, cursor.getColumnName(i) + ": " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
				}
				cursor.moveToNext();
			}
			*/
			String[] from = PlayerController.getColumns();
			int[] to = new int[]{R.id.txtImmagine, R.id.txtTitolo, R.id.txtAutore};
			
			LibraryActivity.adapter = new MySimpleCursorAdapter(this, R.layout.item_listview, cursor, from, to, 0);
			listView.setAdapter(LibraryActivity.adapter);
		}
		
	}

}
