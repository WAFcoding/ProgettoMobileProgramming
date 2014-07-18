/**
 * L'oggetto che gestisce il player, le playlist e la libreria
 * 
 * @author BoRoVe
 * @version 0.0, 18/07/2014 
 */
package PlayerManager;

import android.media.MediaPlayer;

public class PlayerController {
	
	private Library library;
	private Track currentPlayingTrack;
	private Playlist currentPlayingPlaylist;
	private MediaPlayer mediaPlayer;
	
	public PlayerController(){
		
		//TODO inizializzazione della libreria da db
		//TODO inizializzazione del media player
	}
	
	public void connectToDB(){
		//TODO implementare la connessione ad database
	}
	
	public void addTrackToPlaylist(Track t, Playlist p){
		
	}
	
	public void addPlaylist(Playlist p){
		this.library.addPlaylist(p);
	}
	/**
	 * Ricerca e restituisce la playlist selezionata in base al nome
	 * @param name String il nome della playlist da cercare
	 * @return una playlist, null se non va a buon fine
	 */
	public Playlist getPlaylistByName(String name){
		
		for(Playlist p : library.getAllPlayList()){
			if(p.getName().equals(name))
				return p;
		}
		
		return null;
	}
	
	//====================================================================================
	//=====================GESTIONE DEL PLAYER============================================
	public void play(){
		
	}
	
	public void pause(){
		
	}
	
	public void stop(){
		
	}
	
	public void next(){
		
	}
	
	public void previous(){
		
	}
	
	public void forward(){
		
	}
	
	public void rewind(){
		
	}
	//====================================================================================
}
