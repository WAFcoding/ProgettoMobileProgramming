
package PlayerManager;

import android.util.Log;
/**
 * Rappresenta il singolo brano
 * 
 * @author BoRoVe
 * @version 0.0, 18/07/2014
 */
public class Track {

	private String title, cover_path, //eredita il percorso dall'album di cui fa parte, se c'e'
				   singer, file_path, kind; //eredita il genere dall'album di cui fa parte, se c'e'
	private Duration duration;
	
	private final static String LOG= "TRACK";
	
	public Track(){
		
		this.title = this.cover_path = this.singer = this.file_path = this.kind = "";
		this.duration= new Duration(0,0,0);
	}
		/**
		 * 
		 * Costruttore
		 * 
		 * @param title String il titolo del brano
		 * @param duration Duration la durata del brano
		 * @see Duration
		 * @param cover_path String il percorso del file della cover, ereditato dall'album
		 * @param singer String il cantante
		 * @param file_path String il percorso del file audio
		 * @param kind String il genere musicale del brano
		 */
	public Track(String title, Duration duration, String cover_path, String singer, String file_path, String kind){
		
		if(title != null && !title.equals("")){
			this.setTitle(title);
		}
		else{
			Log.d(LOG,"titolo mancante");
		}
		
		if(duration != null){
			this.setDuration(duration);
		}
		else{
			Log.d(LOG,"duration mancante");
		}
		
		if(file_path != null && !file_path.equals("")){
			this.setFile_path(file_path);
		}
		else{
			Log.d(LOG,"file_path mancante");
		}
		
		this.setCover_path(cover_path);
		this.setSinger(singer);
		this.setKind(kind);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCover_path() {
		return cover_path;
	}

	public void setCover_path(String cover_path) {
		this.cover_path = cover_path;
	}

	public String getSinger() {
		return singer;
	}

	public void setSinger(String singer) {
		this.singer = singer;
	}

	public String getFile_path() {
		return file_path;
	}

	public void setFile_path(String file_path) {
		this.file_path = file_path;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}
	
	
}
