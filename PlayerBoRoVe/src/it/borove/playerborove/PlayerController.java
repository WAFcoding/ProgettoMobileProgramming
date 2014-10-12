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

import playlistModules.PlaylistItem;
import playlistModules.SinglePlaylistItem;
import db.SQLiteConnect;
import db.ServiceFileObserver;
import PlayerManager.Queue;
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
import android.media.AudioManager;
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
	
	private static Activity mainActivity;
	private static Context m_context;
	private final static String TAG = "PLAYERCONTROLLER";
	
	//================PLAYLIST/PREVIEW================
	private static Cursor cursorTracks;
	private static Cursor cursorPlaylist;
	private static SinglePlaylistItem currentPlayingTrack;
	private static PlaylistItem currentPlayingPlaylist;
	private static Queue queue;
	private static Queue aux_queue;
	private static int q_loop;
	private static boolean backPressed=false;
	//================DB==============================
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
		m_context 	= context;
		lbm= LocalBroadcastManager.getInstance(context);
		
		/**
		 * impostazione percorso del DB
		 */
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
		
		PlayerController.sqlDatabaseHelper = SQLiteConnect.getInstance(context, db_path, DB_NAME, DATABASE_VERSION);
	}
	
	public static Context getContext(){
		return m_context;
	}

	public void open_settings() {
		Intent intent=new Intent( mainActivity, SettingsActivity.class);
		mainActivity.startActivity(intent);
	}
	
	//Crea il database per la prima volta
	public static void createDb(){
		sqlDatabaseHelper.createDatabase();
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

	public int getQ_loop() {
		return q_loop;
	}

	public void setQ_loop(int q_loop) {
		this.q_loop = q_loop;
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
		// TODO Auto-generated method stub
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
			// TODO Auto-generated method stub
			
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
					// TODO Auto-generated method stub
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
			// TODO Auto-generated method stub
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
	
	/**
	 * Permette di ricavare la lista delle playlist con i brani associati salvate sul db (in background)
	 * setta il cursore di PlayerController (cursorPlaylist)
	 *
	 */
	
	public static class SynchronizePlaylistDb extends AsyncTask<Void, Void, Cursor>{
		private Cursor cursorPlaylist;

		@Override
		protected Cursor doInBackground(Void... params) {
			// TODO Auto-generated method stub
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
	public static void end_music_sevice(){
		Log.d("MusicService","end_music_service");
		musicSrv.stopFade();
		lbm.unregisterReceiver(songPreparedReceiver);
		m_context.unbindService(musicConnection);
		m_context.stopService( new Intent(m_context, MusicService.class));
		//musicSrv.stop();
		playingPlaylist=false;
		serviceConnected=false;
	}
	
	public static void end_music_service_preview(){
		Log.d("MusicService","end_music_service_preview");
		musicSrv.stopFade();
		lbm.unregisterReceiver(previewPreparedReceiver);
		lbm.unregisterReceiver(previewCompleteReceiver);
		lbm.unregisterReceiver(waitForDestroyService);
		m_context.unbindService(musicConnection);
		m_context.stopService( new Intent(m_context, MusicService.class));
		//musicSrv.stop();
		if(preview){
			preview=false;
			Log.d("end_music_service_preview", "impostato preview a false");
		}
		serviceConnected=false;
	}
	
	private static BroadcastReceiver songPreparedReceiver=new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			if(playingPlaylist)
				lbm.registerReceiver(songCompleteReceiver, new IntentFilter("Complete"));
			
			lbm.unregisterReceiver(songPreparedReceiver);
			SharedPreferences prefs=m_context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
			int pos=prefs.getInt("Pos", 0);
			Log.d("activity","before seek");
			seekTo(pos*1000);
			play();
		}
	};
	
	private static BroadcastReceiver songCompleteReceiver=new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			lbm.unregisterReceiver(songCompleteReceiver);
			//queue.printQueue();
			//Log.d("queue", "size= " + queue.getQueue().size());
			//Log.d("queue", "aux_queue size= " + aux_queue.getQueue().size());
			if(queue.isEmpty())
			{
				nLoopPlaylistDone++;
				if(nLoopPlaylistDone==q_loop && q_loop!=1000 )
				{
					playingPlaylist=false;
					
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
				m_context.unbindService(musicConnection);//FIXME controllare se necessario
				m_context.stopService( new Intent(m_context, MusicService.class));
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
				set_player();
			}
		}
	};
	
	private static BroadcastReceiver previewPreparedReceiver=new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			//Log.d("prepared","preview");
			
			lbm.unregisterReceiver(previewPreparedReceiver);
			
			//if(preview)
			lbm.registerReceiver(previewCompleteReceiver, new IntentFilter("Complete Preview") );
			
			//lbm.registerReceiver(previewCompleteReceiver, new IntentFilter("Complete Preview") );
			printToast("Preview of: "+currentPreviewTrack.getTitle());
			SharedPreferences prefs=m_context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
			int durationPreview=(prefs.getInt("Duration Preview", 0)+15)*1000;
			//musicSrv.preview(2000);
			preview(2000);
		}
	};
	
	private static BroadcastReceiver previewCompleteReceiver=new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			//Log.d("on complete preview","on complete preview");
			lbm.unregisterReceiver(previewCompleteReceiver);
			
			if(!queue.isEmpty()){
				
				//queue.printQueue();//stampa la coda

				currentPreviewTrack=queue.removeTop();
				uri=Uri.parse(currentPreviewTrack.getPath_track());
				//set_player();
				
				
				//Log.d("on preview complete ", "chiamato wait for destroy");
				//lbm.registerReceiver(previewPreparedReceiver, new IntentFilter("Prepared"));
			}
			else{
				//lbm.unregisterReceiver(previewPreparedReceiver);
				preview=false;
				Log.d("preview", "impostato false in onComplete");
			}
			
			lbm.registerReceiver(waitForDestroyService, new IntentFilter("Service destroy"));
			
			try{
				m_context.unbindService(musicConnection);
			}catch(IllegalArgumentException e){
				Log.d("Exception", e.toString());
			}
			m_context.stopService( new Intent(m_context, MusicService.class));
		}
	};
	
	private static BroadcastReceiver waitForDestroyService=new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			lbm.unregisterReceiver(waitForDestroyService);

			if(preview){
				Log.d("wait for destroy", "eseguo set player");
				set_player();
			}
			else{
				Log.d("preview", "end");
			}	
		}
	};
	
	public static void printToast(String s){
		Toast.makeText(m_context, s, Toast.LENGTH_SHORT).show();
	}
	
	private static MusicService musicSrv;
	private static boolean serviceConnected=false;
	private static Intent playIntent;
	private static Uri uri;
	private static LocalBroadcastManager lbm;
	private static int nLoopPlaylistDone;
	private static Random gen= new Random();
	private static boolean playingPlaylist;
	private static SinglePlaylistItem currentPreviewTrack;
	private static PlaylistItem currentPreviewPlayingPlaylist;
	private static boolean preview=false;
	private static ServiceConnection musicConnection;
	
	private static void initMusicConnection(){
		
		musicConnection = new ServiceConnection(){
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				serviceConnected=false;
				Log.d("Disc","onServiceDisconnetcted");
			}
		};
	}

	public static void set_player(){
		
			/*if(!preview){
				SharedPreferences prefs=m_context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor=prefs.edit();
				editor.putString("lastSongId", currentPlayingTrack.getId());
				editor.commit();
			}*/
			playIntent = new Intent(m_context, MusicService.class);
			
			if(musicConnection == null){
				initMusicConnection();
			}
			m_context.bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
			
			if(preview){
				lbm.registerReceiver(previewPreparedReceiver, new IntentFilter("Prepared"));
			}
			else
				lbm.registerReceiver(songPreparedReceiver, new IntentFilter("Prepared"));

			Log.d("setPlayer","setPlayer");
			//Log.d("currentPreviewTrack",currentPreviewTrack.getTitle());
	}
	
	protected static final String SETTINGS = "SETTINGS";
	
	public static void play(){
		if(musicSrv!=null && serviceConnected){
			Log.d(TAG,"play");
			musicSrv.playPlayer();
		}
	}
	
	public static void preview(int duration){
		if(musicSrv!=null && serviceConnected){
			Log.d("preview","play preview");
			musicSrv.preview(duration);
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
		// TODO Auto-generated method stub
		if (musicSrv!=null && serviceConnected)
		return musicSrv.isPng();
		return false;
	}

	public static void setLoop() {
		// TODO Auto-generated method stub
		if(musicSrv!=null && serviceConnected)
			musicSrv.Loop(true);
	}

	public static void disableLoop() {
		// TODO Auto-generated method stub
		if(musicSrv!=null && serviceConnected)
		if (musicSrv.isLooping())
			musicSrv.Loop(false);		
	}

	public static boolean isLooping() {
		// TODO Auto-generated method stub
		if(musicSrv!=null && serviceConnected)
		return musicSrv.isLooping();
		return false;
		}

	
	public static void mute() {
		// TODO Auto-generated method stub
		if(musicSrv!=null && serviceConnected)
		if (musicSrv!=null)
				musicSrv.stopFade();
				musicSrv.setVolume(0f);
		}

	public static void audio() {/*
		// TODO Auto-generated method stub
		SharedPreferences prefs=m_context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
		int fadeIn=prefs.getInt("FadeIn",0);
		if(musicSrv!=null && serviceConnected)
			if(fadeIn>0)
			musicSrv.fadeIn(fadeIn);
			else
			musicSrv.setVolume(1f);*/
			}

	
	public static boolean isMute() {
		// TODO Auto-generated method stub
		if(musicSrv!=null && serviceConnected)
		return musicSrv.isMute();
		return false;
		}


	public static void stop() {
		// TODO Auto-generated method stub
		if(musicSrv!=null && serviceConnected)
		{	lbm.registerReceiver(songPreparedReceiver, new IntentFilter("Prepared"));
			musicSrv.seek(0);
			musicSrv.stop();
		}
	}
	private static boolean random;
	
	public static void playPlaylist(PlaylistItem playlist){
		SharedPreferences prefs=m_context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
		q_loop=prefs.getInt("NLoopPlaylist", 1);
		random=prefs.getBoolean("Random Playback", false);
		
		if(!preview){
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
			//aux_queue.addSinglePlaylistItemOnTop(currentPlayingTrack);
			playSingleItem(currentPlayingTrack);
		}
		else{
			printToast("Anteprima fermata, premere di nuovo per riprodurre");
			end_music_service_preview();
			Log.d("playPlaylist", "playlist in riproduzione");
		}
	}
	public static void playSingleItem(SinglePlaylistItem i){
		currentPlayingTrack=i;
		uri=Uri.parse(currentPlayingTrack.getPath_track());
		Intent intent=new Intent(mainActivity, PlayerActivity.class);
		mainActivity.startActivity(intent);
		PlayerController.set_player();
		
	}
	
	public static void previewPlaylist(PlaylistItem playlist){
		//Log.d(TAG," preview playlist");

		if(preview)
		{
			//m_context.unbindService(musicConnection);
			//lbm.registerReceiver(previewCompleteReceiver, new IntentFilter("Complete Preview") );
			//lbm.sendBroadcast(new Intent("Complete Preview"));
			//lbm.registerReceiver(previewPreparedReceiver, new IntentFilter("Prepared") );
			//lbm.sendBroadcast(new Intent("Prepared"));
			printToast("Anteprima fermata");
			end_music_service_preview();
			//set_player();
			preview= false;
			queue.clear();
		}
		else
		{
			preview=true;
			queue.clear();
			queue.addPlaylist(playlist);
			currentPreviewPlayingPlaylist = playlist;
			previewSingleItem(queue.removeTop());
		}
		

	}
	
	public static void previewSingleItem(SinglePlaylistItem i){
		
		currentPreviewTrack=i;
		uri=Uri.parse(currentPreviewTrack.getPath_track());
		set_player();
		
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

}
