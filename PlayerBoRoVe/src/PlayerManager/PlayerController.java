/**
 * L'oggetto che gestisce il player, le playlist e la libreria
 * 
 * @author BoRoVe
 * @version 0.0, 18/07/2014 
 */
package PlayerManager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.MediaPlayer;

public class PlayerController extends SQLiteOpenHelper{
	
	private Library library;
	private Track currentPlayingTrack;
	private Playlist currentPlayingPlaylist;
	private MediaPlayer mediaPlayer;
	private Queue queue, aux_queue;
	private int q_loop;
	
	
	private final String TAG = "PLAYERCONTROLLER";
	private SQLiteDatabase myDatabase;
	private static String dbPath;
	private static
	String DB_NAME ="musicDb.db";
	private static final
	int DATABASE_VERSION = 1;
	
	public PlayerController(Context context){
		super(context, DB_NAME, null, DATABASE_VERSION);
		dbPath = context.getFilesDir().getPath();
		
		this.queue= new Queue();
		this.setQ_loop(1);
		//TODO inizializzazione della libreria da db
		//TODO inizializzazione del media player
		
		
	}
	
	//Crea il database per la prima volta
	public boolean createDb(){
		boolean result = false;
		return result;
	}
	
	public void connectToDB(){
		//TODO implementare la connessione al database
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
	
	public Track getCurrentPlayingTrack(){
		return this.currentPlayingTrack;
	}
	
	//====================================================================================
	//==================GESTIONE DELLA CODA DI RIPRODUZIONE===============================
	public void addTrackToQueue(Track t){
		queue.addTrack(t);
	}
	
	public void addPlaylistToQueue(Playlist p){
		queue.addPlaylist(p);
	}

	public int getQ_loop() {
		return q_loop;
	}

	public void setQ_loop(int q_loop) {
		this.q_loop = q_loop;
	}
	
	public void loop(){
		if(q_loop > 1){
			this.aux_queue= new Queue(queue);
			queue.clear();//ALERT occhio che qua succedono casini;
			for(int i=0; i<q_loop;i++){
				queue.addTrackList(aux_queue.getQueue());
			}
		}
	}
	//====================================================================================
	//==================GESTIONE DATABASE=================================================
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
}
