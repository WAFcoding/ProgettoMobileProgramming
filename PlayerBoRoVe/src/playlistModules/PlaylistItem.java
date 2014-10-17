/**
 * 
 */
package playlistModules;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.util.Log;

/**
 * Questa classe rappresenta una singola riga della listview, e' composta da un SinglePlaylistItem per il titolo e 
 * la copertina e da un array che rappresenta la scrollview orizzontale nella quale verranno inseriti i brani
 * @author "Pasquale Verlotta - pasquale.verlotta@gmail.com"
 *
 */
public class PlaylistItem {
	
	private SinglePlaylistItem cover;
	private ArrayList<SinglePlaylistItem> songs;
	private String title_playlist;
	private ArrayList<String> id_tracks;
	
	private Cursor cursor;
	private final int DIM_HEIGHT 	= 70;
	private final int DIM_WIDTH 	= 70;
	private final Uri ART_CONTENT_URI = Uri.parse("content://media/external/audio/albumart");
	private final BitmapFactory.Options sBitmapOptionsCache = new BitmapFactory.Options();
	
	public PlaylistItem(SinglePlaylistItem p_cover, ArrayList<SinglePlaylistItem> p_songs){
		
		if(p_cover == null){
			p_cover= new SinglePlaylistItem("", "");
		}
		
		if(p_songs == null){
			p_songs= new ArrayList<SinglePlaylistItem>();
		}
		
		setCover(p_cover);
		setSongs(p_songs);
		
	}
	
	/*
	 * costruttore col cursore
	 */
	
	public  PlaylistItem(String title_playlist, ArrayList<SinglePlaylistItem> p_songs){
		this.title_playlist = title_playlist;
		id_tracks = new ArrayList<String>();
	
		if(p_songs == null){
			p_songs= new ArrayList<SinglePlaylistItem>();
		}

		setSongs(p_songs);
		setIdSongs(p_songs);
	}
	
	private void setIdSongs(ArrayList<SinglePlaylistItem> p_songs) {
		for(int i=0; i< p_songs.size(); i++){
			if(!id_tracks.contains(p_songs.get(i).getId()))
				this.id_tracks.add(p_songs.get(i).getId());
		}
	}
	
	public ArrayList<String> getIdTracks(){
		return this.id_tracks;
	}
	
	public String getTitle_playlist() {
		return title_playlist;
	}
	public void setTitle_playlist(String title_playlist) {
		this.title_playlist = title_playlist;
	}

	public SinglePlaylistItem getCover() {
		return cover;
	}

	public void setCover(SinglePlaylistItem cover) {
		this.cover = cover;
	}
	
	public String getCoverTitle(){
		return getCover().getTitle();
	}
	
	public String getCoverImagepath(){
		return getCover().getImagePath();
	}

	public ArrayList<SinglePlaylistItem> getSongs() {
		return songs;
	}

	public void setSongs(ArrayList<SinglePlaylistItem> songs) {
		this.songs = songs;
	}
	
	public void addSong(SinglePlaylistItem p_song){
		this.songs.add(p_song);
	}
	
	public SinglePlaylistItem getSong(int pos){
		return this.songs.get(pos);
	}
	
	public SinglePlaylistItem removeSong(int pos){
		return this.songs.remove(pos);
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