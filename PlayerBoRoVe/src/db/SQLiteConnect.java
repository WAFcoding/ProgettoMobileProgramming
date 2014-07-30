/**
 * Questa classe implementa la connessione con il db e la sua gestione
 * 
 * @author "Pasquale Verlotta - pasquale.verlotta@gmail.com"
 */
package db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteConnect{
	
	private final static String DB_NAME= "BoRoVe_db.db";
	private final static int DB_VERSION= 1;
	
	private final static String LOG= "SQLiteConnect";
	
	private SQLiteDatabase m_db;
	private Context m_context;
	private DBhelper m_db_helper;
	
	public SQLiteConnect(Context context){
		
		m_context= context;
		m_db_helper= new DBhelper(m_context);
	}
	
	public void open(){
		m_db= m_db_helper.getWritableDatabase();
	}
	
	public void close(){
		if(m_db != null){
			m_db.close();
		}
		else{
			Log.d(LOG, "db null.");
		}
	}
	
	public Cursor execQuery(String query){
		
		//TODO aggiungere controllo sul db nullo
		if(m_db.isOpen()){
			return m_db.rawQuery(query, null);
		}
		
		return null;
	}
	
	//TODO implementare funzione per il fetching dei risultati della query,
	//magari passando un array o una lista
	private class DBhelper extends SQLiteOpenHelper{


		public DBhelper(Context context){
			super(context, DB_NAME, null, DB_VERSION);
		}
		
		/**
		 * @param db SQLiteDatabase
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			
		}
	
		/**
		 * @param db SQLiteDatabase
		 * @param oldVersion int
		 * @param newVersion int
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
		}
		
	}
}
