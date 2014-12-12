package it.borove.playerborove;

import java.util.HashMap;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class AlbumMapper{
	private static final String TAG = "ALBUMMAPPER";
	
	// id Track --> content_title
	private HashMap<String,String> idTrackToContentTitle;
	// id Track --> id Album
	private HashMap<String,String> IdTrackToIdAlbum;

	public AlbumMapper(){
		idTrackToContentTitle = new HashMap<String,String>();
		IdTrackToIdAlbum = new HashMap<String,String>();
	}
	
	/**
	 * id_track ---> content_title
	 * 
	 * @param idTrack		id del brano
	 * @param contentTitle	titolo del brano
	 */
	
	public void setIdTrackToContentTitle(String idTrack, String contentTitle){
		if(!idTrack.equals(null) && !contentTitle.equals(null))
			try{
				if(!this.idTrackToContentTitle.containsKey(idTrack) && !this.idTrackToContentTitle.containsValue(idTrack)){
					this.idTrackToContentTitle.put(idTrack, contentTitle);
					//Log.d(TAG, "Aggiunto track in IdTrackToContentTitle: " + idTrack + " " + contentTitle);
				}
			}catch(Exception e){e.printStackTrace();}
	}
	
	/**
	 * id_track ---> id_album
	 * 
	 * @param idTrack	id del brano
	 * @param idAlbum	id dell'album
	 */
	
	public void setIdTrackToIdAlbum(String idTrack, String idAlbum){
		if(!idTrack.equals(null) && !idAlbum.equals(null))
			try{
				if(!this.IdTrackToIdAlbum.containsKey(idTrack))
					if (!this.IdTrackToIdAlbum.containsValue(idAlbum)){
						this.IdTrackToIdAlbum.put(idTrack, idAlbum);
						//Log.d(TAG, "Aggiunto track in IdTrackToIdAlbum: " + idTrack + " " + idAlbum);
					}
					else{
						this.IdTrackToIdAlbum.put(idTrack, "-1");
						//Log.d(TAG, "Aggiunto track in IdTrackToIdAlbum: " + idTrack + " -1");
					}
			}catch(Exception e){e.printStackTrace();}
	}
	
	public String getContentTitleFromIdTrack(String keyIdTrack){
		if(!keyIdTrack.equals(null))
			return this.idTrackToContentTitle.get(keyIdTrack);
		else return null;
	}
	
	public String getIdAlbumFromIdTrack(String keyNameAlbum){
		if(!keyNameAlbum.equals(null)){
			//Log.d(TAG,"getidalbum da proprio NULL");
			return this.IdTrackToIdAlbum.get(keyNameAlbum);
		}
		else return null;
	}
	
	public HashMap<String,String> getIdTrackToContentTitle(){
		return this.idTrackToContentTitle;
	}
	
	public HashMap<String,String> getIdTrackToIdAlbum(){
		return this.IdTrackToIdAlbum;
	}
	
	public void setHashMapIdTrackToContentTitle(HashMap<String,String> temp){
		this.idTrackToContentTitle = temp;
	}
	public void setHashMapIdTrackToIdAlbum(HashMap<String,String> temp){
		this.IdTrackToIdAlbum = temp;
	}

}
