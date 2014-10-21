/**
 * L'oggetto che gestisce il player, le playlist e la libreria
 * 
 * @author BoRoVe
 * @version 0.0, 18/07/2014 
 */
package it.borove.playerborove;

import java.io.File;






import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import db.SQLiteConnect;
import db.ServiceFileObserver;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

public class PlayerController extends SQLiteOpenHelper{
	


	private static SinglePlaylistItem currentPlayingTrack;
	private static PlaylistItem currentPlayingPlaylist;
	private static Queue queue;
	private static Queue aux_queue;
	private static int q_loop;
	private static Activity mainActivity;
	private static boolean backPressed=false;
	private static boolean player=false;
	
	
	private static Context m_context;
	private static Cursor cursorTracks;
	private static Cursor cursorPlaylist;
	private final static String TAG = "PLAYERCONTROLLER";
	//private SQLiteDatabase myDatabase;
	private static SQLiteConnect sqlDatabaseHelper;
	private static String db_path;
	private static final 
	String DB_NAME ="musicDb.db";
	private static final
	int DATABASE_VERSION = 1;
	
	
	public PlayerController(Context context, Activity v){
		super(context, DB_NAME, null, DATABASE_VERSION);
		mainActivity=v;
		playingPlaylist=false;
		queue=new Queue();
		aux_queue= new Queue();
		lbm= LocalBroadcastManager.getInstance(context);
		//db_path 	= context.getFilesDir().getPath();
		
		String externalStorageState= Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(externalStorageState)){
			//possiamo scrivere sulla memoria esterna
			//Log.d(TAG, "external storage ready for read and write");
			db_path= Environment.getExternalStorageDirectory().getPath() + "/PlayerBoRoVe/";
			File db_directory= new File(db_path);
			if(!db_directory.exists()){
				db_directory.mkdir();
			}
		}
		else
			db_path 	= context.getFilesDir().getPath();
		
		//Log.d(TAG, "path of db " + db_path);
		m_context 	= context;
		//this.queue= new Queue();
		//this.setQ_loop(1);
		
