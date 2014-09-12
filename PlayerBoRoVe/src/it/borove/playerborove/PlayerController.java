/**
 * L'oggetto che gestisce il player, le playlist e la libreria
 * 
 * @author BoRoVe
 * @version 0.0, 18/07/2014 
 */
package it.borove.playerborove;

import java.io.File;






import db.SQLiteConnect;
import db.ServiceFileObserver;
import PlayerManager.Library;
import PlayerManager.Playlist;
import PlayerManager.Queue;
import PlayerManager.Track;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

public class PlayerController extends SQLiteOpenHelper{
	
	private Library library;
	private LibraryActivity libraryActivity;
	private Track currentPlayingTrack;
	private Playlist currentPlayingPlaylist;
	private MediaPlayer mediaPlayer;
	private Queue queue, aux_queue;
	private int q_loop;
	private Bundle bundleController;
	private static Activity mainActivity;
	
	
	private static final String DELETE		= "delete";
	private static final String CREATE		= "create";
	private static final String MODIFYFROM	= "modifyfrom";
	private static final String MODIFYTO	= "modifyto";
	private String value;
	
	private Context m_context;
	private static Cursor cursorTracks;
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
		db_path 	= context.getFilesDir().getPath();
		m_context 	= context;
		//this.queue= new Queue();
		//this.setQ_loop(1);
		PlayerController.sqlDatabaseHelper = new SQLiteConnect(context, db_path, DB_NAME, DATABASE_VERSION);
		//TODO inizializzazione della libreria da db
		//TODO inizializzazione del media player
		
		
	}
	
	public static void open_player(Bundle b, Bitmap image){
		Intent intent=new Intent(mainActivity, PlayerActivity.class);
		intent.putExtras(b);
		intent.putExtra("image",image);
		//intent.putExtra("image", image);
		
		mainActivity.startActivity(intent);
	}

	public void open_settings() {
		Intent intent=new Intent( mainActivity, SettingsActivity.class);
		mainActivity.startActivity(intent);
	}
	
	//Crea il database per la prima volta
	public void createDb(){
		sqlDatabaseHelper.createDatabase();
	}
	
	
	
	/*public class Controller extends Activity{
		private Intent i;
		private BroadcastReceiver rec;
		private ServiceFileObserver serviceObserver;
		private Context m_context;
		
		public Controller(Context context){
			this.m_context = context;
		}
		
		
		public void onCreate(){
			
			
			
			serviceObserver = new ServiceFileObserver(this.m_context);
			i = new Intent(Controller.this, ServiceFileObserver.class);
			ComponentName b = startService(i);
			Log.d(TAG, "SERVIZIO PARTITO!!");
			
			serviceObserver.onStart(i, 100);
					
			IntentFilter inf =new IntentFilter("it.borove.playerborove.SERVICE");
			rec = new BroadcastReceiver(){
					@Override
					public void onReceive(Context context, Intent intent) {
						// TODO Auto-generated method stub
						
						if(intent.getExtras().containsKey(PlayerController.CREATE))
							value = intent.getExtras().getString(PlayerController.CREATE);
						else if(intent.getExtras().containsKey(PlayerController.DELETE))
							value = intent.getExtras().getString(PlayerController.DELETE);
						else if(intent.getExtras().containsKey(PlayerController.MODIFYFROM))
							value = intent.getExtras().getString(PlayerController.MODIFYFROM);
						else if(intent.getExtras().containsKey(PlayerController.MODIFYTO))
							value = intent.getExtras().getString(PlayerController.MODIFYTO);	
						
						Log.d(TAG, "onReceive() ---> "+ value);

					}
				};
			registerReceiver(rec,inf);
			
		}
		@Override
		protected void onDestroy() {
			super.onDestroy();
			try{
				stopService(i);
				Log.d(TAG, "service stoppato!");
				//serviceObserver.onDestroy();
				unregisterReceiver(rec);
			}catch(Exception e){Log.e(TAG,"app destroy>>> " + e.getMessage());}
			
		}
			
	}
	*/

	
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
		//sqlDatabaseHelper.eraseDatabase();
		createDb();
		//Cursor getMp3FromStorage = getInfoMp3(this.m_context);
		//sqlDatabaseHelper.SynchronizeDb(getMp3FromStorage);
		new SynchronizeDb().execute();
		//Controller controllerActivity = new Controller(this.m_context);
		//controllerActivity.onCreate();
		
		
		
		
		
		
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
	public Cursor getInfoMetaMp3(Context context, final String namePathTrack){
		Cursor c = null;
		if(namePathTrack == null){
			String field 	= MediaStore.Audio.Media.DISPLAY_NAME + " like ?";
			String[] filter = {"%_.mp3"};
			
			
			c = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
					new String[] {	MediaStore.Audio.Media._ID,
									MediaStore.Audio.Media.DATA,
									MediaStore.Audio.Media.ARTIST,
									MediaStore.Audio.Media.ALBUM,
									MediaStore.Audio.Media.DISPLAY_NAME,
									MediaStore.Audio.Media.TITLE,
									MediaStore.Audio.Media.ALBUM_ID}, field, filter, null);
			c.moveToLast();
			if(c.getCount() == 0){
				Log.d(TAG, "cursor c non ha elementi!");
				c.close();
				return null;
			}
			c.moveToFirst();
			Log.d(TAG, "MediaStore.Audio.Media.EXTERNAL_CONTENT_URI: " + MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
			while(!c.isAfterLast()){
				for(int i=0; i < c.getColumnCount(); i++)
					Log.d(TAG, c.getColumnName(i) + ": --> " + c.getString(i));
				c.moveToNext();
			}
		}
		
		//Log.d(TAG, "external: " + MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
		else{
			//String field 	= MediaStore.Audio.Media.DISPLAY_NAME + " = " + "'"+namePathTrack+"'";
			//Log.d(TAG, "field: " + field);
			/*String field1 	= MediaStore.Audio.Media.DISPLAY_NAME + " like ?";
			String[] filter1 = {"%"+namePathTrack+"%"};
			c = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
					new String[] {	MediaStore.Audio.Media._ID,
									MediaStore.Audio.Media.DATA,
									MediaStore.Audio.Media.ARTIST,
									MediaStore.Audio.Media.ALBUM,
									MediaStore.Audio.Media.DISPLAY_NAME,
									MediaStore.Audio.Media.TITLE,
									MediaStore.Audio.Media.ALBUM_ID}, field1, filter1, null);
			c.moveToLast();
			if(c.getCount() == 0){
				Log.d(TAG, "cursor c non ha elementi!");
				c.close();
				return null;
			}
			//String[] f = {namePathTrack};
			//scanWithPath(context,f);
			c.moveToFirst();
			while(!c.isAfterLast()){
				for(int i=0; i < c.getColumnCount(); i++)
					Log.d(TAG, c.getColumnName(i) + ": --> " + c.getString(i));
				c.moveToNext();
			}
			*/
			
			
			//String[] completePath = {"/storage/emulated/0/Music/" + namePathTrack};
			String[] p = {namePathTrack};
			
			String[] paths = {namePathTrack};
	
			
			//PlayerController.scanWithPath(context, p);
			new updateDbOnTrack().execute(namePathTrack);
			
			//new SynchronizeDb().execute();
			
			
			
			//MyScannerConn client = new MyScannerConn(context, namePathTrack, null);
			
			
			
			/*c = null;
			String field 	= MediaStore.Audio.Media.DISPLAY_NAME + " like ?" ;//+ "'"+paths[0]+"'";
			String[] pathC	= {"%"+title+"%"};
			
			//Log.d(TAG, "field: " + field);
			//String field1 	= MediaStore.Audio.Media.DISPLAY_NAME + " like ?";
			//String[] filter1 = {"%_.mp3"};
			c = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
					new String[] {	MediaStore.Audio.Media._ID,
									MediaStore.Audio.Media.DATA,
									MediaStore.Audio.Media.ARTIST,
									MediaStore.Audio.Media.ALBUM,
									MediaStore.Audio.Media.DISPLAY_NAME,
									MediaStore.Audio.Media.TITLE,
									MediaStore.Audio.Media.ALBUM_ID}, field, pathC, null);
			c.moveToLast();
			if(c.getCount() == 0){
				Log.d(TAG, "onScanCompleted: cursor c non ha elementi!");
				c.close();
				c = null;
			}
			else{
				c.moveToFirst();
				while(!c.isAfterLast()){
					for(int i=0; i < c.getColumnCount(); i++)
						Log.d(TAG, c.getColumnName(i) + ": --> " + c.getString(i));
					c.moveToNext();
				}
			}
			*/
			
			
			
			
		}

		
		
		
		
		
		/*c.moveToFirst();
		
		while(!c.isAfterLast()){
			Log.d(TAG, "_ID: " + c.getString(0) + " -- ALBUM_ID: " + c.getString(6));
			c.moveToNext();
		}
		*/
		/*
		String field1 	= MediaStore.Audio.Albums.ALBUM_ID + " like ?";
		String[] filter1= {"%"};
		Cursor c1 = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
				new String[] {MediaStore.Audio.Albums.ALBUM_ART}, null, null, null);
		c1.moveToFirst();
		while(!c1.isAfterLast()){
			//String title = c1.getString(4);
			String albumArt = c1.getString(0);
			Log.d(TAG, "c1 -> album_art: " + albumArt);	
			c1.moveToNext();
		}
		c1.close();
		*/
		if(c != null)
			c.moveToFirst();
		return c;	
	}
	
	 public static void scanWithPath( final Context context, final String[] paths){
		
		OnScanCompletedListener callback = new OnScanCompletedListener() {
			

			@Override
			public void onScanCompleted(String path, Uri uri) {
				// TODO Auto-generated method stub
				//Log.e(TAG, "String path: " + path + " Uri uri: " + uri.toString() + "String path1: " + path1);
				String uri2 = uri.toString();
				String percorso = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString() + "/";
				String id = uri2.substring(percorso.length());
				
				Cursor c = null;
				String field 	= MediaStore.Audio.Media._ID + " like ?";
				String[] pathC	= {"" + id };
				
				//Log.d(TAG, "field: " + field);
				//String field1 	= MediaStore.Audio.Media.DISPLAY_NAME + " like ?";
				//String[] filter1 = {"%_.mp3"};
				c = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
						new String[] {	MediaStore.Audio.Media._ID,
										MediaStore.Audio.Media.DATA,
										MediaStore.Audio.Media.ARTIST,
										MediaStore.Audio.Media.ALBUM,
										MediaStore.Audio.Media.DISPLAY_NAME,
										MediaStore.Audio.Media.TITLE,
										MediaStore.Audio.Media.ALBUM_ID}, field, pathC, null);
				c.moveToLast();
				if(c.getCount() == 0){
					Log.d(TAG, "onScanCompleted: cursor c non ha elementi!");
					c.close();
					c = null;
				}
				else{
					c.moveToFirst();
					while(!c.isAfterLast()){
						for(int i=0; i < c.getColumnCount(); i++)
							Log.d(TAG, c.getColumnName(i) + ": --> " + c.getString(i));
						c.moveToNext();
					}
				}
			}
			
		};
		
		MediaScannerConnection.scanFile(context, paths, null, callback);
		return;
	}
	 
	 
	 final class MyScannerConn implements MediaScannerConnectionClient{
		 private String completePath;
		 private String mimeType;
		 private MediaScannerConnection mConn;
		 private Context m_context;
		 
		 public MyScannerConn(Context conn, String path, String mimeType){
			 this.completePath = path;
			 this.mimeType = mimeType;
			 this.m_context = conn;
			 mConn = new MediaScannerConnection(conn, this);
			 mConn.connect();
		 }

		@Override
		public void onMediaScannerConnected() {
			// TODO Auto-generated method stub
			mConn.scanFile(completePath, this.mimeType);
			
		}

		@Override
		public void onScanCompleted(String path, Uri uri) {
			// TODO Auto-generated method stub
			//Log.e(TAG, "String path: " + path + " Uri uri: " + uri.toString());
			
			
			String uri2 = uri.toString();
			String percorso = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString() + "/";
			String id = uri2.substring(percorso.length());
			//Log.d(TAG, "_id TRACK: " + id);
			mConn.disconnect();
			
			
			Cursor c = null;
			String field 	= MediaStore.Audio.Media._ID + " = ?" ;//+ "'"+paths[0]+"'";
			String[] pathC	= {"" + id};
			
			//Log.d(TAG, "field: " + field);
			//String field1 	= MediaStore.Audio.Media.DISPLAY_NAME + " like ?";
			//String[] filter1 = {"%_.mp3"};
			c = m_context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
					new String[] {	MediaStore.Audio.Media._ID,
									MediaStore.Audio.Media.DATA,
									MediaStore.Audio.Media.ARTIST,
									MediaStore.Audio.Media.ALBUM,
									MediaStore.Audio.Media.DISPLAY_NAME,
									MediaStore.Audio.Media.TITLE,
									MediaStore.Audio.Media.ALBUM_ID}, field, pathC, null);
			c.moveToLast();
			if(c.getCount() == 0){
				Log.d(TAG, "onScanCompleted: cursor c non ha elementi!");
				c.close();
				c = null;
			}
			else{
				c.moveToFirst();
				while(!c.isAfterLast()){
					for(int i=0; i < c.getColumnCount(); i++)
						Log.d(TAG, c.getColumnName(i) + ": --> " + c.getString(i));
					c.moveToNext();
				}
				c.moveToFirst();
				
			}
			
			
			
			
			
		}

	 }
	 
	 
	 
	 
	 public class updateDbOnTrack extends AsyncTask<String,Void,Void>{
		 private OnScanCompletedListener callback;
		 private String artist;
		 private String pathTrack;
		 private String title;
		 private String album_id;
		 private String display_name;
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
				Log.d(TAG, "file da aggiornare!!");
				File file =  Environment.getExternalStorageDirectory();
				String path2 = file.getPath() + "/Music/";
				display_name = completeString.substring(path2.length());
				
				String oldValue = completeString.substring(0, completeString.indexOf("$"));
				String old_display_name = oldValue.substring(path2.length());
				
				String newValue	= completeString.substring(completeString.indexOf("$")+1, completeString.length());
				String new_display_name = newValue.substring(path2.length());
				
				Log.d(TAG, "old_display_name: " + old_display_name + " new_display_name: "+ new_display_name);
				Cursor c = sqlDatabaseHelper.getExactlyTrack(old_display_name, SQLiteConnect.COLUMN_TITLE);
				if(c != null){
					c.moveToFirst();
					sqlDatabaseHelper.updateRowTrack(c.getString(0), new_display_name, SQLiteConnect.COLUMN_TITLE);
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
						String field 	= MediaStore.Audio.Media._ID + " like ?";
						String[] pathC	= {"" + id };
						
						c = m_context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
								new String[] {	MediaStore.Audio.Media._ID,
												MediaStore.Audio.Media.DATA,
												MediaStore.Audio.Media.ARTIST,
												MediaStore.Audio.Media.ALBUM,
												MediaStore.Audio.Media.DISPLAY_NAME,
												MediaStore.Audio.Media.TITLE,
												MediaStore.Audio.Media.ALBUM_ID}, field, pathC, null);
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
							String temp = c.getString(1);
							File file =  Environment.getExternalStorageDirectory();
							String pt = file.getPath() + "/Music/";
							display_name = temp.substring(pt.length());
							//Log.d(TAG, "display_name: " + display_name);
							
							sqlDatabaseHelper.addRowTrack(display_name, kind, artist, vote, title, album_id, pathTrack);
							Log.d(TAG, "Aggiunto il brano!  --> " + display_name);
							
	
						}
						
							
					}
					else{
						isCanceled = true;
						Log.d(TAG, "Il brano è da cancellare!");
						File file =  Environment.getExternalStorageDirectory();
						String path2 = file.getPath() + "/Music/";
						display_name = completeString.substring(path2.length());
						sqlDatabaseHelper.deleteRowTrack(display_name, SQLiteConnect.COLUMN_TITLE);
					}
					
					/*toLibrary.moveToFirst();
					if(toLibrary != null){
						//LibraryActivity.notifyToLibrary(allTracks);
						while(!toLibrary.isAfterLast()){
							for(int i=0; i< toLibrary.getColumnCount(); i++)
								Log.d(TAG, "doInBackground--->  " + toLibrary.getColumnName(i) + ": " + toLibrary.getString(i));
							toLibrary.moveToNext();
						}
						
					}
					*/

				}
				
			};
			MediaScannerConnection.scanFile(m_context, paths, null, callback);
			
			/*String[] columnsSelect = {"pid AS _id, title, singerName, kind, vote, contentTitle, albumId"};
			//Cursor allTracks = sqlDatabaseHelper.getFilteredTrack("", SQLiteConnect.COLUMN_TITLE, columnsSelect);
			toLibrary.moveToFirst();
			if(toLibrary != null){
				//LibraryActivity.notifyToLibrary(allTracks);
				while(!toLibrary.isAfterLast()){
					for(int i=0; i< toLibrary.getColumnCount(); i++)
						Log.d(TAG, "doInBackground--->  " + toLibrary.getColumnName(i) + ": " + toLibrary.getString(i));
					toLibrary.moveToNext();
				}
				
			}
			*/
			
			
			return null;
		}
		
		protected void onPostExecute(Void result){
			//Log.d(TAG, "onPostExecute!");
			//String[] all = {"*"};
			//String[] columnsSelect = {"pid AS _id, title, singerName, kind, vote, contentTitle, albumId"};
			//new SynchronizeDb().execute();
			/*Cursor allTracks = sqlDatabaseHelper.getFilteredTrack("", SQLiteConnect.COLUMN_TITLE, columnsSelect);
			if(allTracks != null){
				//LibraryActivity.notifyToLibrary(allTracks);
				while(!allTracks.isAfterLast()){
					for(int i=0; i< allTracks.getColumnCount(); i++)
						Log.d(TAG, "onPostExecute--->  " + allTracks.getColumnName(i) + ": " + allTracks.getString(i));
					allTracks.moveToNext();
				}
				
			}
			else
				Log.d(TAG, "allTracks è NULL!");
			 */
		}
		 
	 }

	
	/*public static String getAlbumMp3(String title){
		String uri = "/storage/emulated/0/Music";
		Log.d(TAG, "uri: " + uri);
		
		MediaMetadataRetriever mmr = new MediaMetadataRetriever();
		mmr.setDataSource(uri);
		String albumName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
		
		return albumName;
	}
	*/
	
	
	private class SynchronizeDb extends AsyncTask<Void, Void, Cursor>{
		private Cursor getTracksFromDb;
		@Override
		protected Cursor doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try{
				//Cursor getMp3FromStorage = getInfoMp3(m_context);
				Cursor getMp3FromStorage = getInfoMetaMp3(m_context, null);
				sqlDatabaseHelper.SynchronizeDb(getMp3FromStorage);
				String[] columnsSelect = {"pid AS _id, title, singerName, kind, vote, contentTitle, albumId, pathTrack"};
				getTracksFromDb =  sqlDatabaseHelper.getFilteredTrack("", SQLiteConnect.COLUMN_TITLE, columnsSelect);
				//if(getTracksFromDb != null)
				//	Log.d(TAG, "getTracksFromDb NON è null!");
				//getMp3FromStorage.moveToFirst();
				getTracksFromDb.moveToFirst();
				//Log.d(TAG, "DENTRO DoInBaCkground in syncronizeDb!!!");
				/*while(!getMp3FromStorage.isAfterLast()){
					String id		= getMp3FromStorage.getString(0);
					String title 	=  getMp3FromStorage.getString(1);
					//String author 	=  getMp3FromStorage.getString(2);
					//String kind		=  getMp3FromStorage.getString(3);
					//String vote		=  getMp3FromStorage.getString(4);
					//String cont		=  getMp3FromStorage.getString(5);
					String albumId		=  getMp3FromStorage.getString(6);
					Log.d(TAG, "_id: " + id + " title: " + title + " albumId: " + albumId);
					
					
					getMp3FromStorage.moveToNext();
				}
				getMp3FromStorage.close();
				*/
				//getTracksFromDb.close();
				
			}catch(Exception e){
				e.printStackTrace();
				}
			Log.d(TAG, "db sincronizzato!");
			//Toast.makeText(m_context, "db sincronizzato", Toast.LENGTH_SHORT).show();
			return getTracksFromDb;
		}
		
		protected void onPostExecute(Cursor result){
			if(result != null){
				//Log.d(TAG, "result NON è null!!!");
				setCursorTracks(getTracksFromDb);
				getTracksFromDb.moveToFirst();
				while(!getTracksFromDb.isAfterLast()){
					for(int i=0; i< getTracksFromDb.getColumnCount(); i++){
						Log.e(TAG, getTracksFromDb.getColumnName(i) + ": " + getTracksFromDb.getString(i));
					}
					getTracksFromDb.moveToNext();
				}
				
			}
			
			else
				Log.d(TAG, "result is null!!!");
		}
		
	}
	
	public static final String[] getColumns(){
		String[] from = new String[]{  SQLiteConnect.COLUMN_ALBUM_ID, SQLiteConnect.COLUMN_TITLE, SQLiteConnect.COLUMN_SINGER_NAME, 
				SQLiteConnect.COLUMN_KIND, SQLiteConnect.COLUMN_VOTE};
		return from;
	}
	
	public static Cursor getCursorTracks(){
		String[] columnsSelect = {"pid AS _id, title, singerName, kind, vote, contentTitle, albumId, pathTrack"};
		//String field = SQLiteConnect.COLUMN_TITLE + " like ?";
		//String[] filter = {"%_"};
		//Uri dbPath = Uri.parse(SQLiteConnect.getPathDb());
		sqlDatabaseHelper.openDatabaseReadOnly();
		Cursor cursorTracks = sqlDatabaseHelper.getDb().query(SQLiteConnect.TABLE_NAME_TRACK, columnsSelect,null,null,null,null,null);
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
	
	public void setCursorTracks(Cursor cursor){
		PlayerController.cursorTracks = cursor;
	}
	public static void setVoteTrackFromActivityLibrary(int _id, int vote){
		Cursor c = sqlDatabaseHelper.getExactlyTrack(String.valueOf(vote), SQLiteConnect.COLUMN_VOTE);
		if(c != null){
			String oldValue = c.getString(c.getColumnIndex(SQLiteConnect.COLUMN_VOTE));
			sqlDatabaseHelper.updateRowTrack(oldValue, String.valueOf(vote), SQLiteConnect.COLUMN_VOTE);
		}
		//Log.d(TAG, "Fatto!");
	}
	public static void setTagTrackFromActivityLibrary(int _id, String fileNameTrack, String authorName,String albumName, String kind, int vote){
		Cursor c = sqlDatabaseHelper.getExactlyTrack(String.valueOf(_id), SQLiteConnect.COLUMN_ID);
		if(c != null){
			String oldValueFileName = c.getString(c.getColumnIndex(SQLiteConnect.COLUMN_TITLE));
			String oldValueAuthor = c.getString(c.getColumnIndex(SQLiteConnect.COLUMN_SINGER_NAME));
			//String oldValueAlbum = c.getString(c.getColumnIndex(SQLiteConnect.COLUMN_TITLE));
			String oldValuekind = c.getString(c.getColumnIndex(SQLiteConnect.COLUMN_KIND));
			String oldValueVote = c.getString(c.getColumnIndex(SQLiteConnect.COLUMN_VOTE));
			
			if(!oldValueFileName.equals(fileNameTrack))
				sqlDatabaseHelper.updateRowTrack(String.valueOf(_id), fileNameTrack, SQLiteConnect.COLUMN_TITLE);
			if(!oldValueAuthor.equals(authorName))
				sqlDatabaseHelper.updateRowTrack(String.valueOf(_id), authorName, SQLiteConnect.COLUMN_SINGER_NAME);
			if(!oldValuekind.equals(kind))
				sqlDatabaseHelper.updateRowTrack(String.valueOf(_id), kind, SQLiteConnect.COLUMN_KIND);			
			if(!oldValueVote.equals(String.valueOf(vote)))
				sqlDatabaseHelper.updateRowTrack(String.valueOf(_id), String.valueOf(vote), SQLiteConnect.COLUMN_VOTE);
			
		}
		//Log.d(TAG, "Fatto!");
	}
	
	public static void deleteRowTrack(int _id){
		if(_id >= 0)
			sqlDatabaseHelper.deleteRowTrack(String.valueOf(_id), SQLiteConnect.COLUMN_ID);	
	}
	
}
