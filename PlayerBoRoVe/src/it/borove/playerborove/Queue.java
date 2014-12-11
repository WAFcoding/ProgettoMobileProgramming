/**
 * Questo oggetto rappresenta la coda di riproduzione
 */
package it.borove.playerborove;

import java.util.ArrayList;

import android.util.Log;

public class Queue {
	
	private ArrayList<SinglePlaylistItem> queue;
	
	public Queue(){
		setQueue(new ArrayList<SinglePlaylistItem>());
	}
	
	public Queue(Queue q){
		this.queue= new ArrayList<SinglePlaylistItem>();
		addSinglePlaylistItemList(q.getQueue());
	}

	public ArrayList<SinglePlaylistItem> getQueue() {
		return queue;
	}

	public void setQueue(ArrayList<SinglePlaylistItem> queue) {
		this.queue = queue;
	}
	
	public void addPlaylist(PlaylistItem p){
		for(SinglePlaylistItem t : p.getSongs()){
			this.queue.add(t);
		}
	}
	
	public void printQueue(){
		for(SinglePlaylistItem t : this.queue){
			Log.d("queue", t.getTitle());
		}
	}
	
	public void addSinglePlaylistItemList(ArrayList<SinglePlaylistItem> al_t){
		for(SinglePlaylistItem t : al_t){
			addSinglePlaylistItem(t);
		}
	}
	
	public void addSinglePlaylistItem(SinglePlaylistItem t){
		this.queue.add(t);
	}
	
	public void addSinglePlaylistItemOnTop(SinglePlaylistItem t){
		this.queue.add(0, t);;
	}
	
	public SinglePlaylistItem removeTop(){
		return this.queue.remove(0);
	}
	
	public boolean isEmpty(){
		return this.queue.isEmpty();
	}
	
	public int getNumberOfSinglePlaylistItems(){
		return this.queue.size();
	}
	/**
	 * Calcola la durata totale della coda
	 * @return la durata della coda
	 */
	public String getDuration(){
		Duration duration= new Duration(0, 0, 0);
		for(SinglePlaylistItem t : getQueue()){
		}
		
		return duration.getDuration();
	}
	
	public void clear(){
		this.queue.clear();
	}


}
