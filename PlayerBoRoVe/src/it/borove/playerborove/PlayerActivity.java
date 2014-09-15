package it.borove.playerborove;



import java.io.IOException;
import java.util.Formatter;
import java.util.Locale;

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
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class PlayerActivity extends Activity{
	
	protected static final String SETTINGS = "SETTINGS";
	//private MediaPlayer mediaPlayer;
	private MusicService musicSrv;
	private boolean serviceConnected=false;
	private Intent playIntent;
	private Uri uri;
	private BroadcastReceiver receiver;
	private LocalBroadcastManager lbm;
	private ImageButton pause;
	private ImageButton stop;
	private SeekBar seekBar;
	private ImageButton loop;
	private ImageButton audio;
	private TextView endTime;
	private TextView currentTime;
	private static final int    SHOW_PROGRESS = 2;
	StringBuilder               mFormatBuilder;
	Formatter                   mFormatter;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("activity","created");
		setContentView(R.layout.player_activity);
		pause=(ImageButton)findViewById(R.id.pause);
		stop=(ImageButton)findViewById(R.id.stop);
		loop=(ImageButton)findViewById(R.id.loop);
		audio=(ImageButton)findViewById(R.id.audio);
		seekBar=(SeekBar)findViewById(R.id.mediacontroller_progress);
		endTime=(TextView)findViewById(R.id.time);
		currentTime=(TextView)findViewById(R.id.time_current);
		onClickListener();
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
		seekBar.setMax(1000);
		

		//mediaController.setEnabled(true);
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
				seekBar.setOnSeekBarChangeListener(mSeekListener);
			}
			};
			
			playIntent = new Intent(this, MusicService.class);
			bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
			Log.d("Activity","Bind");
	 
		}
	

	
	private void onClickListener() {
		// TODO Auto-generated method stub
		pause.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			if(isPlaying())
			{
				pause.setBackgroundResource(R.drawable.ic_media_play);
				pause();
			}
			else
			{
				pause.setBackgroundResource(R.drawable.ic_media_pause);
				start();
			}
				
			}
			
		});
		stop.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(musicSrv!=null){
					lbm.registerReceiver(receiver, new IntentFilter("Prepared"));
					musicSrv.seek(0);
					musicSrv.stop();
					pause.setBackgroundResource(R.drawable.ic_media_play);
					seekBar.setEnabled(false);
				}
			}
			
		});
		loop.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(musicSrv!=null){
					if (musicSrv.isLooping()){
						loop.setBackgroundResource(R.drawable.ic_media_loop2);
						musicSrv.Loop(false);
					}
					else{
						loop.setBackgroundResource(R.drawable.ic_media_loop);
						musicSrv.Loop(true);
					}
				}
				
			}
			
		});
		audio.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (musicSrv!=null){
					if(musicSrv.isMute()){
						musicSrv.setVolume(1);
						audio.setBackgroundResource(R.drawable.ic_media_audio);
					}
					else{
						musicSrv.setVolume(0f);
						audio.setBackgroundResource(R.drawable.ic_media_mute);
					}
					
				}
				
			}
			
		});
		
	}
	
private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int pos;
            switch (msg.what) {
                case SHOW_PROGRESS:
                    pos = setProgress();
                    if (isPlaying()) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                    }
                    break;
            }
        }
    };

private int setProgress() {
	
		
	Log.d("progress","progress");
	 int duration = getDuration();
	 int position = getCurrentPosition();
	 if(currentTime!=null)
		 currentTime.setText(stringForTime(position));

     if (endTime != null)
        endTime.setText(stringForTime(duration));
     
     if (duration > 0) {
         // use long to avoid overflow
         long pos = 1000L * position / duration;
         seekBar.setProgress( (int) pos);
     }
return 0;
}

private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
    public void onStartTrackingTouch(SeekBar bar) {
    ;

        // By removing these pending progress messages we make sure
        // that a) we won't update the progress while the user adjusts
        // the seekbar and b) once the user is done dragging the thumb
        // we will post one of these messages to the queue again and
        // this ensures that there will be exactly one message queued up.
        mHandler.removeMessages(SHOW_PROGRESS);
    }

    public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
        if (!fromuser) {
            // We're not interested in programmatically generated changes to
            // the progress bar's position.
            return;
        }

        long duration =getDuration();
        long newposition = (duration * progress) / 1000L;
        seekTo( (int) newposition);
        if (currentTime != null)
            currentTime.setText(stringForTime( (int) newposition));
    }

    public void onStopTrackingTouch(SeekBar bar) {
        setProgress();
 

        // Ensure that progress is properly updated in the future,
        // the call to show() does not guarantee this because it is a
        // no-op if we are already showing.
        mHandler.sendEmptyMessage(SHOW_PROGRESS);
    }
};

private String stringForTime(int timeMs) {
    int totalSeconds = timeMs / 1000;

    int seconds = totalSeconds % 60;
    int minutes = (totalSeconds / 60) % 60;
    int hours   = totalSeconds / 3600;

    mFormatBuilder.setLength(0);
    if (hours > 0) {
        return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
    } else {
        return mFormatter.format("%02d:%02d", minutes, seconds).toString();
    }
}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("activity","destroy");
		lbm.unregisterReceiver(receiver);
		unbindService(musicConnection);
		serviceConnected=false;
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
			Log.d("sevice","onServiceDisconnetcted");
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
		seekBar.setEnabled(true);
		if(musicSrv!=null){
			Log.d("controller","play");
			musicSrv.playPlayer();
			mHandler.sendEmptyMessage(SHOW_PROGRESS);
		}
	}

	public void pause() {
		if(musicSrv!=null){
		musicSrv.pausePlayer();
		Log.d("contrller","pause");
		}
	}

	 int getDuration() {
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
		//musicSrv.fadeOut(3);
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

	
	public int getBufferPercentage() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public boolean canPause() {
		// TODO Auto-generated method stub
		return true;
	}


	public boolean canSeekBackward() {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean canSeekForward() {
		// TODO Auto-generated method stub
		return true;
	}



}
