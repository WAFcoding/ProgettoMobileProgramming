/**
 * 
 */
package playlistModules;

import java.util.ArrayList;

/**
 * Questa classe rappresenta una singola riga della listview, e' composta da un SinglePlaylistItem per il titolo e 
 * la copertina e da un array che rappresenta la scrollview orizzontale nella quale verranno inseriti i brani
 * @author "Pasquale Verlotta - pasquale.verlotta@gmail.com"
 *
 */
public class PlaylistItem {
	
	private SinglePlaylistItem cover;
	private ArrayList<SinglePlaylistItem> songs;
	
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

}
