/**
 * Questa classe implementa la connessione con il db e la sua gestione
 * 
 * @author "Pasquale Verlotta - pasquale.verlotta@gmail.com"
 */
package db;

import java.io.File;
import java.nio.channels.GatheringByteChannel;
import java.util.concurrent.SynchronousQueue;

import utility.Utils;
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
	String COLUMN_ALBUM_NAME		= "albumName";
	public static final
	String COLUMN_DURATION			= "duration";
	
	
	public static final
	String TABLE_NAME_CONTAINS		= "contains";
	public static final
	String COLUMN_ID_BID			= "bid";
	public static final
	String COLUMN_ID_PID			= "pid";
		
	private SQLiteDatabase m_db;
	
	private Context m_context;
	
	private static SQLiteConnect mIstance = null;
	
	//private MyFileObserver fileOb;
	
	
	private SQLiteConnect(Context context, final String path, final String dbName, final int version){
		super(context, dbName, null, version);
		m_context			= context;
		db_path 			= path;
		setDATABASE_VERSION(version);
		DB_NAME 			= dbName;

	}
	
	public static SQLiteConnect getInstance(Context context, final String path, final String dbName, final int version){
		if(mIstance == null){
			mIstance = new SQLiteConnect(context.getApplicationContext(), path, dbName, version);
		}
		
		
		return mIstance;
	}
	
	/**
	 * Se non esiste crea il nuovo database(solo tabelle e senza entry)
	 * @return true se il database gi� esisteva, false se � stato appena creato
	 */
 	public boolean createDatabase(){
		boolean dbExist = checkDatabase();
		if(dbExist){
			// non fare nulla, il db esiste gi�
			Log.d(LOG, "database esistente");
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
					COLUMN_PATH_TRACK + " text not null," + 
					COLUMN_ALBUM_NAME + " text not null," +
					COLUMN_DURATION + " text not null" +");";
			
			String newTablePlaylistQueryString =	"create table " + 
					TABLE_NAME_PLAYLIST + 	" (" + 
					COLUMN_ID +  " integer primary " + "key autoincrement not null," + 
					COLUMN_NAME + " text not null" + ");";
			
			String newTableContainsQueryString =	"create table " + 
					TABLE_NAME_CONTAINS + 	" (" + 
					COLUMN_ID_BID +  " text not null," + 
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
	public static String getPathDb(){
		return db_path + DB_NAME;
	}
	/**
	 * Controlla se ail dabase esiste gi� per evitare ricopiature del fileogni volta che riapri l'app
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
		if(m_db != null)
			return this.m_db;
		else
			return null;
	}
	
	/**
	 * Aggiunge una nuova playlist
	 * @param namePlaylist		nome della playlist da aggiungere
	 * @return
	 */
	public void addRowPlaylist(String namePlaylist){
		ContentValues content = new ContentValues();
		content.put(COLUMN_NAME, namePlaylist);
		try{
			 openDatabaseRW();
			 m_db.execSQL("PRAGMA foreign_keys = ON");
			 m_db.insert(TABLE_NAME_PLAYLIST, null, content);
			 closeDatabase();
		}catch(Exception e){
			Log.d(LOG, "Errore nella addRowPlaylist!! " + e.getMessage());
		}
		//return getExactlyNamePlaylist(namePlaylist);
		
	}
	/**
	 * Aggiunge un nuovo brano
	 * @param title				nome del file con estensione(.mp3 ecc..)
	 * @param kind				genere del brano
	 * @param nameSinger		nome dell'artista
	 * @param vote				voto di preferenza assegnato
	 * @param contentTitle		il titolo del brano(senza estensione)
	 * @param albumId			id dell'album(copertina dell'album rappresentato come long int)
	 * @param albumName			Il nome dell'album
	 * @param duration			Durata del brano(espresso in msec)
	 * @return il brano appena aggiunto
	 */
	public void addRowTrack(String title, String kind, String nameSinger, String vote, String contentTitle, String albumId, String path, 
								String albumName, String duration){
		/*
		 * questo parser elimina eventuali apici e slash presenti nel nome del file (String title)
		 */
		String[] tags = {title, nameSinger, contentTitle, albumName};
		int k=0;
		while(k < tags.length){
			while(tags[k].contains("'") || tags[k].contains("\\")){
				String leftTemp="";
				String rigthTemp="";
				for(int i=0; i< tags[k].length(); i++){
					if(tags[k].substring(i, i+1).equals("'") || tags[k].substring(i, i+1).equals("\\")){
						leftTemp = tags[k].substring(0,i);
						rigthTemp = tags[k].substring(i+1, tags[k].length());
						//Log.d(LOG, "leftTemp: " + leftTemp + " rigthTemp: " + rigthTemp);
						tags[k] = leftTemp +""+ rigthTemp;
						//Log.d(LOG, "title: " + title);
					}					
				}
			}
			k++;
		}

		ContentValues content = new ContentValues();
		content.put(COLUMN_TITLE, title);
		content.put(COLUMN_KIND, kind);
		content.put(COLUMN_SINGER_NAME, nameSinger);
		content.put(COLUMN_VOTE, vote);
		content.put(COLUMN_CONTENT_TITLE, contentTitle);
		content.put(COLUMN_ALBUM_ID, albumId);
		content.put(COLUMN_PATH_TRACK, path);
		content.put(COLUMN_ALBUM_NAME, albumName);
		content.put(COLUMN_DURATION, duration);
		
		try{
			 openDatabaseRW();
			 m_db.execSQL("PRAGMA foreign_keys = ON");
			 m_db.insert(TABLE_NAME_TRACK, null, content);	 
			 closeDatabase();
		}catch(Exception e){
			Log.d(LOG, "Errore nella addRowTrack!! " + e.getMessage());
		}
		
		//return getExactlyTrack(title,"title");
	}
	/**
	 * Aggiunge una nuova entry nella tabella contains indicando l'associazione tra brano e playlist
	 * @param idPlaylist	id della plalist
	 * @param idTrack		id del brano appartenente alla playlist
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
	
	public boolean editPlaylistName(String idPlaylist, String new_name){
		if(idPlaylist.equals(null) || new_name.equals(null)){
			return false;
		}
		int toReturn=0;
		ContentValues values = new ContentValues();
		values.put(COLUMN_ID, idPlaylist);
		values.put(COLUMN_NAME, new_name);
		String whereClause= COLUMN_ID + "='" + idPlaylist + "'";
		try{
			openDatabaseRW();
			toReturn= m_db.update(TABLE_NAME_PLAYLIST, values, whereClause, null);
			closeDatabase();
		}
		catch (Exception e){
			Log.d(LOG, "Errore in editPlaylistName!! " + e.getMessage());
		}
		
		if(toReturn > 0)
			return true;
		
		return false;
		
	}

	/**
	 * Cancella un brano singolo
	 * @param value			il valore di ricerca del brano 
	 * @param columnType	il tipo di colonna cui value f� parte
	 */
	public void deleteRowTrack(String value, String columnType){
		if(value == null || columnType == null)
			return;
		
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
			else if(columnType.equals(COLUMN_ALBUM_NAME))
				m_db.delete(TABLE_NAME_TRACK, COLUMN_ALBUM_NAME + "=" + "'" + value + "'", null);
			else if(columnType.equals(COLUMN_DURATION))
				m_db.delete(TABLE_NAME_TRACK, COLUMN_DURATION + "=" + "'" + value + "'", null);
			else if(columnType.equals(COLUMN_ID))
				m_db.delete(TABLE_NAME_TRACK, COLUMN_ID + "=" + "'" + value + "'", null);
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
		if(name == null)
			return;
		
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
	
	/**
	 * Elimina un brano di una playlist (entry tabella)
	 * 
	 * @param idPlaylist		id della playlist
	 * @param idTrack			id del brano appartenente alla playlist
	 */
	
	public void deleteRowContains(String idPlaylist, String idTrack){
		if(idPlaylist == null || idTrack == null)
			return;
		
		String condition = COLUMN_ID_PID + " = " + "'" + idPlaylist +"'" + " AND " + COLUMN_ID_BID + " = " + "'" + idTrack + "'";
		try{
			openDatabaseRW();
			m_db.execSQL("PRAGMA foreign_keys = ON");
			m_db.delete(TABLE_NAME_CONTAINS, condition, null);
			closeDatabase();
		}catch(SQLiteException e ){
			e.printStackTrace();
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
						String temp= Utils.replaceBadSymbols(c.getString(5));
						if(valueContentTitle.equals(temp)){
							Log.d(LOG, "valueContentTitle: " + valueContentTitle);
							//Log.d(LOG, "c.getString(5): " + c.getString(5));
							trackFound = true;
							break;			
						}				
						c.moveToNext();
					}
					if(!trackFound){
						Log.d(LOG, "brano cancellato: " + tableTrack.getString(0) + ": " + tableTrack.getString(1));
						//Log.d(LOG, "brano cancellato: " + tableTrack.getString(5));
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
		c.moveToFirst();
		if(c != null){
			while(!c.isAfterLast()){
				String vote			= "0";
				String kind			= "unknown";
				String pathTrack	= c.getString(1);
				String contentTitle	= c.getString(5);
				String title 		= c.getString(4);
				String singerName	= c.getString(2);
				String albumId		= c.getString(6);
				String albumName	= c.getString(3);
				String duration		= c.getString(7);
				
				//controllo che non vi siano caratteri strani
				title= Utils.replaceBadSymbols(title);
				contentTitle= Utils.replaceBadSymbols(contentTitle);
				
				/*Log.d("SQLiteConnect", "=======================file: " + pathTrack + " " + singerName + " " 
						+ title + " " + contentTitle + " "+ albumId + " "
						+ kind + " " + vote + "============================");*/
				
				//Log.d(LOG, "SQLite Connect - duration: " + duration);
				openDatabaseRW();
				Cursor trackOnDb = getExactlyTrack(contentTitle, COLUMN_CONTENT_TITLE);
				closeDatabase();
				if(trackOnDb == null){				
					//Cursor e = 
					addRowTrack(title, kind, singerName, vote, contentTitle, albumId, pathTrack, albumName, duration);
					Log.d(LOG, "nuovo brano rilevato e aggiunto al db: " + title);
					/*e.moveToFirst();
					while(!e.isAfterLast()){
						for(int i=0; i < e.getColumnCount(); i++)
							Log.d(LOG, e.getColumnName(i) + ": " + e.getString(i));
						e.moveToNext();
					}
					*/
				}
				else{
					if(!trackOnDb.getString(1).equals(title)){
						updateRowTrack(trackOnDb.getString(0), title, COLUMN_TITLE);
						Log.d(LOG, "aggiornato il titolo del brano: " + trackOnDb.getString(1));
					}	
					if(!trackOnDb.getString(2).equals(singerName)){
						updateRowTrack(trackOnDb.getString(0), singerName, COLUMN_SINGER_NAME);
						Log.d(LOG, "aggiornato il nome artista: " + trackOnDb.getString(2));			
					}
					if(!trackOnDb.getString(8).equals(albumName)){
						updateRowTrack(trackOnDb.getString(0), albumName, COLUMN_ALBUM_NAME);
						Log.d(LOG, "aggiornato il nome dell'album: " + trackOnDb.getString(8));			
					}	
				}
				
			
				c.moveToNext();
			}
			}
			else
				Log.d(LOG, "cursor c � null!!");
	
		//return result;
	}

	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	/**
	 * Aggiorna il brano singolo
	 * @param idTrack		id del brano da aggiornare
	 * @param newValue		il nuovo valore da aggiornare
	 * @param columnType 	la colonna cui newValue appartiene
	 */
	public void updateRowTrack(String idTrack, String newValue, String columnType){
		
		ContentValues contentValues = new ContentValues();
		if(columnType.equals(COLUMN_SINGER_NAME) || columnType.equals(COLUMN_KIND) || columnType.equals(COLUMN_TITLE) || columnType.equals(COLUMN_VOTE)
				|| columnType.equals(COLUMN_ALBUM_NAME))	
			/*
			 * questo parser elimina eventuali apici e slash presenti in newValue
			 */
			while(newValue.contains("'") || newValue.contains("\\")){
				String leftTemp="";
				String rigthTemp="";
				for(int i=0; i< newValue.length(); i++){
					if(newValue.substring(i, i+1).equals("'") || newValue.substring(i, i+1).equals("\\")){
						leftTemp = newValue.substring(0,i);
						rigthTemp = newValue.substring(i+1, newValue.length());
						//Log.d(LOG, "leftTemp: " + leftTemp + " rigthTemp: " + rigthTemp);
						newValue = leftTemp +""+ rigthTemp;
						//Log.d(LOG, "title: " + title);
					}					
				}
			}

			contentValues.put(columnType, newValue);
		
		String where = COLUMN_ID + "='" + idTrack + "'";
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
		//String[] columnSelect = {COLUMN_NAME};
		//String [] filter = {name};
		Cursor cursor = null;
		if(!name.equals(null)){
			String field = COLUMN_NAME + "='" + name + "'";
			cursor = m_db.query(SQLiteConnect.TABLE_NAME_PLAYLIST, null, field, null,null,null,null);
		}
		//Cursor cursor = m_db.query(SQLiteConnect.TABLE_NAME_PLAYLIST, columnSelect, field, filter, null, null, null);
		cursor.moveToLast();
		if(cursor.getCount() == 0){
			cursor.close();
			closeDatabase();
			return null;
		}
		cursor.moveToFirst();		
		//closeDatabase();
		return cursor;
	}
	
	/**
	 * Si ricerca una singola traccia
	 * @param value			il valore(titolo, voto...) richiesto per la query
	 * @param columnType	la colonna contenente l'oggetto 'value'
	 * @return
	 */
	public Cursor getExactlyTrack(String value, String columnType){
		//openDatabaseReadOnly();
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
		
		//closeDatabase();
		Log.d(LOG, "Track trovata: "+ cursor.getString(0) +" " +cursor.getString(1) +" "+cursor.getString(2) +" "+cursor.getString(3) +
				 " " + cursor.getString(4) +" " +cursor.getString(5) + " " + cursor.getLong(6));
		return cursor;
	}
	
	public Cursor getFilteredTrack(String value, final String columnType, String[] columnsSelect){
		openDatabaseReadOnly();
		Cursor cursor = null;
		if(!columnsSelect.equals("*")){
			String field = columnType + " like ?";
			String [] filter = {value + "%_"};
			cursor = m_db.query(SQLiteConnect.TABLE_NAME_TRACK, columnsSelect, field, filter, null, null, null);
		}
		else{
			String field = columnType + " like ?";
			String [] filter = {value + "%_"};
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
	 * @param value				il valore inserito dall'utente. Se � vuota (""), restituisce tutte le entry presenti nella tabella
	 * @param columnType		la colonna cui value fa parte
	 * @param columnsSelect		le colonne restituite dalla query(SELECT). Se � uguale a "*" restituisce tutte le colonne
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
		if(this.m_db == null)
			return null;
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
				Log.d(LOG, "dentro queryResult");
				try{
					String field 		= attr + " = '" + where + "'";
					String [] filter 	= null;
					query		= "SELECT "+ "c."+select + " FROM " + table_src + " AS a, " + table_dst + " AS c"
							+ " WHERE " + "a." + COLUMN_ID + "=c." + COLUMN_ID_PID + " AND " + "a." + field;
					cursor = m_db.rawQuery(query, filter);
					/*if(cursor == null)
						Log.e(LOG, "CURSOR NULL");
					else{
						cursor.moveToFirst();
						Log.e(LOG, "********* cursor: " + cursor.getCount());
						
					}
					*/
				}catch(SQLiteException e ){e.printStackTrace();}
			}
			else{
				try{
					String field 		= attr + " like ?";
					String [] filter 	= {where + "%" };
					query		= "SELECT "+ "a." + COLUMN_ID +" AS id_P, a."+ COLUMN_NAME + ",b."+select + " FROM " + table_src + " AS a, " 
							+ table_dst + " AS b, " + TABLE_NAME_CONTAINS + " AS c"
							+ " WHERE " + "a." + COLUMN_ID + "=c." + COLUMN_ID_PID + " AND " + "b." + COLUMN_ID + "=c." + COLUMN_ID_BID 
							+ " AND " + "a." + field;
					cursor = m_db.rawQuery(query, filter);
					if(cursor == null)
						Log.e(LOG, "CURSOR NULL");
					else{
						cursor.moveToFirst();
						Log.e(LOG, "********* cursor: " + cursor.getCount());
						
					}
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
		//closeDatabase();
		return cursor;
	}

	public static int getDATABASE_VERSION() {
		return DATABASE_VERSION;
	}

	public static void setDATABASE_VERSION(int dATABASE_VERSION) {
		DATABASE_VERSION = dATABASE_VERSION;
	}
	
	
}
