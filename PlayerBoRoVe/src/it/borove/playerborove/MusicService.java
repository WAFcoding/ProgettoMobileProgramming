package it.borove.playerborove;



import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;



public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, OnCompletionListener{
	
	private static final String SETTINGS = "SETTINGS";
	private MediaPlayer player;
	private Uri uri;
	private final IBinder mBinder = new MusicBinder();
	private float volume=1f;
	private boolean stopped=false;
	
	
	public void onCreate(){
		super.onCreate();
		initMusicPlayer();
		Log.d("Service","Created");
	}

	public void initMusicPlayer(){
		player=new MediaPlayer();
		player.setWakeMode(getApplicationContext(),PowerManager.PARTIAL_WAKE_LOCK);
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		player.setOnPreparedListener(this);
		player.setOnCompletionListener(this);
		player.setOnErrorListener(this);
	}
	
	public void setPath(Uri u) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException{
		uri=u;
		player.setDataSource(getApplicationContext(), uri);
		player.prepareAsync();
	}
	
    public class MusicBinder extends Binder {
        public MusicService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MusicService.this;
        }
    }
	
	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub
	     LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
	     Intent mIntent= new Intent();
	     mIntent.setAction("Prepared");
	     setVolume(volume);
	     lbm.sendBroadcast(mIntent);
	     //playPlayer();
		//mediacontroller.show();
	}
	
	@Override
	public void onCompletion(MediaPlayer mp) {
	
		LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
	     Intent mIntent= new Intent();
	     mIntent.setAction("Complete");
	     Log.d("complete","complete");
	     lbm.sendBroadcast(mIntent);
		
	}
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    
    @Override
    public boolean onUnbind(Intent intent) {
    	player.stop();
    	player.release();
		return false;
    }
    
	public void pausePlayer(){
		Log.d("service","pause call");
		player.pause();
		Log.d("service","pause");
	}
	
	public void stop(){
		stopped=true;
		player.stop();
	}
	
	public boolean isPng(){
		return player.isPlaying();
	}
	public void playPlayer(){
		SharedPreferences prefs=getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
		int fadeIn=prefs.getInt("FadeIn",0);
		//Log.d("FADe",Integer.toString(fadeIn));
		if(!stopped){
		fadeIn(fadeIn);
		player.start();
		}
		else{
			stopped=false;
			player.prepareAsync();
		}

	}
	public int getDur() {
		return player.getDuration();
	}
	public int getPosn(){
		SharedPreferences prefs=getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
		int fadeOut=prefs.getInt("FadeOut", 0);
		if(fadeOut!=0 && player.getCurrentPosition()>=player.getDuration()-fadeOut*1000)
			fadeOut(fadeOut);
		return player.getCurrentPosition();
	}
	public void seek(int posn){
		/*si può fare meglio
		  seekTo e pause sono metodi asincroni. Con questa strategia risoviamo
		  il problema che si aveva invocando prima seekTo e immediatamente dopo, pause.
		 */
		if (player.isPlaying())
		{
			player.pause();
			player.seekTo(posn);
			player.start();
		}
		else
			player.seekTo(posn);
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		Log.d("Error","error");
		return false;
	}
	
	public void Loop(boolean b){
			player.setLooping(b);
	}
	public boolean isMute(){
		return (volume==0);
	}
	public boolean isLooping(){
		return player.isLooping();	
	}

	public void setVolume(float v){
		volume=v;
		player.setVolume(v, v);
	}
	public void fadeOut(int fadeDuration){
		Log.d("fade out", "fade out");
		//usare il logaritmo per fare fade
		if(fadeDuration>0){
			setVolume(1f);
			final double decrement=1/(double)(fadeDuration*10);
			Log.d("fade","fade");
			final Timer timer=new Timer(true);
			TimerTask task=new TimerTask(){

				@Override
				public void run() {
					
					// TODO Auto-generated method stub
					volume=(float) (volume-decrement);
					if (volume<=0){
						volume=0f;
						timer.cancel();
						timer.purge();
					}
					Log.d("volume", Double.toString(volume));
					setVolume(volume);
				}
			};
			timer.schedule(task, 100, 100);
		}
	
	}
	
	public void fadeIn(int fadeDuration){
		if(fadeDuration>0){
			setVolume(0f);
			final double increment=1/(double)(fadeDuration*10);
			Log.d("fade","fade");
			final Timer timer=new Timer(true);
			TimerTask task=new TimerTask(){

				@Override
				public void run() {
					
					// TODO Auto-generated method stub
					volume=(float) (volume+increment);
					if (volume>=1){
						volume=1;
						timer.cancel();
						timer.purge();
					}
					Log.d("volume", Double.toString(volume));
					setVolume(volume);
				}
			};
			timer.schedule(task, 100, 100);
		}
	
	}


	
}
