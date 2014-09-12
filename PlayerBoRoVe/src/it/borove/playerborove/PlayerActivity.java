package it.borove.playerborove;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.TextView;

public class PlayerActivity extends Activity implements  MediaPlayerControl{
	
	protected static final String SETTINGS = "SETTINGS";
	//private MediaPlayer mediaPlayer;
	private MediaController mediaController;
	private MusicService musicSrv;
	private boolean serviceConnected=false;
	private Intent playIntent;
	private Uri uri;
	private BroadcastReceiver receiver;
	private LocalBroadcastManager lbm;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_activity);
		mediaController = new MediaController(this){
			@Override 
			public void hide(){
			}
			@Override  
			 public void setAnchorView(View view){
				super.setAnchorView(view);
			    ImageButton stopButton = new ImageButton(getApplicationContext());
			    ImageButton loopButton = new ImageButton(getApplicationContext());
			    ImageButton muteButton = new ImageButton(getApplicationContext());
			    
			     //stopButton.setImageResource(resId);
			     FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			     params.gravity = Gravity.RIGHT;
			     //addView(searchButton, params);
				
			}
		
			public boolean dispatchKeyEvent(KeyEvent event)
	        {
	            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
	            { 	 super.hide();
	                ((Activity) getContext()).finish();}

	            return super.dispatchKeyEvent(event);
	        }
		};	
		mediaController.setMediaPlayer(this);
		mediaController.setAnchorView(findViewById(R.id.container));
		//mediaController.setEnabled(true);
		lbm= LocalBroadcastManager.getInstance(this);
		Log.d("Activity","Created");
		receiver=new BroadcastReceiver(){

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				
				String artist= getIntent().getExtras().getString("singer");
				String title= getIntent().getExtras().getString("title");
				String kind= getIntent().getExtras().getString("kind");
				
				setText(artist+" "+title+" "+kind);
				//image
				mediaController.show();
				SharedPreferences prefs=getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
				int pos=prefs.getInt("Pos", 0);
				seekTo(pos*1000);
				start();
				lbm.unregisterReceiver(receiver);
				setImage((Bitmap)getIntent().getParcelableExtra("image"));
			}
			};
			
			playIntent = new Intent(this, MusicService.class);
			bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
			Log.d("Activity","Bind");
	 
		}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(musicConnection);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	
	
	private ServiceConnection musicConnection = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			it.borove.playerborove.MusicService.MusicBinder binder = (it.borove.playerborove.MusicService.MusicBinder)service;
			//get service
			musicSrv = binder.getService();
			serviceConnected=true;
			try {
				
				lbm.registerReceiver(receiver, new IntentFilter("Prepared"));
				uri= Uri.parse(getIntent().getExtras().getString("uri"));
				Log.d("uri", uri.toString());
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
		}
	};
	
	public void setText(String s){
		TextView text=(TextView) findViewById(R.id.textView);
		text.setText(s);
		text.setSelected(true);
	
	}
	public void setImage(Bitmap p){
		ImageView image=(ImageView) findViewById(R.id.imageView1);
		if (p==null)
			image.setImageResource(R.drawable.icon);
		else
		image.setImageBitmap(p);
	}

	@Override
	public void start() {
		Log.d("controller","play");
		musicSrv.playPlayer();
	}

	@Override
	public void pause() {
		musicSrv.pausePlayer();
		Log.d("contrller","pause");
	}

	@Override
	public int getDuration() {
		if(musicSrv!=null && serviceConnected)
			return musicSrv.getDur();
		else return 0;
		
	}

	@Override
	public int getCurrentPosition() {
		if(musicSrv!=null && serviceConnected)
			return musicSrv.getPosn();
		else return 0;
		
	}

	@Override
	public void seekTo(int pos) {
		musicSrv.seek(pos);
		//musicSrv.fadeOut(3);
	}

	public int getAudioSessionId() {
		return 0;
	}
	@Override
	public boolean isPlaying() {
		Log.d("dfdsfd", String.valueOf(musicSrv.isPng()));
		// TODO Auto-generated method stub
		if (musicSrv!=null && serviceConnected)
		return musicSrv.isPng();
		return false;
	}

	@Override
	public int getBufferPercentage() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean canPause() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean canSeekForward() {
		// TODO Auto-generated method stub
		return true;
	}



}
