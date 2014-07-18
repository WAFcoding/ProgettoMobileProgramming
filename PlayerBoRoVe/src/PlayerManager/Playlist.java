/**
 * E' un insieme di brani, puo' essere visto come una
 * playlist o come un album
 * 
 * @autho BoRoVe
 * @version 0.0, 18/07/2014
 */
package PlayerManager;

import java.util.ArrayList;

public class Playlist {

	private ArrayList<Track> playlist;
	private Track current;
	
	public Playlist(){
		
		setPlaylist(new ArrayList<Track>());
		setCurrent(new Track());
	}

	public ArrayList<Track> getPlaylist() {
		return playlist;
	}

	public void setPlaylist(ArrayList<Track> playlist) {
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
			for(int i=0; i<playlist.size(); i++){
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
		for(int i=0; i<playlist.size(); i++){
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
		if(index == playlist.size()-1){
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
}
