package db;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.IBinder;
import android.util.Log;

public class ServiceFileObserver extends Service{
	private static final String OBSERVER 	= "FileObserver";
	
	private static final String DELETE		= "delete";
	private static final String CREATE		= "create";
	private static final String MODIFYFROM	= "modifyfrom";
	private static final String MODIFYTO	= "modifyto";
	
	private MyFileObserver observer;
	private Context m_context;

	
	public ServiceFileObserver(Context context){
		observer = new MyFileObserver("/storage/emulated/0/Music");
		Log.d(OBSERVER, "ServiceFileObserve creato!");
		this.m_context = context;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		Log.d(OBSERVER, "ServiceFileObserve attivo!");
		observer.startWatching();
	}
	@Override
	public void onDestroy() {
		Log.d(OBSERVER, "ServiceFileObserve disattivo!!");
		observer.stopWatching();
	}
	
	public Context getContext(){
		return this.m_context;
	}
	
	private class MyFileObserver extends FileObserver{
		private String absolutePath;

		public MyFileObserver(String path) {
			super(path);
			// TODO Auto-generated constructor stub
			this.absolutePath 	= path;
		}
		

		@Override
		public void onEvent(int event, String path) {
			// TODO Auto-generated method stub
			if(path == null)
				return;
			
			//a new file or subdirectory was created under the monitored directory
			if((FileObserver.CREATE & event) != 0){
				Log.d(OBSERVER, "un nuovo file è stato aggiunto!! ---> " + this.absolutePath + "/" + path + " created!!");
				Intent fr = new Intent("it.borove.playerborove.SERVICE");
				//Intent fr = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	        	String createdFilePath = this.absolutePath + "/" + path;
	        	Bundle bundle = new Bundle();
	        	try{
	        		bundle.putString(ServiceFileObserver.CREATE,  createdFilePath);
	        		fr.putExtras(bundle);
	        		getContext().sendBroadcast(fr);
	        	}catch(Exception e){e.printStackTrace();}
			}
	
			if((FileObserver.MOVED_FROM & event)!=0) {
				Bundle bundle = new Bundle();
				Intent fr = new Intent("it.borove.playerborove.SERVICE");
				String originalFilePath = this.absolutePath + "/" + path;
				try{
					bundle.putString(ServiceFileObserver.MODIFYFROM,  originalFilePath);
	        		fr.putExtras(bundle);
	        		getContext().sendBroadcast(fr);
					//Log.d(OBSERVER, "un file è stato modificato(ORIGINALE)!! ---> " + originalFilePath);
				}catch(Exception e){e.printStackTrace();}
				
			}
			
			if((FileObserver.MOVED_TO & event)!=0) {
				String modifiedFilePath = this.absolutePath + "/" + path;
				//Log.d(OBSERVER, "un file è stato modificato(NUOVO)!! ---> " + modifiedFilePath);
					Intent fr = new Intent("it.borove.playerborove.SERVICE");
					Bundle bundle = new Bundle();
					try{					
						bundle.putString(ServiceFileObserver.MODIFYTO,  modifiedFilePath);
		        		fr.putExtras(bundle);
			        	getContext().sendBroadcast(fr);
		        	}catch(Exception e){e.printStackTrace();}			
			}

	        //a file was deleted from the monitored directory
	        if ((FileObserver.DELETE & event)!=0) {
	        	//Log.d(OBSERVER, "un file è stato cancellato!! ---> " + this.absolutePath + "/" + path +  " is deleted");
	        	Intent fr = new Intent("it.borove.playerborove.SERVICE");
	        	String deletedFilePath = this.absolutePath + "/" + path;
	        	Bundle bundle = new Bundle();
	        	try{
	        		bundle.putString(ServiceFileObserver.DELETE,  deletedFilePath);
	        		fr.putExtras(bundle);
		        	getContext().sendBroadcast(fr);
	        	}catch(Exception e){e.printStackTrace();}
	        	
	        }
		
		}
	}

}
