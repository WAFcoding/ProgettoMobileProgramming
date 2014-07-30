/**
 * E' un insieme di brani, puo' essere visto come una
 * playlist o come un album
 * 
 * @autho BoRoVe
 * @version 0.0, 18/07/2014
 */
package PlayerManager;

import it.borove.playerborove.R;

import java.util.ArrayList;

import android.view.KeyEvent;

import android.app.Activity;

public class Playlist{

	private ArrayList<Track> playlist;
	private Track current;
	private String name;
	
	public Playlist(){
		
		setPlaylist(new ArrayList<Track>());
		setCurrent(new Track());
		setName("");
	}
	/**
	 * 
	 * @param p ArrayList<Track> la lista di brani
	 * @param n String il nome univoco della playlist
	 */
	public Playlist(ArrayList<Track> p, String n){
		
		setPlaylist(p);
		setCurrent(new Track());
		setName(n);
	}

	public ArrayList<Track> getPlaylist() {
		return playlist;
	}

	public void setPlaylist(ArrayList<Track> playlist) {
		if(this.playlist != null) this.playlist.clear();
		this.playlist = playlist;
	}

	public Track getCurrent() {
		return current;
	}

	public void setCurrent(Track current) {
		this.current = current;
	}
	/**
	 * Aggiunge un brano alla playlist
	 * @param t Track brano da aggiungere
	 */
	public void addTrack(Track t){
		if(t != null)
			playlist.add(t);
	}
	/**
	 * Rimuove il brano selezionato tramite il titolo dalla playlist
	 * @param title String il titolo del brano da rimuovere
	 * @return il brano appena rimosso
	 */
	public Track removeTrackByString(String title){
		if(title != null && !title.equals("")){
			for(int i=0; i<size(); i++){
				if(title.equals(playlist.get(i).getTitle())){
					return playlist.remove(i);
				}
			}
		}
		return null;
	}
	/**
	 * Imposta il brano attualmente in esecuzione nella playlist
	 * @param t Track il brano da impostare 
	 * @return la posizione del brano, -1 se non va a buon fine
	 */
	public int setCurrentTrack(Track t){
		setCurrent(t);
		for(int i=0; i<size(); i++){
			if(this.current.getTitle().equals(playlist.get(i).getTitle())){
				return i;
			}
		}
		return -1;
	}
	/**
	 * Restituisce il brano successivo a quello corrente nella playlist
	 * @param index int indice del brano corrente acquisisto con setCurrentTrack
	 * @see #setCurrentTrack(Track) 
	 * @return il brano seguente, null se alla fine della playlist
	 * 
	 */
	public Track getNext(int index){
		if(index == size()-1){
			return null;
		}
		index+= 1;
		return playlist.get(index);
	}
	/**
	 * Restituisce il brano successivo a quello corrente nella playlist
	 * @param index int indice del brano corrente acquisito con setCurrentTrack
	 * @see #setCurrentTrack(Track)
	 * @return il brano precedente, null se all'inizio della playlist
	 */
	public Track getPrevious(int index){
		if(index == 0){
			return null;
		}
		index-= 1;
		return playlist.get(index);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int size(){
		return this.playlist.size();
	}
}
