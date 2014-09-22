package it.borove.playerborove;

import java.io.IOException;
import android.app.Activity;
import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PlayerActivity extends Activity implements MyMediaController.MediaPlayerControl{
	
	protected static final String SETTINGS = "SETTINGS";
	private MusicService musicSrv;
	private boolean serviceConnected=false;
	private Intent playIntent;
	private Uri uri;
	private BroadcastReceiver receiver;
	private LocalBroadcastManager lbm;
	private MyMediaController mediaController;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		
		super.onCreate(savedInstanceState);
		Log.d("activity","created");
		setContentView(R.layout.player_activity);
		mediaController=new MyMediaController(this);
		mediaController.setMediaPlayer(this);
		lbm= LocalBroadcastManager.getInstance(this);
		Log.d("Activity","Created");
		receiver=new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				lbm.unregisterReceiver(receiver);
				String artist= getIntent().getExtras().getString("singer");
				String title= getIntent().getExtras().getString("title");
				String kind= getIntent().getExtras().getString("kind");
				setText(artist+" "+title+" "+kind);
				//image
				SharedPreferences prefs=getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
				int pos=prefs.getInt("Pos", 0);
				Log.d("activity","before seek");
				seekTo(pos*1000);
				start();
				setImage((Bitmap)getIntent().getParcelableExtra("image"));
				mediaController.show((LinearLayout)findViewById(R.id.container));
				}
			};
			playIntent = new Intent(this, MusicService.class);
			bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
			
		}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("activity","destroy");
		lbm.unregisterReceiver(receiver);
		mediaController.hide();
		unbindService(musicConnection);
		serviceConnected=false;
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
			Log.d("Disc","onServiceDisconnetcted");
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
	public void start() {
		if(musicSrv!=null){
			Log.d("controller","play");
			musicSrv.playPlayer();
			}
	}
	public void pause() {
		if(musicSrv!=null){
			musicSrv.pausePlayer();
			Log.d("contrller","pause");
		}
	}

	public int getDuration() {
		if(musicSrv!=null && serviceConnected)
			return musicSrv.getDur();
		else return 0;
		}

	public int getCurrentPosition() {
		if(musicSrv!=null && serviceConnected)
			return musicSrv.getPosn();
		else return 0;
	}

	public void seekTo(int pos) {
		musicSrv.seek(pos);
	}

	public int getAudioSessionId() {
		return 0;
	}

	public boolean isPlaying() {
		// TODO Auto-generated method stub
		if (musicSrv!=null && serviceConnected)
		return musicSrv.isPng();
		return false;
	}

	@Override
	public void setLoop() {
		// TODO Auto-generated method stub
		if(musicSrv!=null)
			musicSrv.Loop(true);
	}

	@Override
	public void disableLoop() {
		// TODO Auto-generated method stub
		if (musicSrv.isLooping())
			musicSrv.Loop(false);		
	}

	@Override
	public boolean isLooping() {
		// TODO Auto-generated method stub
		return musicSrv.isLooping();
		}

	@Override
	public void mute() {
		// TODO Auto-generated method stub
		if (musicSrv!=null)
				musicSrv.setVolume(0f);
		}

	@Override
	public void audio() {
		// TODO Auto-generated method stub
			musicSrv.setVolume(1f);
			}

	@Override
	public boolean isMute() {
		// TODO Auto-generated method stub
		return musicSrv.isMute();
		}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		if(musicSrv!=null){
			lbm.registerReceiver(receiver, new IntentFilter("Prepared"));
			musicSrv.seek(0);
			musicSrv.stop();
		}
	}



	




}