		//PlayerController.sqlDatabaseHelper = new SQLiteConnect(context, db_path, DB_NAME, DATABASE_VERSION);
		PlayerController.sqlDatabaseHelper = SQLiteConnect.getInstance(context, db_path, DB_NAME, DATABASE_VERSION);
	}
	


	public static void open_settings() {
		Intent intent=new Intent( mainActivity, SettingsActivity.class);
		mainActivity.startActivity(intent);
	}
	
	public static void open_library(){
		m_context.startActivity(new Intent(m_context, LibraryActivity.class));
	}
	
	//Crea il database per la prima volta
	public static boolean createDb(){
		return sqlDatabaseHelper.createDatabase();
	}
	
	/*public void addTrackToPlaylist(Track t, Playlist p){
		
	}
	
	public void addPlaylist(Playlist p){
		//this.library.addPlaylist(p);
	}*/


	
	//====================================================================================
	//=====================GESTIONE DEL PLAYER============================================
	
	
	public void next(){
		
	}
	
	public void previous(){
		
	}
	

	
	public void rewind(){
		
	}
	
	public SinglePlaylistItem getCurrentPlayingTrack(){
		return this.currentPlayingTrack;
	}
	
	//====================================================================================
	//==================GESTIONE DELLA CODA DI RIPRODUZIONE===============================
	public void addTrackToQueue(SinglePlaylistItem t){
		queue.addSinglePlaylistItem(t);
	}
	
	public void addPlaylistToQueue(PlaylistItem p){
		queue.addPlaylist(p);
	}
	
	/*public void loop(){
		if(q_loop > 1){
			this.aux_queue= new Queue(queue);
			queue.clear();//ALERT occhio che qua succedono casini;
			for(int i=0; i<q_loop;i++){
				queue.addSinglePlaylistItemList(aux_queue.getQueue());
			}
		}
	}
	*/
	//====================================================================================
	//==================GESTIONE DATABASE=================================================
	
	@Override
	public void onCreate(SQLiteDatabase db) {
	
		//eraseDatabase();
		createDb();
		new SynchronizeDb().execute();
	}
	
	public static void eraseDatabase(){
		if(sqlDatabaseHelper != null)
			sqlDatabaseHelper.eraseDatabase();
		cursorPlaylist 	= null;
		cursorTracks	= null;
	
	}

	public static boolean isDatabaseExist(){
		boolean exist = false;
		
		if(sqlDatabaseHelper.getDb() != null)
			exist = true;
		
		return exist;
	}
	
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	
	/**
	 * Al primo avvio dell'app parsa le info dei file mp3 dallo storage android compreso la copertina dell'album (se esiste)
	 * @param context
	 * @return cursore
	 */
	public static Cursor getInfoMetaMp3(Context context, final String namePathTrack){
		Cursor c = null;
		//Log.d(TAG, "Dentro getInfoMetaMp3");
		if(namePathTrack == null){
			//String field 	= MediaStore.Audio.Media.DISPLAY_NAME + " like ?";
			//String[] filter = {"%_.mp3"};
			
			String selection= MediaStore.Audio.Media.IS_MUSIC + " != ?";
			String [] filter= {"0"};

			
			c = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
					new String[] {	MediaStore.Audio.Media._ID,
									MediaStore.Audio.Media.DATA,
									MediaStore.Audio.Media.ARTIST,
									MediaStore.Audio.Media.ALBUM,
									MediaStore.Audio.Media.DISPLAY_NAME,
									MediaStore.Audio.Media.TITLE,
									MediaStore.Audio.Media.ALBUM_ID,
									MediaStore.Audio.Media.DURATION}, selection, filter, null);
			c.moveToLast();
			if(c.getCount() == 0){
				Log.d(TAG, "cursor c non ha elementi!");
				c.close();
				return null;
			}
			c.moveToFirst();
			//Log.d(TAG, "MediaStore.Audio.Media.EXTERNAL_CONTENT_URI: " + MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
			/*while(!c.isAfterLast()){
				for(int i=0; i < c.getColumnCount(); i++)
					Log.d(TAG, c.getColumnName(i) + ": --> " + c.getString(i));
				c.moveToNext();
			}*/
			
			
		}
		else{
			new updateDbOnTrack().execute(namePathTrack);
		}

		if(c != null)
			c.moveToFirst();
		return c;	
	}
	
	public static class updateDbOnTrack extends AsyncTask<String,Void,Void>{
		 private OnScanCompletedListener callback;
		 private String artist;
		 private String pathTrack;
		 private String title;
		 private String album_id;
		 private String display_name;
		 private String albumName;
		 private String duration;
		 private String kind;
		 private String vote;
		 private boolean isCanceled = false;
		 private String completeString;
		 private Cursor toLibrary;

		@Override
		protected Void doInBackground(String... params) {
			
			String[] paths = {params[0]};
			completeString = params[0];
			
			if(completeString.contains("$")){
				//Log.d(TAG, "file da aggiornare!!");
				File file =  Environment.getExternalStorageDirectory();
				String path2 = file.getPath() + "/Music/";
				display_name = completeString.substring(path2.length());
				
				String oldValue = completeString.substring(0, completeString.indexOf("$"));
				String old_display_name = oldValue.substring(path2.length());
				
				String newValue	= completeString.substring(completeString.indexOf("$")+1, completeString.length());
				String new_display_name = newValue.substring(path2.length());
				
				//Log.d(TAG, "old_display_name: " + old_display_name + " new_display_name: "+ new_display_name);
				sqlDatabaseHelper.openDatabaseReadOnly();
				Cursor c = sqlDatabaseHelper.getExactlyTrack(old_display_name, SQLiteConnect.COLUMN_TITLE);
				sqlDatabaseHelper.closeDatabase();
				if(c != null){
					c.moveToFirst();
					sqlDatabaseHelper.updateRowTrack(c.getString(0), new_display_name, SQLiteConnect.COLUMN_TITLE);
					c.close();
				}	
			
				return null;			
			}
			
			callback = new OnScanCompletedListener() {
				@Override
				public void onScanCompleted(String path, Uri uri) {
					
					if(uri != null && path != null){
						String uri2 = uri.toString();
						String percorso = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString() + "/";
						String id = uri2.substring(percorso.length());
						Cursor c = null;
						String field 	= MediaStore.Audio.Media._ID + " = ?";
						String[] pathC	= {"" + id };
						
						c = m_context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
								new String[] {	MediaStore.Audio.Media._ID,
												MediaStore.Audio.Media.DATA,
												MediaStore.Audio.Media.ARTIST,
												MediaStore.Audio.Media.ALBUM,
												MediaStore.Audio.Media.DISPLAY_NAME,
												MediaStore.Audio.Media.TITLE,
												MediaStore.Audio.Media.ALBUM_ID,
												MediaStore.Audio.Media.DURATION}, field, pathC, null);			
						/*c.moveToFirst();
						while(!c.isAfterLast()){
							for(int i=0; i < c.getColumnCount(); i++)
								Log.e(TAG, c.getColumnName(i) + " " + c.getString(i));
							c.moveToNext();
						}
						*/
					c.moveToLast();
						if(c.getCount() == 0){
							Log.d(TAG, "onScanCompleted: cursor c non ha elementi!");
							c.close();
							c = null;
						}
						else{
							c.moveToFirst();
							toLibrary 	= c;
							pathTrack 	= c.getString(1);
							artist		= c.getString(2);
							title		= c.getString(5);
							album_id	= c.getString(6);
							kind		= "unknown";
							vote		= "0";
							albumName	= c.getString(3);
							duration	= c.getString(7);
							String temp = c.getString(1);
							File file =  Environment.getExternalStorageDirectory();
							String pt = file.getPath() + "/Music/";
							display_name = temp.substring(pt.length());
							//Log.d(TAG, "display_name: " + display_name);
							
							sqlDatabaseHelper.addRowTrack(display_name, kind, artist, vote, title, album_id, pathTrack, albumName, duration);
							Log.d(TAG, "Aggiunto il brano!  --> " + display_name);
							
	
						}
						
							
					}
					else{
						isCanceled = true;
						Log.d(TAG, "Il brano e' da cancellare!");
						File file =  Environment.getExternalStorageDirectory();
						String path2 = file.getPath() + "/Music/";
						display_name = completeString.substring(path2.length());
						sqlDatabaseHelper.deleteRowTrack(display_name, SQLiteConnect.COLUMN_TITLE);
					}
				
				}
				
			};
			MediaScannerConnection.scanFile(m_context, paths, null, callback);
	
			return null;
		}
		
		protected void onPostExecute(Void result){
		}
		 
	 }

	public static class SynchronizeDb extends AsyncTask<Void, Void, Cursor>{
		private Cursor getTracksFromDb;
		@Override
		protected Cursor doInBackground(Void... params) {
			
			try{
				Cursor getMp3FromStorage = getInfoMetaMp3(m_context, null);
				sqlDatabaseHelper.SynchronizeDb(getMp3FromStorage);
				String[] columnsSelect = {"pid AS _id, title, singerName, kind, vote, contentTitle, albumId, pathTrack, albumName, duration"};
				getTracksFromDb =  sqlDatabaseHelper.getFilteredTrack("", SQLiteConnect.COLUMN_TITLE, columnsSelect);
				getTracksFromDb.moveToFirst();
				
			}catch(Exception e){
				e.printStackTrace();
				}
			Log.d(TAG, "db sincronizzato!");
			return getTracksFromDb;
		}
		
		protected void onPostExecute(Cursor result){
			if(result != null){
				//setCursorTracks(getTracksFromDb);
				//getTracksFromDb.moveToFirst();
				getTracksFromDb.close();
				closeConnectionDB();
				
				
				/*while(!getTracksFromDb.isAfterLast()){
					for(int i=0; i< getTracksFromDb.getColumnCount(); i++){
						Log.e(TAG, getTracksFromDb.getColumnName(i) + ": " + getTracksFromDb.getString(i));
					}
					getTracksFromDb.moveToNext();
				}*/
							
				new SynchronizePlaylistDb().execute();
			}
			
			else
				Log.d(TAG, "result is null!!!");
		}
		
	}
	
	public static final String[] getColumns(){
		String[] from = new String[]{  "_id", SQLiteConnect.COLUMN_TITLE, SQLiteConnect.COLUMN_SINGER_NAME};
				//SQLiteConnect.COLUMN_KIND, SQLiteConnect.COLUMN_VOTE, SQLiteConnect.COLUMN_ALBUM_NAME, SQLiteConnect.COLUMN_DURATION};
		return from;
	}
	
	public static Cursor getCursorTracks(){
		if(sqlDatabaseHelper.getDb() == null)
			return null;
		
		String[] columnsSelect = {"pid AS _id, title, singerName, kind, vote, contentTitle, albumId, pathTrack, albumName, duration"};
		sqlDatabaseHelper.openDatabaseReadOnly();
		cursorTracks = sqlDatabaseHelper.getDb().query(SQLiteConnect.TABLE_NAME_TRACK, columnsSelect,null,null,null,null,null);
		//sqlDatabaseHelper.closeDatabase();
		/*cursorTracks.moveToFirst();
		Log.e(TAG, "*********+DENTRO GetCursorTracks()**********");
		while(!cursorTracks.isAfterLast()){
			for(int i=0; i< cursorTracks.getColumnCount(); i++){
				Log.e(TAG, cursorTracks.getColumnName(i) + ": " + cursorTracks.getString(i));
			}
			cursorTracks.moveToNext();
		}
		
		Log.e(TAG, "***********FUORI GetCursorTracks()************");
		*/
		
		return cursorTracks;
		
	}
	
	public static void setCursorTracks(Cursor cursor){
		PlayerController.cursorTracks = cursor;
	}
	
	public static Cursor getCursorPlaylist(){
		//columnsSelect = {"id_P, nameP, pid AS _id, title, singerName, kind, vote, contentTitle, albumId, pathTrack, albumName, duration"};
		
		try{
			//String[] columnSelect = {"*"};
			cursorPlaylist = sqlDatabaseHelper.getQueryResult("", SQLiteConnect.COLUMN_NAME, SQLiteConnect.TABLE_NAME_PLAYLIST,
																 SQLiteConnect.TABLE_NAME_TRACK, "*");
			/*cursorPlaylist.moveToFirst();
			while(!cursorPlaylist.isAfterLast()){
				for(int i=0; i<cursorPlaylist.getColumnCount(); i++){
					Log.d(TAG, "GETCURSORPLAYLIST--> " + cursorPlaylist.getColumnName(i) + ": " + cursorPlaylist.getString(i));
				}
				
				
				cursorPlaylist.moveToNext();
			}
			*/
			
		}catch(Exception e){
			e.printStackTrace();}
		//Log.d(TAG, "Recupero playlist! su db");
		
		return cursorPlaylist;
	}
	
	public static void setCursorPlaylist(Cursor c){
		PlayerController.cursorPlaylist = c;
	}
	
	/**
	 * Modifica i tag del singolo brano
	 * 
	 * @param _id				l'id del brano
	 * @param fileNameTrack		il nuovo nome del brano
	 * @param authorName		il nuovo autore del brano
	 * @param kind				il nuovo genere
	 * @param vote				il nuovo voto attribuito al brano
	 * @param albumName			il nuovo nome dell'album del brano
	 * @param duration			la durata del brano(non modificabile)
	 */
	
	public static void setTagTrackFromActivityLibrary(int _id, String fileNameTrack, String authorName, String kind, int vote,
														String albumName, String duration){
		sqlDatabaseHelper.openDatabaseRW();
		Cursor c = sqlDatabaseHelper.getExactlyTrack(String.valueOf(_id), SQLiteConnect.COLUMN_ID);
		sqlDatabaseHelper.closeDatabase();
		if(c != null){
			String oldValueFileName 	= c.getString(c.getColumnIndex(SQLiteConnect.COLUMN_TITLE));
			String oldValueAuthor 		= c.getString(c.getColumnIndex(SQLiteConnect.COLUMN_SINGER_NAME));
			String oldValuekind 		= c.getString(c.getColumnIndex(SQLiteConnect.COLUMN_KIND));
			String oldValueVote 		= c.getString(c.getColumnIndex(SQLiteConnect.COLUMN_VOTE));
			String oldValueAlbumName 	= c.getString(c.getColumnIndex(SQLiteConnect.COLUMN_ALBUM_NAME));
			String oldValueDuration 	= c.getString(c.getColumnIndex(SQLiteConnect.COLUMN_DURATION));
			
			if(!oldValueFileName.equals(fileNameTrack))
				sqlDatabaseHelper.updateRowTrack(String.valueOf(_id), fileNameTrack, SQLiteConnect.COLUMN_TITLE);
			if(!oldValueAuthor.equals(authorName))
				sqlDatabaseHelper.updateRowTrack(String.valueOf(_id), authorName, SQLiteConnect.COLUMN_SINGER_NAME);
			if(!oldValuekind.equals(kind))
				sqlDatabaseHelper.updateRowTrack(String.valueOf(_id), kind, SQLiteConnect.COLUMN_KIND);			
			if(!oldValueVote.equals(String.valueOf(vote)))
				sqlDatabaseHelper.updateRowTrack(String.valueOf(_id), String.valueOf(vote), SQLiteConnect.COLUMN_VOTE);
			if(!oldValueAlbumName.equals(String.valueOf(albumName)))
				sqlDatabaseHelper.updateRowTrack(String.valueOf(_id), albumName, SQLiteConnect.COLUMN_ALBUM_NAME);
			if(!oldValueDuration.equals(String.valueOf(duration)))
				sqlDatabaseHelper.updateRowTrack(String.valueOf(_id), duration, SQLiteConnect.COLUMN_DURATION);
			
		}
		//Log.d(TAG, "Fatto!");
	}
	
	public static void deleteRowTrack(int _id){
		if(_id >= 0)
			sqlDatabaseHelper.deleteRowTrack(String.valueOf(_id), SQLiteConnect.COLUMN_ID);	
	}
	
	/**
	 * Aggiunge una nuova playlist nel db con il/i brano/i associati
	 * 
	 * @param namePlaylist	nome della playlist(String)
	 * @param arrayIdTrack	insieme di brani associati alla playlist
	 */
	
	public static void addPlaylistToDb(String namePlaylist, ArrayList<String> arrayIdTrack){
		try{
			sqlDatabaseHelper.addRowPlaylist(namePlaylist);
			Cursor pl = sqlDatabaseHelper.getExactlyNamePlaylist(namePlaylist);
			if(pl != null)
				for(int i=0; i< arrayIdTrack.size(); i++){
					if(arrayIdTrack.get(i) != null){
						//Log.d(TAG, "pl.getString(0): " + pl.getString(0));
						//Log.d(TAG, "namePlaylist: " + namePlaylist + " pl.getString(0): " + pl.getString(0));
						
						sqlDatabaseHelper.addRowContains(pl.getString(0), arrayIdTrack.get(i));
					}
				}
			else{
				Log.d(TAG, "Errore in addPlaylistToDb(): pl � NULL");
			}
		}catch(Exception e){
			Log.d(TAG, "Errore in addPlaylistToDb()");
			e.printStackTrace();
			}	
		
	}
	
	/**
	 * Aggiunge o rimuove un singolo brano da una playlist esistente
	 * 
	 * @param name		Il nome della playlist
	 * @param idTrack	L'id del singolo brano
	 * @param cancel	TRUE se il brano deve essere eliminato, FALSE deve essere aggiunto alla playlist
	 */
	
	public static void setPlaylistOnDb(String name, String idTrack, boolean cancel){
		try{
			Cursor singlePlaylist = sqlDatabaseHelper.getExactlyNamePlaylist(name);
			if(singlePlaylist != null){
				if(!cancel){
					sqlDatabaseHelper.addRowContains(singlePlaylist.getString(0), idTrack);
				}
				else{
					sqlDatabaseHelper.deleteRowContains(singlePlaylist.getString(0), idTrack);
				}
			}		
		}catch(Exception e){
			Log.d(TAG, "Errore in setPlaylistOnDb()");
			e.printStackTrace();
			}
		
	}
	
	/**
	 * Cancella la playlist specificata
	 * 
	 * @param name	il nome della playlist
	 */
	
	public static void deletePlaylist(ArrayList<String> name){
		if(name == null)
			return;
		if(name.size() == 0)
			return;
		
		for(int i=0; i < name.size(); i++){
			try{
				Cursor c = sqlDatabaseHelper.getExactlyNamePlaylist(name.get(i));
				if(c != null){
					sqlDatabaseHelper.deleteRowPlaylist(name.get(i));
				}
			}catch(Exception e){
				Log.d(TAG, "Errore in deleteRowPlaylist()");
			}
		}
	}
	
	public static void deleteSinglePlaylist(String name){

		if(name == null)
			return;

		try{
			Cursor c = sqlDatabaseHelper.getExactlyNamePlaylist(name);
			if(c != null){
				sqlDatabaseHelper.deleteRowPlaylist(name);
			}
		}catch(Exception e){
			Log.d(TAG, "Errore in deleteSinglePlaylist: deleteRowPlaylist()");
		}
	}
	
	/**
	 * Permette di ricavare la lista delle playlist con i brani associati salvate sul db (in background)
	 * setta il cursore di PlayerController (cursorPlaylist)
	 *
	 */
	
	public static class SynchronizePlaylistDb extends AsyncTask<Void, Void, Cursor>{
		private Cursor cursorPlaylist;

		@Override
		protected Cursor doInBackground(Void... params) {
			
			try{
				//String[] columnSelect = {"*"};
				this.cursorPlaylist = sqlDatabaseHelper.getQueryResult("", SQLiteConnect.COLUMN_NAME, SQLiteConnect.TABLE_NAME_PLAYLIST,
																	 SQLiteConnect.TABLE_NAME_TRACK, "*");
			}catch(Exception e){
				e.printStackTrace();}
			Log.d(TAG, "Recupero playlist! su db");
			return cursorPlaylist;
		}
		
		protected void onPostExecute(Cursor result){
			//setCursorPlaylist(this.cursorPlaylist);
			//this.cursorPlaylist.close();
			//closeConnectionDB();
			/*ArrayList<String> tracks = new ArrayList<String>();
			tracks.add("1");
			tracks.add("2");
			tracks.add("3");
			tracks.add("4");
			tracks.add("5");
			addPlaylistToDb("primo", tracks);
			
			ArrayList<String> tracks2 = new ArrayList<String>();
			tracks2.add("3");
			tracks2.add("4");
			addPlaylistToDb("secondo", tracks2);
			
			ArrayList<String> tracks3 = new ArrayList<String>();
			//tracks3.add("1");
			tracks3.add("6");
			addPlaylistToDb("terzo", tracks3);
			
			Cursor test = sqlDatabaseHelper.getQueryResult("%", SQLiteConnect.COLUMN_NAME, SQLiteConnect.TABLE_NAME_PLAYLIST,
					 SQLiteConnect.TABLE_NAME_TRACK, "*");
			setCursorPlaylist(test);
			//Cursor test2 = sqlDatabaseHelper.getQueryResult("primo", SQLiteConnect.COLUMN_NAME, SQLiteConnect.TABLE_NAME_PLAYLIST,
			//		 SQLiteConnect.TABLE_NAME_TRACK, "*");
			if(test != null){
				test.moveToFirst();
				while(!test.isAfterLast()){
					for(int i=0; i< test.getColumnCount(); i++)
						Log.d(TAG, "TEST ---> " + test.getColumnName(i) + ": " + test.getString(i));
					test.moveToNext();
				}
			}
			*/
			
			/*if(this.cursorPlaylist != null){
				this.cursorPlaylist.moveToFirst();
				while(!this.cursorPlaylist.isAfterLast()){
					for(int i=0; i< this.cursorPlaylist.getColumnCount(); i++)
						Log.d(TAG, this.cursorPlaylist.getColumnName(i) + ": " + this.cursorPlaylist.getString(i));
					this.cursorPlaylist.moveToNext();
				}
			}
			*/
			//else
			//	Log.d(TAG, "test � NULL");
			
		}
		
	}

	public static void closeConnectionDB(){
		if(sqlDatabaseHelper != null)
			sqlDatabaseHelper.closeDatabase();	
	}
	
	public static String getArtistCurrentPlayingTrack(){
		if(currentPlayingTrack!=null)
		return currentPlayingTrack.getSinger_name();
		else return null;
	}
	
	public static String getTitleCurrentPlayingTrack(){
		if(currentPlayingTrack!=null)
		return currentPlayingTrack.getTitle();
		else return null;
	}
	
	public static String getKindCurrentPlayingTrack(){
		if(currentPlayingTrack!=null)
		return currentPlayingTrack.getKind();
		else return null;
	}
	
	public static  Bitmap getCoverCurrentPlayingTrack(){
		if(currentPlayingTrack!=null)
		return currentPlayingTrack.getBitmapCover();
		else return null;
	}
	public static void end_music_sevice(){//change
		Log.d("MusicService","end_music_service");

		lbm.unregisterReceiver(previewCompleteReceiver);
		lbm.unregisterReceiver(songPreparedReceiver);
		lbm.unregisterReceiver(previewPreparedReceiver);
		lbm.unregisterReceiver(songCompleteReceiver);
		if(preview || playingPlaylist || player)
			m_context.unbindService(musicConnection);
		
		if(musicSrv!=null)
			musicSrv.stopFade();
			
		playingPlaylist=false;
		serviceConnected=false;
		preview=false;
		player=false;
	}
	
	private static BroadcastReceiver songCompleteReceiver=new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			//lbm.unregisterReceiver(songPreparedReceiver);
			Log.d("songCompleteReceiver","attivato");
			lbm.unregisterReceiver(songCompleteReceiver);
			//queue.printQueue();
			Log.d("queue", "size= " + queue.getQueue().size());
			Log.d("queue", "aux_queue size= " + aux_queue.getQueue().size());
			if(queue.isEmpty())
			{
				nLoopPlaylistDone++;
				if(nLoopPlaylistDone==q_loop && q_loop!=1000 )
				{
					//playingPlaylist=false;
					
					//m_context.unbindService(musicConnection);
				}
				else
				queue.addPlaylist(currentPlayingPlaylist);
				//Intent intent2=new Intent(mainActivity, PlaylistActivity.class);						
				//mainActivity.startActivity(intent2);
			}
	
				if(!musicSrv.isLooping()&& (nLoopPlaylistDone!=q_loop || q_loop==1000))
				{
					Log.d("complete song","complete song");
					if(!backPressed)
						aux_queue.addSinglePlaylistItemOnTop(currentPlayingTrack);
					backPressed=false;
					
					if(random){
						
						int pos= gen.nextInt(queue.getNumberOfSinglePlaylistItems());
						currentPlayingTrack= queue.getQueue().remove(pos);
					}
					else{
						currentPlayingTrack=queue.removeTop();
					}
					
					uri=Uri.parse(currentPlayingTrack.getPath_track());
					try {
						set_player_playlist();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					};

		

				}
		}
	};
	private static BroadcastReceiver songPreparedReceiver=new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			lbm.unregisterReceiver(songPreparedReceiver);
			if(playingPlaylist){
				lbm.registerReceiver(songCompleteReceiver, new IntentFilter("Complete"));
				Log.d("songComplete","Registrato");
			}
			SharedPreferences prefs=m_context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
			int pos=prefs.getInt("Pos", 0);
			Log.d("activity","before seek");
			seekTo(pos*1000);
			play();
		}
	};
	
	public static Context getContext(){
		return m_context;
	}
	
	
	private static BroadcastReceiver previewPreparedReceiver=new BroadcastReceiver(){//change
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("On receive prepared","preview");
			lbm.unregisterReceiver(previewPreparedReceiver);
			//lbm.registerReceiver(previewCompleteReceiver, new IntentFilter("Complete Preview") );
			printToast("Preview of: "+currentPreviewTrack.getTitle());
			SharedPreferences prefs=m_context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
			int durationPreview=(prefs.getInt("Duration Preview", 0)+15)*1000;//TODO PlayerBoRoVe ora lo prende statico, lo deve leggere dalle prefences
			musicSrv.preview(5000);
		}
	};
	
	public static void printToast(String s){
		Toast.makeText(m_context, s, Toast.LENGTH_SHORT).show();
	}
	
	private static BroadcastReceiver previewCompleteReceiver=new BroadcastReceiver(){//change
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("on complete preview","on complete preview");
			//FIXME controllare se necessario
			m_context.unbindService(musicConnection);
			if(!queue.isEmpty()){
				
				lbm.registerReceiver(waitForDestroyService, new IntentFilter("Service destroy"));
				currentPreviewTrack=queue.removeTop();
				uri=Uri.parse(currentPreviewTrack.getPath_track());
			}
			else{
				lbm.unregisterReceiver(previewCompleteReceiver);
				lbm.unregisterReceiver(previewPreparedReceiver);
				preview=false;
				Log.d("end","preview");
			}
		}
	};
	
	private static BroadcastReceiver waitForDestroyService=new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			lbm.unregisterReceiver(waitForDestroyService);
			set_player();
			
		}
		
	};
	
	private static MusicService musicSrv;
	private static boolean serviceConnected=false;
	private static Intent playIntent;
	private static Uri uri;
	private static LocalBroadcastManager lbm;
	private static int nLoopPlaylistDone;
	private static Random gen= new Random();
	private static boolean playingPlaylist;
	private static SinglePlaylistItem currentPreviewTrack;
	private static boolean preview=false;
	private static ServiceConnection musicConnection = new ServiceConnection(){
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			it.borove.playerborove.MusicService.MusicBinder binder = (it.borove.playerborove.MusicService.MusicBinder)service;
			//get service
			Log.d("service","onConnected");
			musicSrv = binder.getService();
			serviceConnected=true;
			try {
				musicSrv.setPath(uri);	

			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			serviceConnected=false;
			Log.d("Disc","onServiceDisconnetcted");
		}
	};
	


	public static void set_player(){
		
			if(!preview){
				SharedPreferences prefs=m_context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor=prefs.edit();
				editor.putString("lastSongId", currentPlayingTrack.getId());
				editor.commit();
			}
			playIntent = new Intent(m_context, MusicService.class);
			m_context.bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
			
			if(preview)
				lbm.registerReceiver(previewPreparedReceiver, new IntentFilter("Prepared"));
			else
				lbm.registerReceiver(songPreparedReceiver, new IntentFilter("Prepared"));
			Log.d("setPlayer","setPlayer");
	}
	
	public static void set_player_playlist_init(){
		playIntent = new Intent(m_context, MusicService.class);
		m_context.bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
		lbm.registerReceiver(songPreparedReceiver, new IntentFilter("Prepared"));
		Log.d("setPlayer","setPlayer");
	}
	
	public static void set_player_playlist() throws IllegalArgumentException, SecurityException, IllegalStateException, IOException{
		Log.d("setPlayer","setPlayer");
	
			SharedPreferences prefs=m_context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor=prefs.edit();
			editor.putString("lastSongId", currentPlayingTrack.getId());
			editor.commit();
			lbm.registerReceiver(songPreparedReceiver, new IntentFilter("Prepared"));
			musicSrv.setPath(uri);
}
	
	protected static final String SETTINGS = "SETTINGS";
	
	public static void play(){
		if(musicSrv!=null && serviceConnected){
			Log.d(TAG,"play");
			musicSrv.playPlayer();
		}
	}
	
	public static void pause(){
		Log.d(TAG,"pause");
		if(musicSrv!=null && serviceConnected){
			musicSrv.pausePlayer();
		}	
	}
	
	public static int getDuration() {
		if(musicSrv!=null && serviceConnected)
			return musicSrv.getDur();
		else return 0;
		}
	
	public static int getCurrentPosition() {
		if(musicSrv!=null && serviceConnected)
			return musicSrv.getPosn();
		else return 0;
	}

	public static void seekTo(int pos) {
		if(musicSrv!=null && serviceConnected)
			musicSrv.seek(pos);
	}
	
	public static boolean isPlaying() {
		if (musicSrv!=null && serviceConnected)
			return musicSrv.isPng();
		return false;
	}

	public static void setLoop() {
		
		if(musicSrv!=null && serviceConnected)
			musicSrv.Loop(true);
	}

	public static void disableLoop() {
		if(musicSrv!=null && serviceConnected)
		if (musicSrv.isLooping())
			musicSrv.Loop(false);		
	}

	public static boolean isLooping() {
		if(musicSrv!=null && serviceConnected)
		return musicSrv.isLooping();
		return false;
		}

	
	public static void mute() {
		if(musicSrv!=null && serviceConnected){
			musicSrv.stopFade();
			musicSrv.setVolume(0f);
		}
	}

	public static void audio() {/*
		SharedPreferences prefs=m_context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
		
		int fadeIn=prefs.getInt("FadeIn",0);
		if(musicSrv!=null && serviceConnected)
			if(fadeIn>0)
			musicSrv.fadeIn(fadeIn);
			else
			musicSrv.setVolume(1f);*/
		if(musicSrv!=null && serviceConnected){
			musicSrv.setVolume(1f);
		}
	}

	
	public static boolean isMute() {
		if(musicSrv!=null && serviceConnected)
			return musicSrv.isMute();

		return false;
	}


	public static void stop() {//change
		if(musicSrv!=null && serviceConnected)
		{	lbm.registerReceiver(songPreparedReceiver, new IntentFilter("Prepared"));
			//musicSrv.seek(0);
			musicSrv.stop();
		}
	}
	private static boolean random;
	
	public static void playPlaylist(PlaylistItem playlist){
		if(preview)
			end_music_sevice();
		
		SharedPreferences prefs=m_context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
		q_loop=prefs.getInt("NLoopPlaylist", 1);
		random=prefs.getBoolean("Random Playback", false);
		//if(q_loop>1 && random)
			//trackLoop=new ArrayList<Integer>(playlist.getSongs().size());
	
		playingPlaylist=true;
		nLoopPlaylistDone=0;
		queue.clear();
		aux_queue.clear();
		queue.addPlaylist(playlist);
		currentPlayingPlaylist=playlist;
		
		if(random){
			
			int pos= gen.nextInt(queue.getNumberOfSinglePlaylistItems());
			currentPlayingTrack= queue.getQueue().remove(pos);
		}
		else{
			currentPlayingTrack=queue.removeTop();
		}
		SharedPreferences.Editor editor=prefs.edit();
		editor.putString("lastSongId", currentPlayingTrack.getId());
		editor.commit();
		//aux_queue.addSinglePlaylistItemOnTop(currentPlayingTrack);
		lbm.registerReceiver(songPreparedReceiver, new IntentFilter("Prepared"));
		uri=Uri.parse(currentPlayingTrack.getPath_track());
		playIntent = new Intent(m_context, MusicService.class);
		m_context.bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
		Intent intent=new Intent(mainActivity, PlayerActivity.class);
		mainActivity.startActivity(intent);
		
	}
	
	public static void previewPlaylist(PlaylistItem playlist){//change
		Log.d(TAG," preview playlist");
		queue.clear();
		queue.addPlaylist(playlist);
		
		if(preview)
		{  Log.d(TAG, "stop previus preview");
			
			musicSrv.stopFade();
			//lbm.unregisterReceiver(songPreviewReceiver);
			//m_context.unbindService(musicConnection);
			//lbm.registerReceiver(previewCompleteReceiver, new IntentFilter("Complete Preview") );
			lbm.sendBroadcast(new Intent("Complete Preview"));
			Log.d(TAG, "Intent complete preview lanced");
			
		}
		else
		{
		preview=true;
		lbm.registerReceiver(previewCompleteReceiver, new IntentFilter("Complete Preview") );
		currentPreviewTrack=queue.removeTop();
		previewSingleItem(currentPreviewTrack);//preview single item
		}
	}
	
	public static void previewSingleItem(SinglePlaylistItem i){
		
		currentPreviewTrack=i;
		uri=Uri.parse(currentPreviewTrack.getPath_track());
		PlayerController.set_player();
		
	}
	public static void playSingleItem(SinglePlaylistItem i){
		player=true;
		currentPlayingTrack=i;
		uri=Uri.parse(currentPlayingTrack.getPath_track());
		Intent intent=new Intent(mainActivity, PlayerActivity.class);
		mainActivity.startActivity(intent);
		PlayerController.set_player();
		
	}
	
	public static boolean canBackForward(){
	return true;		
	}
	
	public static void forward(){
		if(playingPlaylist){
			
			if(!queue.isEmpty() || nLoopPlaylistDone+1!=q_loop){
				musicSrv.stopFade();
				LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(m_context);
				Intent mIntent= new Intent();
				mIntent.setAction("Complete");
				//stopFade();
				Log.d("forward","pressed");
				lbm.sendBroadcast(mIntent);	

			}
		}
		
	}
	
	public static void back(){
		if(playingPlaylist && !aux_queue.isEmpty()){
			backPressed=true;
			queue.addSinglePlaylistItemOnTop(currentPlayingTrack);
			queue.addSinglePlaylistItemOnTop(aux_queue.removeTop());
			Log.d("queue", "size= " + queue.getQueue().size());
			Log.d("queue", "aux_queue size= " + aux_queue.getQueue().size());
			
			musicSrv.stopFade();
			LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(m_context);
			Intent mIntent= new Intent();
			mIntent.setAction("Complete");
			//stopFade();
			Log.d("complete","complete");
			lbm.sendBroadcast(mIntent);
			
		}
		
	}
	

	public void open_player(){
		
		Log.d("open player","open player");
		player=true;
		SharedPreferences prefs=m_context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
		sqlDatabaseHelper.openDatabaseReadOnly();
		Cursor tracks=sqlDatabaseHelper.getExactlyTrack(prefs.getString("lastSongId", "0"),SQLiteConnect.COLUMN_ID);
		Log.d(prefs.getString("lastSongId", "0"),"null");
		
		if(tracks!=null){		
			String _id=null;
			String p_title=null;
			String singerName=null;
			String kind=null;
			String vote=null;
			String nameFile=null;
			String album_id=null;
			String path_track=null;
			String albumName=null;
			String duration=null;

			tracks.moveToFirst();
			while(!tracks.isAfterLast()){
				_id= tracks.getString(0);
				p_title= tracks.getString(1);
				singerName= tracks.getString(2);
				kind=tracks.getString(3);
				vote=tracks.getString(4);
				nameFile= tracks.getString(5);
				album_id= tracks.getString(6);
				path_track = tracks.getString(7);
				albumName = tracks.getString(8);
				duration =tracks.getString(9);
				tracks.moveToNext();
				Log.d("done","done");
			}
			//controllare *************************
			tracks.close();
			sqlDatabaseHelper.closeDatabase();
			SinglePlaylistItem song=new SinglePlaylistItem(_id, p_title, singerName, kind, vote, nameFile, album_id, path_track, albumName, duration, m_context);
			playSingleItem(song);
		}
			
		//playSingleItem(PlayerController.cursorTracks.getExatlyTrack()�);

	}
	/**
	 * calcola le statistiche sulla libreria, es: numero di brani totali, durata totale, brano più lungo, file più grande
	 */
	public static void libraryDetails(){

		int number_of_all_track=0;
		Duration duration_of_library= new Duration(0, 0, 0);
		Duration longest_track_duration= new Duration(0,0,0);
		String longest_track_title= "";
		double memory_occupation= 0.0;//occupazione di memoria di tutta la libreria
		double bigger_track_occupation= 0.0;
		String biggest_track_title= "";
		ArrayList<Integer> number_of_track_for_kind;//numero di brani per tipo
		ArrayList<Integer> vote_of_track_for_kind;//voto medio dei brani per tipo

		Cursor newCursor = getCursorTracks();
		if(newCursor != null){
			newCursor.moveToFirst();
			while(!newCursor.isAfterLast()){

				//incremento il numero di brani
				number_of_all_track++;

				//incremento la durata totale della libreria
				//e cerco il brano di durata maggiore
				String duration= newCursor.getString(9);
				int sec 	= Integer.parseInt(duration) / 1000;
				int min 	= sec / 60;
				sec = sec % 60;
				int hour	= min / 60;
				min = min % 60; 
				Duration tmp_duration= new Duration(hour, min, sec);
				duration_of_library.sum(tmp_duration);
				if(longest_track_duration.isSmallerOf(tmp_duration)){
					longest_track_duration.setDuration(tmp_duration);
					longest_track_title= newCursor.getString(1);
				}

				//incremento la memoria totale occupata dalla libreria
				//e certo il file con maggior occupazione
				String pathTrack= newCursor.getString(7);
				File file= new File(pathTrack);
				double tmp_size= (file.length()/1048576.0);
				memory_occupation+= Math.round(tmp_size);
				if(bigger_track_occupation < tmp_size){
					bigger_track_occupation= Math.round(tmp_size);
					biggest_track_title= newCursor.getString(1);
				}

				newCursor.moveToNext();
			}	
			/*Log.d("stat library", "num of track: " + number_of_all_track + ", total duration: " + duration_of_library.getDuration()
									+ ", memory: " + memory_occupation+ ", bigger file: " + biggest_track_title + " - " + bigger_track_occupation 
									+ ", longest track: " + longest_track_title + " - " + longest_track_duration.getDuration());*/


			Intent library_details 	= new Intent(m_context, LibraryDetailsActivity.class);
			Bundle details		= new Bundle();
			
			details.putString("n_tracks", String.valueOf(number_of_all_track));
			details.putString("total_duration", duration_of_library.getDuration());
			details.putString("memory_size", String.valueOf(memory_occupation) + " MB");
			details.putString("bigger_file", biggest_track_title + " - " +String.valueOf(bigger_track_occupation) + " MB");
			details.putString("longest_file", longest_track_title + " - " +longest_track_duration.getDuration());

			library_details.putExtras(details);
			m_context.startActivity(library_details);
		}
	}

	/**
	 * calcola le statistiche per le playlist presenti 
	 */
	public static void playlistDetails(){
		int number_of_playlist= 0;//numero totale di playlist
		Duration total_duration_of_playlists= new Duration(0,0,0);//durata totale di tutte le playlist
		Duration duration_of_playlist= new Duration(0,0,0);
		String longest_playlist= "";//nome della playlist più lunga
		Duration duration_of_longest_playlist= new Duration(0,0,0);//durata della playlist più lunga
		String biggest_playlist= "";//nome della playlist con più brani
		int number_of_track_for_biggest_playlist= 0;
		int number_of_track_in_playlist=0;

		ArrayList<String> playlists= new ArrayList<String>();

		Cursor cursor= getCursorPlaylist();
		if(cursor != null){
			//popolo l'array di tutte le playlist
			while(!cursor.isAfterLast()){
				//Log.d("cursor", cursor.getString(0) + ", " + cursor.getString(1) + ", " + cursor.getString(2) + ", " + cursor.getString(7));

				if(!playlists.contains(cursor.getString(1))){
					playlists.add(cursor.getString(1));
				}

				cursor.moveToNext();
			}
			cursor.moveToFirst();
			//imposto il numero di playlist
			number_of_playlist= playlists.size();

			//calcolo la durata totale, la playlist piu' lunga e quella con piu' elementi
			for(String playlist : playlists){


				while(!cursor.isAfterLast()){

					if(playlist.equals(cursor.getString(1))){

						String tmp_duration_track= cursor.getString(11);
						int sec 	= Integer.parseInt(tmp_duration_track) / 1000;
						int min 	= sec / 60;
						sec = sec % 60;
						int hour	= min / 60;
						min = min % 60; 
						Duration tmp_duration= new Duration(hour, min, sec);

						duration_of_playlist.sum(tmp_duration);
						number_of_track_in_playlist++;
					}

					cursor.moveToNext();
				}

				//calcolo la durata totale
				total_duration_of_playlists.sum(duration_of_playlist);
				//calcolo la playlist piu' lunga
				if(duration_of_longest_playlist.isSmallerOf(duration_of_playlist)){
					duration_of_longest_playlist.setDuration(duration_of_playlist);
					longest_playlist= playlist;
				}
				//calcolo la playlist con piu' elementi
				if(number_of_track_for_biggest_playlist < number_of_track_in_playlist){
					number_of_track_for_biggest_playlist= number_of_track_in_playlist;
					biggest_playlist= playlist;
				}

				cursor.moveToFirst();
				duration_of_playlist.setDuration(0, 0, 0);
			}

			//Log.d("details playlist","numero playlist: " + number_of_playlist + ", durata totale: " + total_duration_of_playlists.getDuration() + ", longest: " + longest_playlist + " - " + duration_of_longest_playlist.getDuration()
				//	+ ", biggest: " + biggest_playlist + " - " + number_of_track_for_biggest_playlist);

			Intent playlist_details= new Intent(m_context, PlaylistDetailsActivity.class);
			Bundle details= new Bundle();

			details.putString("n_playlist", String.valueOf(number_of_playlist));
			details.putString("total_duration", total_duration_of_playlists.getDuration());
			details.putString("bigger_file", biggest_playlist + " - " +String.valueOf(number_of_track_for_biggest_playlist));
			details.putString("longest_playlist", longest_playlist + " - " +duration_of_longest_playlist.getDuration());

			playlist_details.putExtras(details);
			m_context.startActivity(playlist_details);
		}
	}
	
	/**
	 * calcola le statistiche per la singola playlist selezionata
	 */
	public static void playlistSingleDetails(PlaylistItem playlist){
		
		int n_track_in_playlist= playlist.getSongs().size();
		Duration total_duration_playlist= new Duration(0,0,0);
		Duration longest_track= new Duration(0,0,0);
		String longest_track_name= "";
		
		for(SinglePlaylistItem track : playlist.getSongs()){
			//prendo la durata totale
			int sec 	= Integer.parseInt(track.getDuration()) / 1000;
			int min 	= sec / 60;
			sec = sec % 60;
			int hour	= min / 60;
			min = min % 60; 
			Duration tmp_duration= new Duration(hour, min, sec);
			total_duration_playlist.sum(tmp_duration);
			
			//prendo il brano piu' lungo
			if(longest_track.isSmallerOf(tmp_duration)){
				longest_track.setDuration(tmp_duration);
				longest_track_name= track.getTitle();
			}
		}
		

		Intent playlist_single_details= new Intent(m_context, PlaylistSingleDetails.class);
		Bundle details= new Bundle();

		details.putString("n_track_in_playlist", String.valueOf(n_track_in_playlist));
		details.putString("total_duration", total_duration_playlist.getDuration());
		details.putString("longest_track_in_playlist", longest_track_name + " - " +longest_track.getDuration());

		playlist_single_details.putExtras(details);
		m_context.startActivity(playlist_single_details);
	}
	
	public static void open_edit_playlist(PlaylistItem playlist){

		m_context.startActivity(new Intent(m_context, PlaylistEditActivity.class));
	}

	public static void open_add_track_to_playlist(){
		m_context.startActivity(new Intent(m_context, AddTracksToPlaylist.class));
	}
	
	public static void open_playlist_tracks(){
		//m_context.startActivity(new Intent(m_context, PlaylistTracks.class));
	}
}
