/**
 * 
 */
package playlistModules;

import it.borove.playerborove.AlbumMapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.util.Log;

/**
 * Questa classe rappresenta il singolo elemento della scroll view e il singolo elemento della copertina
 * e' composto da una Stringa che indica il nome e da un'immagine
 * @author "Pasquale Verlotta - pasquale.verlotta@gmail.com"
 *
 */
public class SinglePlaylistItem implements Parcelable{
	
	private String title;
	private String image_path;
	
	private String album_id;
	private Bitmap cover;
	private String path_track;
	private String kind;
	private String vote;
	private String nameFile;
	private String albumName;
	private String duration;
	private String id;
	private String singer_name;

	
	private final int DIM_HEIGHT 	= 200;
	private final int DIM_WIDTH 	= 200;
	private final Uri ART_CONTENT_URI = Uri.parse("content://media/external/audio/albumart");
	private final BitmapFactory.Options sBitmapOptionsCache = new BitmapFactory.Options();
	

	public SinglePlaylistItem(String p_title, String p_image_path){
		setTitle(p_title);
		setImagePath(p_image_path);
		
	}
	
	public SinglePlaylistItem(String _id,String p_title, String singerName, String kind, String vote, String nameFile, 
			String album_id, String path_track, String albumName, String duration, Context context){
		this.id					= _id;
		this.singer_name 		= singerName;
		this.kind 				= kind;
		this.vote				= vote;
		this.nameFile			= nameFile;
		this.album_id 			= album_id;
		this.path_track			= path_track;
		this.albumName			= albumName;
		this.duration			= duration;
		
		setTitle(p_title);
		//setImagePath(p_image_path);
		if(album_id != null)
			cover = getArtworkQuick(context, Integer.parseInt(album_id), DIM_WIDTH, DIM_HEIGHT);
		else
			cover = null;
		
	}
	
	public String getId() {
		return this.id;
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
	public void setSinger_name(String author) {
		this.singer_name = author;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setKind(String kind) {
		this.kind = kind;
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
	                	//Log.d(TAG, "dentro getArtworkQuick: b ï¿½ NULL");
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
	        	Log.e("PlayListItem: ----> ", " dentro getArtworkQuick: Uri è NULL");
	        return null;
	    }

	public String getAlbum_id() {
		return album_id;
	}

	public void setAlbum_id(String album_id) {
		this.album_id = album_id;
	}

	public String getVote() {
		return vote;
	}

	public void setVote(String vote) {
		this.vote = vote;
	}

	public String getnameFile() {
		return this.nameFile;
	}

	public void setnameFile(String nameFile) {
		this.nameFile = nameFile;
	}

	public String getAlbumName() {
		return albumName;
	}

	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(this.id);
		dest.writeString(this.singer_name);
		dest.writeString(this.kind);
		dest.writeString(this.vote);
		dest.writeString(this.nameFile);
		dest.writeString(this.album_id);
		dest.writeString(this.path_track);
		dest.writeString(this.albumName);
		dest.writeString(this.duration);
		dest.writeString(this.title);
		List<Bitmap> bit = new ArrayList<Bitmap>();
		bit.add(getBitmapCover());
		dest.writeTypedList(bit);
	}
	
	
	public SinglePlaylistItem(){
		super();
	}
	public SinglePlaylistItem(Parcel in){
		this();
		readFromParcel(in);
	}
	
	private void readFromParcel(Parcel in){
		this.id					= in.readString();
		this.singer_name 		= in.readString();
		this.kind 				= in.readString();
		this.vote				= in.readString();
		this.nameFile			= in.readString();
		this.album_id 			= in.readString();
		this.path_track			= in.readString();
		this.albumName			= in.readString();
		this.duration			= in.readString();
		this.title				= in.readString();
		
		List<Bitmap> coverSong	= new ArrayList<Bitmap>();
		in.readTypedList(coverSong, Bitmap.CREATOR);
		if(!coverSong.isEmpty())
			this.cover			= coverSong.get(0);
	
	}

	public static final Parcelable.Creator<SinglePlaylistItem> CREATOR = new Parcelable.Creator<SinglePlaylistItem>() {

		@Override
		public SinglePlaylistItem createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new SinglePlaylistItem(source);
		}

		@Override
		public SinglePlaylistItem[] newArray(int size) {
			// TODO Auto-generated method stub
			return new SinglePlaylistItem[size];
		}
		
		 
	};
	
}