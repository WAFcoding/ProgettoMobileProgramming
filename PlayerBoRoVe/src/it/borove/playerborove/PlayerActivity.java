package it.borove.playerborove;

import android.app.Activity;
import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PlayerActivity extends Activity implements MyMediaController.MediaPlayerControl{
	
	protected static final String SETTINGS = "SETTINGS";
	private LocalBroadcastManager lbm;
	private MyMediaController mediaController;
	
	private BroadcastReceiver receiver=new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("Prepared", "player activity");
			lbm.unregisterReceiver(receiver);
			lbm.registerReceiver(receiverComplete, new IntentFilter("Complete"));
			String artist= PlayerController.getArtistCurrentPlayingTrack();
			String title= PlayerController.getTitleCurrentPlayingTrack();
			String kind= PlayerController.getKindCurrentPlayingTrack();
			setText(artist+" "+title+" "+kind);
			setImage(PlayerController.getCoverCurrentPlayingTrack());
			mediaController.show((LinearLayout)findViewById(R.id.container));
			}
		};

		private BroadcastReceiver receiverComplete=new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d("Complete", "player activity");
				mediaController.stopSeek();;
				lbm.unregisterReceiver(receiverComplete);
				lbm.registerReceiver(receiver, new IntentFilter("Prepared"));
				}
			};
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Log.d("activity","created");
		setContentView(R.layout.player_activity);
		mediaController=new MyMediaController(this);
		mediaController.setMediaPlayer(this);
		lbm= LocalBroadcastManager.getInstance(this);
		lbm.registerReceiver(receiver, new IntentFilter("Prepared"));
		//lbm.registerReceiver(receiverComplete, new IntentFilter("Complete"));
		}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("activity","destroy");
		lbm.unregisterReceiver(receiver);
		mediaController.hide();
		PlayerController.end_music_sevice();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event){
		
		if(keyCode == KeyEvent.KEYCODE_BACK){
			
			finish();
			overridePendingTransition(R.anim.top_in, R.anim.bottom_out);
			return true;
		}	
		return super.onKeyDown(keyCode, event); 
	}
	
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
		PlayerController.play();
	}
	public void pause() {
		PlayerController.pause();
	}

	public int getDuration() {
		return PlayerController.getDuration();
		}

	public int getCurrentPosition() {
		return PlayerController.getCurrentPosition();
	}

	public void seekTo(int pos) {
		PlayerController.seekTo(pos);
	}

	public int getAudioSessionId() {
		return 0;
	}

	public boolean isPlaying() {
		return PlayerController.isPlaying();
	}

	@Override
	public void setLoop() {
		PlayerController.setLoop();
	}

	@Override
	public void disableLoop() {
		PlayerController.disableLoop();
	}

	@Override
	public boolean isLooping() {
		return PlayerController.isLooping();
		}

	@Override
	public void mute() {
		PlayerController.mute();
		}

	@Override
	public void audio() {
		PlayerController.audio();
		}

	@Override
	public boolean isMute() {
		return PlayerController.isMute();
		}

	@Override
	public void stop() {
			PlayerController.stop();
	}

	@Override
	public boolean canBackForward() {
		return PlayerController.canBackForward();
	}

	@Override
	public void forward() {
		//Log.d("forward","forward");
		PlayerController.forward();
	}

	@Override
	public void back() {
		PlayerController.back();
	}
}
