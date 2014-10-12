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
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, 
													OnCompletionListener, AudioManager.OnAudioFocusChangeListener{
	
	private static final String SETTINGS = "SETTINGS";
	private MediaPlayer player;
	private Uri uri;
	private  IBinder mBinder;
	private float volume=1f;
	private boolean stopped=false;
	private boolean fadeOutStarted=false;
	private boolean isLooping=false;
	
	private static final int    FADE_IN = 1;
	private static final int    FADE_OUT = 2;
	private double increment;
	private double decrement;
	private TimerTask task;
	private Timer timerFade;
	//private MyTask task;
	//================AUDIO FOCUS=====================
	private static AudioManager audiomanager;
	private static int focusResult;
	
	public void onCreate(){
		super.onCreate();
		initMusicPlayer();
	
			
		Log.d("Service","Created service");
	}
	
	public void onDestroy(){
		Log.d("Service","Destroyed service");
		LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
		Intent mIntent= new Intent();
		mIntent.setAction("Service destroy");
		lbm.sendBroadcast(mIntent);
		super.onDestroy();
	}
	


	public void initMusicPlayer(){
		player=new MediaPlayer();
		player.setWakeMode(getApplicationContext(),PowerManager.PARTIAL_WAKE_LOCK);
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		player.setOnPreparedListener(this);
		player.setOnCompletionListener(this);
		player.setOnErrorListener(this);
		mBinder = new MusicBinder();
		audiomanager= (AudioManager) PlayerController.getContext().getSystemService(Context.AUDIO_SERVICE);
	}
	
	public void setPath(Uri u) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException{
		uri=u;
		player.setDataSource(getApplicationContext(), uri);
		//Log.d("service", "chiamata prepareAsync");
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
		
		if(getFocusResult() != AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
			PlayerController.printToast("Impossibile riprodurre, focus non concesso");
			Log.d("audio focus", "focus negato");
		}
		else{
			LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
			Intent mIntent= new Intent();
			mIntent.setAction("Prepared");
			setVolume(volume);
			lbm.sendBroadcast(mIntent);
			//playPlayer();
			//mediacontroller.show();
			Log.d("audio focus", "focus concesso");
		}
	}
	
	@Override
	public void onCompletion(MediaPlayer mp) {
	
		stopFade();
		
		if(isLooping())
		{
			Log.d("loop","true");
		//	fadeIn(3);
			playPlayer();
		}
		else{
			Log.d("loop","false");
		
		
			LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
			Intent mIntent= new Intent();
			mIntent.setAction("Complete");
			//stopFade();
			Log.d("complete","complete");
			lbm.sendBroadcast(mIntent);
			
			//mIntent.setAction("Complete Preview");
			//lbm.sendBroadcast(mIntent);
		}
			
	}
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    
    @Override
    public boolean onUnbind(Intent intent) {
    	try{
        	player.stop();
        	player.reset();
        	player.release();
        	Log.d("onUnbind di service","unbind musicservice");
    	}catch(IllegalStateException e){
        	Log.d("onUnbind di service eccezione",e.toString());
    	}
		return false;
    }
    
	public void pausePlayer(){
		Log.d("service","pause call");
		stopFade();
		player.pause();
	}
	
	public void stop(){
		stopped=true;
		stopFade();
		player.stop();
	}
	
	public boolean isPng(){
		return player.isPlaying();
	}
	public void playPlayer(){
		Log.d("playplayer","service");
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
	public void preview(int durationPreview){
		//Log.d("start","Preview");
		/*if(!stopped)
			
			player.start();
		else{
			stopped=false;
			player.prepareAsync();
		}*/
		player.start();
		timerFade=new Timer();
		final LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
		task=new TimerTask(){

			@Override
			public void run() {
				try{
					Intent mIntent= new Intent();
					mIntent.setAction("Complete Preview");
					lbm.sendBroadcast(mIntent);
					Log.d("run di timer","inviato intent complete preview");
					player.pause();
					//player.stop();
					timerFade.purge();
					timerFade.cancel();
					this.cancel();
				} catch(IllegalStateException e) {
					Log.d("exception",e.toString());
				}
			}
			
		};
		//timerFade.schedule(task, Math.min(durationPreview,player.getDuration()));
		if(getDur() >= durationPreview)
			timerFade.schedule(task, durationPreview);
	}
	public int getDur() {
		return player.getDuration();
	}
	public int getPosn(){
		SharedPreferences prefs=getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
		int fadeOut=prefs.getInt("FadeOut", 0);
		if(!fadeOutStarted &&fadeOut!=0 && player.getCurrentPosition()>=player.getDuration()-fadeOut*1000)
			fadeOut(fadeOut);
	
		return player.getCurrentPosition();
	}
	public void seek(int posn){
		/*si pu� fare meglio
		  seekTo e pause sono metodi asincroni. Con questa strategia risoviamo
		  il problema che si aveva invocando prima seekTo e immediatamente dopo, pause.
		 */
		if (player.isPlaying())
		{
			stopFade();
			player.pause();
			player.seekTo(posn);
			player.start();
		}
		else
			player.seekTo(posn);
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		Log.d("onError","error");
		player.stop();
		player.reset();
		player.release();
		return true;//se falso viene chiamato automaticamente onCompletion
	}
	
	public void Loop(boolean b){
			//player.setLooping(b);
		isLooping=b;
	}
	public boolean isMute(){
		return (volume==0);
	}
	public boolean isLooping(){
		return isLooping;
		//return player.isLooping();	
	}

	public void setVolume(float v){
		volume=v;
		player.setVolume(v, v);
	}
	
	private class MyTaskFadeIn extends TimerTask{


		@Override
		public void run() {
			Log.d("fadein","step");
			volume=(float) (volume+increment);
			if (volume>=1){
				volume=1;
				stopFade();
				return;//FIXME 
			}
			//Log.d("volume", Double.toString(volume));
			setVolume(volume);

		}
	};

	private class MyTaskFadeOut extends TimerTask{
		@Override
		public void run() {
			volume=(float) (volume-decrement);
			if (volume<=0){
				volume=0f;
				stopFade();

				return;
			}
			//Log.d("volume", Double.toString(volume));
			setVolume(volume);

		}
	};
		
		
	
	public void fadeIn(int fadeDuration){
		Log.d("fadein","started");
		fadeOutStarted=false;
		if(fadeDuration>0){
			setVolume(0f);
			increment=1/(double)(fadeDuration*10);
			//if(timerFade==null)
			String s=String.valueOf(System.nanoTime());
			timerFade=new Timer("task_"+s);
			Log.d("create timer",s);
			
			//if(task==null){
			MyTaskFadeIn task=new MyTaskFadeIn();
			timerFade.schedule(task, 100,100);

		}
		else setVolume(1);
	
	}
	public void fadeOut(int fadeDuration){
		
		fadeOutStarted=true;
		//usare il logaritmo per fare fade
		if(fadeDuration>0){
			decrement=1/(double)(Math.min(fadeDuration*10,(player.getDuration()-player.getCurrentPosition())/100));
			Log.d("create timer","fadeout");
			if(timerFade!=null)
			{
				Log.d("fade out","timerfade!=null");
				stopFade();
				
			
			}
			timerFade=new Timer("task_fadeout");
			MyTaskFadeOut task=new MyTaskFadeOut();
			timerFade.schedule(task, 100,100);
			//mHandler.sendEmptyMessage(FADE_OUT);

		}
	
	}
	
	public void stopFade(){
		//task.cancel();
		//Log.d("task cancelled",task.toString());
		if(timerFade!=null){
			timerFade.purge();
			timerFade.cancel();
			Log.d("timer cancelled",timerFade.toString());
			timerFade=null;
			if(task!=null)
				task.cancel();
		}		
	}

	
	public int getFocusResult(){
		return audiomanager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
	}

	@Override
	public void onAudioFocusChange(int focusChange) {
		try{
			switch (focusChange){

			case AudioManager.AUDIOFOCUS_GAIN:
				//il focus è ritornato all'applicazione
				if(player == null) 
					initMusicPlayer();
				else if(!player.isPlaying())
					player.start();
				setVolume(1.0f);
				break;
			case AudioManager.AUDIOFOCUS_LOSS:
				//il focus è stato perso per parecchio tempo, liberare memoria e rilasciare risorse

				if(player.isPlaying()) 
					player.stop();
				player.reset();
				player.release();
				player= null;
				break;
			case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
				//il focus è stato perso per un breve istante di tempo, si deve mettere almeno in pausa
				if(player.isPlaying()) 
					player.pause();
				break;
			case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
				//focus perso per poco tempo, si può continuare a riprodurre ma a basso volume
				if(player.isPlaying())
					setVolume(0.1f);
				break;

			}
		}
		catch(IllegalStateException e){
			Log.d("onAudioFocusChange exception",e.toString());
		}
	}
}
