/**
 * 
 */
package playlistModules;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

/**
 * Questa classe rappresenta il singolo elemento della scroll view e il singolo elemento della copertina
 * e' composto da una Stringa che indica il nome e da un'immagine
 * @author "Pasquale Verlotta - pasquale.verlotta@gmail.com"
 *
 */
public class SinglePlaylistItem {
	
	private String title;
	private String image_path;
	
	private String album_id;
	private Bitmap cover;
	private String path_track;
	private String kind;
	

	private String singer_name;
	
	private final int DIM_HEIGHT 	= 200;
	private final int DIM_WIDTH 	= 200;
	private final Uri ART_CONTENT_URI = Uri.parse("content://media/external/audio/albumart");
	private final BitmapFactory.Options sBitmapOptionsCache = new BitmapFactory.Options();
	
	
	
	public SinglePlaylistItem(String p_title, String p_image_path){
		setTitle(p_title);
		setImagePath(p_image_path);
		
	}
	
	public SinglePlaylistItem(String p_title, String singerName, String kind, String album_id, String path_track, Context context){
		this.singer_name 		= singerName;
		this.kind 				= kind;
		this.album_id 			= album_id;
		this.path_track			= path_track;
		setTitle(p_title);
		//setImagePath(p_image_path);
		cover = getArtworkQuick(context, Integer.parseInt(album_id), DIM_WIDTH, DIM_HEIGHT);
		
	}
	public Bitmap getBitmapCover(){
		return this.cover;
	}

	public String getTitle() {
		return title;
	}
	public String getKind() {
		return kind;
	}

	public String getPath_track() {
		return path_track;
	}

	public String getSinger_name() {
		return singer_name;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImagePath() {
		return image_path;
	}

	public void setImagePath(String image_path) {
		this.image_path = image_path;
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
	                	//Log.d(TAG, "dentro getArtworkQuick: b � NULL");
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
	        	Log.e("PlayListItem: ----> ", " dentro getArtworkQuick: Uri � NULL");
	        return null;
	    }

}