/**
 * Questa classe implementa la connessione con il db e la sua gestione
 * 
 * @author "Pasquale Verlotta - pasquale.verlotta@gmail.com"
 */
package db;

import java.io.File;
import java.nio.channels.GatheringByteChannel;
import java.util.concurrent.SynchronousQueue;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.MediaStore;
import android.util.Log;

public class SQLiteConnect extends SQLiteOpenHelper{
	
	private static String DB_NAME;
	private static int DATABASE_VERSION;
	private static String db_path;
	
	private final static String LOG= "SQLiteConnect";
	
	public static final
	String TABLE_NAME_PLAYLIST 		= "playlist";
	public static final
	String COLUMN_ID 				= "pid";
	public static final
	String COLUMN_NAME 				= "nameP";
	
	public static final
	String TABLE_NAME_TRACK 		= "track";
	public static final
	String COLUMN_PATH_TRACK		= "pathTrack";
	public static final
	String COLUMN_KIND				= "kind";
	public static final
	String COLUMN_TITLE				= "title";
	public static final
	String COLUMN_SINGER_NAME		= "singerName";
	public static final
	String COLUMN_VOTE				= "vote";
	public static final
	String COLUMN_CONTENT_TITLE		= "contentTitle";
	public static final
	String COLUMN_ALBUM_ID			= "albumId";
	
	public static final
	String TABLE_NAME_CONTAINS		= "contains";
	public static final
	String COLUMN_ID_BID			= "bid";
	public static final
	String COLUMN_ID_PID			= "pid";
		
	private SQLiteDatabase m_db;
	private Context m_context;
	
	//private MyFileObserver fileOb;
	
	public SQLiteConnect(Context context, final String path, final String dbName, final int version){
		super(context, dbName, null, version);
		m_context			= context;
		db_path 			= path;
		setDATABASE_VERSION(version);
		DB_NAME 			= dbName;

	}
	
