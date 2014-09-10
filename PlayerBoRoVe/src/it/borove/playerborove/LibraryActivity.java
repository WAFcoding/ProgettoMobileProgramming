package it.borove.playerborove;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;


import db.SQLiteConnect;
import PlayerManager.PlayerController;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
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

@SuppressLint("UseSparseArrays")
public class LibraryActivity extends Activity {
	private final static String TAG = "ACTIVITYLIBRARY";
	private final int RESWIDTH				= 250;
	private final int RESHEIGTH				= 250;
	private final int REQUEST_VOTE_TRACK 	= 101;
	private int idTrack;						
	private static  ListView listView;
	private static MySimpleCursorAdapter adapter;
	private static Context m_c;
	private static Cursor newCursor;
	//private HashMap<Integer,Boolean> idAlbumsArt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_library3);
		
		//controller 	= new PlayerController(this.getApplicationContext());
		listView		= (ListView)findViewById(R.id.listView1);
		//idAlbumsArt = new HashMap<Integer,Boolean>();
		idTrack = 0;
		Cursor cursor 	= PlayerController.getCursorTracks();
		//InfoActivityTrack activitySon = new InfoActivityTrack();
		//InitHashAlbumsArt(cursor);
		setAdapter(cursor);
		listener();
		
		
		
	}
	@Override
	protected void onActivityResult(int requestCode,int resultCode, Intent data){
		if(requestCode == REQUEST_VOTE_TRACK && resultCode == RESULT_OK){
			Bundle bundle2 = data.getExtras();
			int valueOfTrack = bundle2.getInt("valueTrack");
			PlayerController.setVoteTrackFromActivityLibrary(idTrack, valueOfTrack);
		}
	}
	
	protected void listener(){
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				Intent trackActivity = new Intent(LibraryActivity.this, TrackActivity.class);
				Bundle infoTrack = new Bundle();
				Cursor tracks = adapter.getCursor();
				boolean reachable = tracks.moveToPosition(position);
				if(reachable){
					idTrack				= tracks.getInt(0);
					String nameTrack	= tracks.getString(1);
					String singerName	= tracks.getString(2);
					String kind			= tracks.getString(3);
					String vote 		= tracks.getString(4);
					String titleTrack 	= tracks.getString(5);
					Bitmap albumId		= adapter.getArtworkQuick(getApplicationContext(), tracks.getInt(6), RESWIDTH, RESHEIGTH);
					
					//for(int i=0; i< tracks.getColumnCount(); i++)
					//	Log.d(TAG, tracks.getColumnName(i) + ": " + tracks.getString(i));
					
					infoTrack.putString("nameTrack", nameTrack);
					infoTrack.putString("singerName", singerName);
					infoTrack.putString("kind", kind);
					infoTrack.putString("vote", vote);
					infoTrack.putString("titleTrack", titleTrack);
					trackActivity.putExtra("imageAlbum", albumId);

					trackActivity.putExtras(infoTrack);
					startActivityForResult(trackActivity, REQUEST_VOTE_TRACK);	
				}	
				return false;
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
			// TODO Auto-generated constructor stub
			 m_context = context;
		}

		@Override
		public void setViewImage(ImageView v, String id){
				Bitmap bitmap = getArtworkQuick(m_context, Integer.parseInt(id), DIM_WIDTH, DIM_HEIGHT);
				
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
		
		/* @Override
		 public Cursor swapCursor(Cursor newCursor){
			 if (newCursor == mCursor) {
		            return null;
		        }
		        Cursor oldCursor = mCursor;
		        if (oldCursor != null) {
		        	Log.d(TAG, "oldCursor != null");
		            if (mChangeObserver != null) oldCursor.unregisterContentObserver(mChangeObserver);
		            if (mDataSetObserver != null) oldCursor.unregisterDataSetObserver(mDataSetObserver);
		        }
		        mCursor = newCursor;
		        if (newCursor != null) {
		        	Log.d(TAG, "newCursor != null");
		            if (mChangeObserver != null) newCursor.registerContentObserver(mChangeObserver);
		            if (mDataSetObserver != null) newCursor.registerDataSetObserver(mDataSetObserver);
		            mRowIDColumn = newCursor.getColumnIndexOrThrow("_id");
		            mDataValid = true;
		            // notify the observers about the new cursor
		            notifyDataSetChanged();
		           
		            
		        } else {
		        	Log.d(TAG, "(newCursor == null");
		            mRowIDColumn = -1;
		            mDataValid = false;
		            // notify the observers about the lack of a data set
		            notifyDataSetInvalidated();
		        }
	 
			return oldCursor;
			 
		 }
		 */
		 
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
	
	public void onDataSetChanged(){
		
	}
	

	public static void notifyToLibrary(Cursor cursor){
		if(cursor != null){
			cursor.moveToFirst();
			//LibraryActivity.adapter.swapCursor(cursor);
			//Log.d(TAG, "swapCursor");
			//adapter.notifyDataSetChanged();
			Log.d(TAG, "notifyDataSetChanged()");
			//listView.setAdapter(LibraryActivity.adapter);
			
			/*while(!cursor.isAfterLast()){
				for(int i=0; i< cursor.getColumnCount(); i++)
					Log.d(TAG, cursor.getColumnName(i) + ": " + cursor.getString(i));
				cursor.moveToNext();
			}
			Log.d(TAG, "////notifyDataSetChanged()/////");
			newCursor = cursor;
			*/
			LibraryActivity.adapter.swapCursor(cursor);

			
		}
		else
			Log.d(TAG, "notifiyToLibrary(): curosr NULL!");

	}
	
	
	
	
		/*private void InitHashAlbumsArt(Cursor cursor){
		if(this.idAlbumsArt != null){
			if(cursor != null){
				cursor.moveToFirst();
				while(!cursor.isAfterLast()){
					this.idAlbumsArt.put(cursor.getInt(6), false);
					
					cursor.moveToNext();
				}
			}
		}
	}

	private void setTrueHashAlbumsArt(int key){
		if(this.idAlbumsArt.containsKey(key))
			if(!getValueHashAlbumsArt(key))
				this.idAlbumsArt.put(key, true);
	}
	
	
	private boolean getValueHashAlbumsArt(int key){
		return this.idAlbumsArt.get(key);
	
	}
	private boolean deleteHashAlbumArt(int key){
		boolean result = false;
		if(this.idAlbumsArt.containsKey(key))
			result = this.idAlbumsArt.remove(key);
		
		return result;
	}
	
	private void clearHashAlbumArt(){
		this.idAlbumsArt.clear();
	}
	*/
	
}
