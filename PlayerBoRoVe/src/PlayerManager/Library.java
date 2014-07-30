/**
 * Rappresenta l'insieme di tutte le playlist, degli album
 * 
 * @author BoRoVe
 * @version 0.0, 18/07/2014 
 */
package PlayerManager;

import java.util.ArrayList;

public class Library {
	
	private ArrayList<Playlist> allPlayList;
	private int number_of_track, number_of_playlist;
	
	public Library(){
		setAllPlayList(new ArrayList<Playlist>());
		setNumber_of_playlist(-1);
		setNumber_of_track(-1);   
	}
	
	//TODO un costruttore con parametri necessari alla connessione ad database per scaricare le
	//informazioni necessarie
	
	//TODO un sistema di caching per evitare di riscaricare tutto il db ad ogni avvio dell'applicazione
	
	public void updateLibrary(){
		/*
		 * TODO implementare il controllo della presenza dei file in memoria
		 * conviene qua o ad un altro livello??
		 */
	}
	/**
	 * Aggiunge una playlist/album alla libreria
	 * @param playlist 
	 */
	public void addPlaylist(Playlist playlist){
		this.allPlayList.add(playlist);
	}
	/**
	 * Cerca la playlist con il nome corrispondente e la restituisce
	 * @param name String la playlist da cercare
	 * @return la playlist cercata, null se non va a buon fine
	 */
	public Playlist getPlaylist(String name){
		for(Playlist p : allPlayList){
			if(p.getName().equals(name)){
				return p;
			}
		}
		return null;
	}

	public ArrayList<Playlist> getAllPlayList() {
		return allPlayList;
	}
	//FIXME forse non serve ma conviene controllare se funziona cosi o se 
	//bisogna fare il for
	public void setAllPlayList(ArrayList<Playlist> allPlayList) {
		this.allPlayList = allPlayList;
	}

	public int getNumber_of_track() {
		number_of_track= 0;
		for(Playlist p : allPlayList){
			number_of_track+= p.size();
		}
		return number_of_track;
	}

	public void setNumber_of_track(int number_of_track) {
		this.number_of_track = number_of_track;
	}

	public int getNumber_of_playlist() {
		number_of_playlist= allPlayList.size();
		return number_of_playlist;
	}

	public void setNumber_of_playlist(int number_of_playlist) {
		this.number_of_playlist = number_of_playlist;
	}

}