	/**
	 * Se non esiste crea il nuovo database(solo tabelle e senza entry)
	 * @return true se il database già esisteva, false se è stato appena creato
	 */
	public boolean createDatabase(){
		boolean dbExist = checkDatabase();
		if(dbExist){
			// non fare nulla, il db esiste già
			Log.d(LOG, "database già esistente");
		}
		else{		
			try{	
				m_db = SQLiteDatabase.openDatabase(db_path + DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
			}catch(SQLiteException e){
				Log.d(LOG, "errore nella creazione database: " + e.getMessage());
			}
			/*
			 ** Creazione delle tabelle
			 */
			String newTableTrackQueryString =	"create table " + 
					TABLE_NAME_TRACK + 	" (" + 
					COLUMN_ID +  " integer primary " + "key autoincrement not null," + 
					COLUMN_TITLE + " text not null," +
					COLUMN_SINGER_NAME + " text not null," +
					COLUMN_KIND + " text not null," + 
					COLUMN_VOTE + " text not null," +
					COLUMN_CONTENT_TITLE 	+ " text not null," + 
					COLUMN_ALBUM_ID + " text not null," +
					COLUMN_PATH_TRACK + ");";
			
			String newTablePlaylistQueryString =	"create table " + 
					TABLE_NAME_PLAYLIST + 	" (" + 
					COLUMN_ID +  " integer primary " + "key autoincrement not null," + 
					COLUMN_NAME + " text not null" + ");";
			
			String newTableContainsQueryString =	"create table " + 
					TABLE_NAME_CONTAINS + 	" (" + 
					COLUMN_ID_BID +  " integer primary " + "key," + 
					COLUMN_ID_PID + " text not null," + 
					" foreign key (" + COLUMN_ID_BID + ") references " + TABLE_NAME_TRACK + "(" + COLUMN_ID +") " 
					+ "on delete cascade," +
					" foreign key (" + COLUMN_ID_PID + ") references " + TABLE_NAME_PLAYLIST + "(" + COLUMN_ID + ") "
					+ "on delete cascade" + ");";
			try{
				m_db.execSQL(newTableTrackQueryString);
			}catch(SQLException e){
				e.printStackTrace();
				Log.d(LOG, "Eccezione nella creazione della tabella Track!");
			}
			try{
				m_db.execSQL(newTablePlaylistQueryString);
			}catch(SQLException e){
				e.printStackTrace();
				Log.d(LOG, "Eccezione nella creazione della tabella Playlist!");
			}
			try{
				m_db.execSQL(newTableContainsQueryString);
			}catch(SQLException e){
				e.printStackTrace();
				Log.d(LOG, "Eccezione nella creazione della tabella Contains!");
			}
		}
		return dbExist;
		
	}
	/**
	 * Controlla se ail dabase esiste già per evitare ricopiature del fileogni volta che riapri l'app
	 * @return true se il db esiste, falso altrimenti
	 */
	private boolean checkDatabase(){
		String path = db_path + DB_NAME;
		File file = new File(path);
		if(file.exists()){
			//Log.d(LOG, "file esiste!!!");	
			try{
				openDatabaseReadOnly();
				closeDatabase();
			}catch(SQLiteException e){e.printStackTrace();}
		}
		return m_db != null ? true : false;
	}
	
	public void openDatabaseRW(){
			try{
				m_db = SQLiteDatabase.openDatabase(db_path + DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
			}catch(SQLiteException e){e.printStackTrace();}
	}
	
	public void openDatabaseReadOnly(){
		try{
			m_db = SQLiteDatabase.openDatabase(db_path + DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
		}catch(SQLiteException e){Log.d(LOG,"Eccezione in apertura database");}
	}
	
	public void closeDatabase(){
		if(m_db != null){
			m_db.close();
		}
		else{
			Log.d(LOG, "db null.");
		}
	}
	public SQLiteDatabase getDb(){
		return this.m_db;
	}
	
	/**
	 * Aggiunge una nuova playlist
	 * @param namePlaylist		nome della playlist da aggiungere
	 * @return
	 */
	public Cursor addRowPlaylist(String namePlaylist){
		ContentValues content = new ContentValues();
		content.put(COLUMN_NAME, namePlaylist);
		try{
			 openDatabaseRW();
			 m_db.insert(TABLE_NAME_PLAYLIST, null, content);
			 closeDatabase();
		}catch(Exception e){
			Log.d(LOG, "Errore nella addRowPlaylist!! " + e.getMessage());
		}
		return getExactlyNamePlaylist(namePlaylist);	
	}
	/**
	 * Aggiunge un nuovo brano
	 * @param title				nome del file con estensione(.mp3 ecc..)
	 * @param kind				genere del brano
	 * @param nameSinger		nome dell'artista
	 * @param vote				voto di preferenza assegnato
	 * @param contentTitle		il titolo del brano(senza estensione)
	 * @param albumId			id dell'album(copertina dell'album rappresentato come long int)
	 * @return il brano appena aggiunto
	 */
	public Cursor addRowTrack(String title, String kind, String nameSinger, String vote, String contentTitle, String albumId, String path){
		ContentValues content = new ContentValues();
		content.put(COLUMN_TITLE, title);
		content.put(COLUMN_KIND, kind);
		content.put(COLUMN_SINGER_NAME, nameSinger);
		content.put(COLUMN_VOTE, vote);
		content.put(COLUMN_CONTENT_TITLE, contentTitle);
		content.put(COLUMN_ALBUM_ID, albumId);
		content.put(COLUMN_PATH_TRACK, path);
		
		try{
			 openDatabaseRW();
			 m_db.execSQL("PRAGMA foreign_keys = ON");
			 m_db.insert(TABLE_NAME_TRACK, null, content);	 
			 closeDatabase();
		}catch(Exception e){
			Log.d(LOG, "Errore nella addRowTrack!! " + e.getMessage());
		}
		
		return getExactlyTrack(title,"title");
	}
	/**
	 * Aggiunge una nuova entry nella tabella contains indicando l'associazione tra brano e playlist
	 * @param idPlaylist
	 * @param idTrack
	 */
	public void addRowContains(String idPlaylist, String idTrack){
		if(idPlaylist.equals(null) || idTrack.equals(null)){
			return;
		}	
		ContentValues values = new ContentValues();
		values.put(COLUMN_ID_BID, idTrack);
		values.put(COLUMN_ID_PID, idPlaylist);
		try{
			openDatabaseRW();
			m_db.insert(TABLE_NAME_CONTAINS, null, values);
			closeDatabase();
		}
		catch (Exception e){
				Log.d(LOG, "Errore in containsTrack!! " + e.getMessage());
		}
	}

	/**
	 * Cancella un brano singolo
	 * @param value			il valore di ricerca del brano 
	 * @param columnType	il tipo di colonna cui value fà parte
	 */
	public void deleteRowTrack(String value, String columnType){	
		try{
			openDatabaseRW();
			m_db.execSQL("PRAGMA foreign_keys = ON");
			if(columnType.equals(COLUMN_TITLE))
				m_db.delete(TABLE_NAME_TRACK, COLUMN_TITLE + "=" + "'" + value + "'", null);
			else if(columnType.equals(COLUMN_KIND))
				m_db.delete(TABLE_NAME_TRACK, COLUMN_KIND + "=" + "'" + value + "'", null);
			else if(columnType.equals(COLUMN_SINGER_NAME))
				m_db.delete(TABLE_NAME_TRACK, COLUMN_SINGER_NAME + "=" + "'" + value + "'", null);
			else if(columnType.equals(COLUMN_VOTE))
				m_db.delete(TABLE_NAME_TRACK, COLUMN_VOTE + "=" + "'" + value + "'", null);
			else if(columnType.equals(COLUMN_CONTENT_TITLE))
				m_db.delete(TABLE_NAME_TRACK, COLUMN_CONTENT_TITLE + "=" + "'" + value + "'", null);
			closeDatabase();
		}
		catch (SQLiteException e){
			Log.d(LOG, "Errore nella deleteRowTrack!! " + e.getMessage());
		}
	}
	
	/**
	 * Cancella una playlist
	 * @param name		nome della playlist da eliminare
	 */
	public void deleteRowPlaylist(String name){
		try{
			openDatabaseRW();
			m_db.execSQL("PRAGMA foreign_keys = ON");
			m_db.delete(TABLE_NAME_PLAYLIST, COLUMN_NAME + "=" + "'"+name+"'", null);
			closeDatabase();
		}
		catch (Exception e){
			Log.d(LOG, "Errore nella deleteRowPlaylist!! " + e.getMessage());
		}	
	}
	
	public void eraseDatabase(){
		String path = db_path + DB_NAME;
		SQLiteDatabase.deleteDatabase(new File(path));
		m_db = null;
		Log.d(LOG, "Database cancellato!!");
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		//eraseDatabase();
		//createDatabase();
		//if(SynchronizeDb()){
		//	Log.d(LOG, "Database sincronizzato!!");
		//}
		//	Log.d(LOG, "Database non sincronizzato!!");
					
	}
	
	public void SynchronizeDb(Cursor getMp3FromStorage){
		//boolean result = false;
		String[] column = {COLUMN_CONTENT_TITLE};
		// Ottieni tutti i brani presenti nel db
		openDatabaseRW();
		Cursor tableTrack = m_db.query(TABLE_NAME_TRACK, column,null,null,null,null,null);
		//Cursor c = getInfoMp3(this.m_context);
		Cursor c = getMp3FromStorage;
		tableTrack.moveToLast();
		if(tableTrack.getCount() > 0){
			//Log.d(LOG, "dentro if!!!!!!!!!!");
			if(c != null){
				//Log.d(LOG, "se c != null!!!!!!!!!!");
				tableTrack.moveToFirst();
				while(!tableTrack.isAfterLast()){
					boolean trackFound = false;
					String valueContentTitle = tableTrack.getString(tableTrack.getColumnIndex(COLUMN_CONTENT_TITLE));
					//Log.d(LOG, "tableTrack.getString(0): " + tableTrack.getString(0));
					c.moveToFirst();
					while(!c.isAfterLast()){		
						//Log.d(LOG, "c.getString(5): " + c.getString(5));
						if(valueContentTitle.equals(c.getString(5))){
							trackFound = true;
							break;			
						}				
						c.moveToNext();
					}
					if(!trackFound){
						//Log.d(LOG, "brano cancellato: " + tableTrack.getString(0));
						this.deleteRowTrack(valueContentTitle, COLUMN_CONTENT_TITLE);			
					}	
					tableTrack.moveToNext();
				}	
			}
			else{
				Log.d(LOG, "cancello tutto !!!!!!!!!");
				tableTrack.moveToFirst();
				while(!tableTrack.isAfterLast()){			
					String valueContentTitle = tableTrack.getString(tableTrack.getColumnIndex(COLUMN_CONTENT_TITLE));
					this.deleteRowTrack(valueContentTitle, COLUMN_CONTENT_TITLE);
					tableTrack.moveToNext();
				}
			}
		}
		closeDatabase();
			if(c != null){
				while(!c.isAfterLast()){
					String vote			= "0";
					String kind			= "unknown";
					String pathTrack	= c.getString(1);
					String contentTitle	= c.getString(5);
					String title 		= c.getString(4);
					String singerName	= c.getString(2);
					String albumId		= c.getString(6);
					
					Cursor trackOnDb = getExactlyTrack(contentTitle, COLUMN_CONTENT_TITLE);
					if(trackOnDb == null){				
							Cursor e = addRowTrack(title, kind, singerName, vote, contentTitle, albumId, pathTrack);
							Log.d(LOG, "nuovo brano rilevato e aggiunto al db");
							Log.d(LOG, e.getColumnName(1) + ": " + e.getString(1) + " " + e.getColumnName(2) + ": " + e.getString(2) + 
									" " + e.getColumnName(3) + ": " + e.getString(3) + " " + e.getColumnName(4) + ": " + e.getString(4) +
									" " + e.getColumnName(5) + ": " + e.getString(5) + " " + e.getColumnName(6) + ": " + e.getString(6) + 
									" " + e.getColumnName(7) + ": " + e.getString(7));			
					}
					else{
						if(!trackOnDb.getString(1).equals(title)){
							updateRowTrack(trackOnDb.getString(1), title, COLUMN_TITLE);
							Log.d(LOG, "aggiornato il titolo del brano: " + trackOnDb.getString(1));
						}	
						if(!trackOnDb.getString(2).equals(singerName)){
							updateRowTrack(trackOnDb.getString(2), singerName, COLUMN_SINGER_NAME);
							Log.d(LOG, "aggiornato il nome artista: " + trackOnDb.getString(2));			
						}		
					}
				
					c.moveToNext();
				}
			}
			else
				Log.d(LOG, "cursor c è null!!");
	
		//return result;
	}

	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}
	
	/**
	 * Aggiorna il brano singolo
	 * @param oldValue		il vecchio valore da aggiornare
	 * @param newValue		il nuovo valore che aggiorna oldValue
	 * @param columnType 	la colonna cui oldValue/newValue fanno parte
	 */
	public void updateRowTrack(String oldValue, String newValue, String columnType){
		
		ContentValues contentValues = new ContentValues();
		if(columnType.equals(COLUMN_SINGER_NAME) || columnType.equals(COLUMN_KIND) || columnType.equals(COLUMN_TITLE) || columnType.equals(COLUMN_VOTE))
			contentValues.put(columnType, newValue);
		
		String where = columnType + "='" + oldValue + "'";
		openDatabaseRW();
		m_db.execSQL("PRAGMA foreign_keys = ON");
		m_db.update(TABLE_NAME_TRACK, contentValues, where, null);
		onUpgrade(this.m_db, SQLiteConnect.getDATABASE_VERSION(), SQLiteConnect.getDATABASE_VERSION() +1);
		closeDatabase();
		return;
	}
	
	/**
	 * Aggiorna il singolo playlist
	 * @param oldValue		il vecchio valore da aggiornare
	 * @param newValue		il nuovo valore che aggiorna oldValue
	 * @param columnType 	la colonna cui oldValue/newValue fanno parte
	 */
	public void updateRowPlaylist(String oldValue, String newValue, String columnType){
		ContentValues contentValues = new ContentValues();
		if(columnType.equals(COLUMN_NAME))
			contentValues.put(columnType, newValue);
		
		String where = columnType + "='" + oldValue + "'";
		openDatabaseRW();
		m_db.execSQL("PRAGMA foreign_keys = ON");
		m_db.update(TABLE_NAME_PLAYLIST, contentValues, where, null);
		onUpgrade(this.m_db, SQLiteConnect.getDATABASE_VERSION(), SQLiteConnect.getDATABASE_VERSION() +1);
		closeDatabase();
		return;
		
	}
	
	/**
	 * Si ricerca una singola playlist
	 * @param name			il nome della playlist da ricercare
	 * @return
	 */
	public Cursor getExactlyNamePlaylist(String name){
		openDatabaseReadOnly();
		String field = "nameP = ?";
		String [] filter = {name};
		Cursor cursor = m_db.query(SQLiteConnect.TABLE_NAME_PLAYLIST, null, field, filter, null, null, null);
		cursor.moveToLast();
		if(cursor.getCount() != 1){
			cursor.close();
			closeDatabase();
			return null;
		}
		cursor.moveToFirst();		
		closeDatabase();
		return cursor;
	}
	
	/**
	 * Si ricerca una singola traccia
	 * @param value			il valore(titolo, voto...) richiesto per la query
	 * @param columnType	la colonna contenente l'oggetto 'value'
	 * @return
	 */
	public Cursor getExactlyTrack(String value, String columnType){
		openDatabaseReadOnly();
		Cursor cursor = null;
		if(!value.equals(null)){
			String field = columnType + "='" + value + "'";
			cursor = m_db.query(SQLiteConnect.TABLE_NAME_TRACK, null, field, null, null, null, null);
		}
		cursor.moveToLast();
		if(cursor.getCount() == 0){
			cursor.close();
			closeDatabase();
			return null;
		}
		cursor.moveToFirst();		
		Log.d(LOG, "Track trovata: "+ cursor.getString(0) +" " +cursor.getString(1) +" "+cursor.getString(2) +" "+cursor.getString(3) +
				 " " + cursor.getString(4) +" " +cursor.getString(5) + " " + cursor.getLong(6));
		closeDatabase();
		return cursor;
	}
	
	public Cursor getFilteredTrack(String value, final String columnType, String[] columnsSelect){
		openDatabaseReadOnly();
		Cursor cursor = null;
		if(!columnsSelect.equals("*")){
			String field = columnType + " like ?";
			String [] filter = {value + "%"};
			cursor = m_db.query(SQLiteConnect.TABLE_NAME_TRACK, columnsSelect, field, filter, null, null, null);
		}
		else{
			String field = columnType + " like ?";
			String [] filter = {value + "%"};
			cursor = m_db.query(SQLiteConnect.TABLE_NAME_TRACK, null, field, filter, null, null, null);
		}
		cursor.moveToLast();
		if(cursor.getCount() >= 300){
			cursor.close();
			closeDatabase();
			return null;
		}
		cursor.moveToFirst();		
		closeDatabase();
		return cursor;
	}
	
	/**
	 * Restituisce un insieme di playlist dalla ricerca
	 * @param value				il valore inserito dall'utente. Se è vuota (""), restituisce tutte le entry presenti nella tabella
	 * @param columnType		la colonna cui value fa parte
	 * @param columnsSelect		le colonne restituite dalla query(SELECT). Se è uguale a "*" restituisce tutte le colonne
	 * @return
	 */
	public Cursor getFilteredPlaylist(String value, String columnType, String[] columnsSelect){
		openDatabaseReadOnly();
		Cursor cursor = null;
		if(!columnsSelect.equals("*")){
			String field = columnType + " like ?";
			String [] filter = {value + "%"};
			cursor = m_db.query(SQLiteConnect.TABLE_NAME_PLAYLIST, columnsSelect, field, filter, null, null, null);
		}
		else{
			String field = columnType + " like ?";
			String [] filter = {value + "%"};
			cursor = m_db.query(SQLiteConnect.TABLE_NAME_PLAYLIST, null, field, filter, null, null, null);
		}
		cursor.moveToLast();
		if(cursor.getCount() >= 100){
			cursor.close();
			closeDatabase();
			return null;
		}
		cursor.moveToFirst();		
		closeDatabase();
		return cursor;
	}
	
	/** quando si effettuato query complesse
	 * @param where			la stringa inserita dall' utente
	 * @param attr			il nome della colonna cui appartiene il parametro 'where'
	 * @param table_src		la tabella di origine della ricerca
	 * @param table dst		la tabella di destinazione della query (dove viene presa l'informazione richiesta dall'utente)
	 * @param select		la/e colonna/e richieste dalla query
	 * @return cursor		la tabella contenente le entry che soddisfano la query 
	 * 
	 */
	public Cursor getQueryResult(String where, String attr, String table_src, String table_dst, String select){
		openDatabaseReadOnly();
		String query 		= "";
		Cursor cursor	 	= null;
		if(table_src.equals(TABLE_NAME_TRACK)){
			if(table_dst.equals(TABLE_NAME_PLAYLIST)){
				try{
					String field 		= attr + " like ?";
					String [] filter 	= {where + "%" };
					query		= "SELECT "+ "b." + select + " FROM " + table_src + " AS a, " + table_dst + " AS b, " + TABLE_NAME_CONTAINS + " AS c"
									+ " WHERE " + "a." + COLUMN_ID + "=c." + COLUMN_ID_BID + " AND " + "b." + COLUMN_ID + "=c." + COLUMN_ID_PID 
									+ " AND " + "a." + field;
					cursor = m_db.rawQuery(query, filter);
				}catch(SQLiteException e ){e.printStackTrace();}
			}
			else if(table_dst.equals(TABLE_NAME_CONTAINS)){
				try{
					String field 		= attr + " = '" + where + "'";
					String [] filter 	= null;
					query		= "SELECT "+ "c." + select + " FROM " + table_src + " AS a, " + table_dst + " AS c"
							+ " WHERE " + "a." + COLUMN_ID + "=c." + COLUMN_ID_BID + " AND " + "a." + field; 
					cursor = m_db.rawQuery(query, filter);
				}catch(SQLiteException e ){e.printStackTrace();}
			}
		}
		else if(table_src.equals(TABLE_NAME_PLAYLIST)){
			if(table_dst.equals(TABLE_NAME_CONTAINS)){
				try{
					String field 		= attr + " = '" + where + "'";
					String [] filter 	= null;
					query		= "SELECT "+ "c."+select + " FROM " + table_src + " AS a, " + TABLE_NAME_CONTAINS + " AS c"
							+ " WHERE " + "a." + COLUMN_ID + "=c." + COLUMN_ID_PID + " AND " + "a." + field;
					cursor = m_db.rawQuery(query, filter);
				}catch(SQLiteException e ){e.printStackTrace();}
			}
			else{
				try{
					String field 		= attr + " like ?";
					String [] filter 	= {where + "%" };
					query		= "SELECT "+ "b."+select + " FROM " + table_src + " AS a, " + table_dst + " AS b, " + TABLE_NAME_CONTAINS + " AS c"
							+ " WHERE " + "a." + COLUMN_ID + "=c." + COLUMN_ID_PID + " AND " + "b." + COLUMN_ID + "=c." + COLUMN_ID_BID 
							+ " AND " + "a." + field;
					cursor = m_db.rawQuery(query, filter);
				}catch(SQLiteException e ){e.printStackTrace();}
			}
		}
		
		else return null;
		cursor.moveToLast();
		if(cursor.getCount() == 0){
			cursor.close();
			closeDatabase();
			return null;
		}
		cursor.moveToFirst();
		closeDatabase();
		return cursor;
	}

	public static int getDATABASE_VERSION() {
		return DATABASE_VERSION;
	}

	public static void setDATABASE_VERSION(int dATABASE_VERSION) {
		DATABASE_VERSION = dATABASE_VERSION;
	}
	
	/**
	 * Parsa le info dei file  dallo storage di android
	 * @param context
	 * @return cursore
	 */
	/*private Cursor getInfoMp3(Context context){
		String field 	= MediaStore.Audio.Media.DISPLAY_NAME + " like ?";
		String[] filter = {"%_.mp3"};
		Cursor c = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] {	MediaStore.Audio.Media._ID,
								MediaStore.Audio.Media.DATA,
								MediaStore.Audio.Media.ARTIST,
								MediaStore.Audio.Media.ALBUM,
								MediaStore.Audio.Media.DISPLAY_NAME,
								MediaStore.Audio.Media.TITLE}, field, filter, null);
		c.moveToLast();
		if(c.getCount() == 0){
			Log.d(LOG, "cursor non ha elementi!");
			c.close();
			return null;
		}
		c.moveToFirst();
		return c;
	}
	*/
}
