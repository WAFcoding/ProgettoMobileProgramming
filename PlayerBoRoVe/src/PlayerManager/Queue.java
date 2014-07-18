/**
 * Questo oggetto rappresenta la coda di riproduzione
 */
package PlayerManager;

import java.util.ArrayList;

public class Queue {
	
	private ArrayList<Track> queue;
	
	public Queue(){
		setQueue(new ArrayList<Track>());
	}

	public ArrayList<Track> getQueue() {
		return queue;
	}

	public void setQueue(ArrayList<Track> queue) {
		this.queue = queue;
	}
	
	public void addPlaylist(Playlist p){
		for(Track t : p.getPlaylist()){
			this.queue.add(t);
		}
	}
	
	public void addTrack(Track t){
		this.queue.add(t);
	}
	
	public Track removeTop(){
		return this.queue.remove(0);
	}
	
	public boolean isEmpty(){
		return this.queue.isEmpty();
	}
	
	public int getNumberOfTracks(){
		return this.queue.size();
	}
	
	public Duration getDuration(){
		Duration duration= new Duration(0, 0, 0);
		for(Track t : getQueue()){
			Duration tmp= t.getDuration();
		}
		
		return duration;
	}

}
