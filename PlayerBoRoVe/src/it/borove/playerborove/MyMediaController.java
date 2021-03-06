package it.borove.playerborove;

import java.util.Formatter;
import java.util.Locale;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MyMediaController extends FrameLayout{
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
	private MediaPlayerControl mPlayer;	
	private View mRoot;
	private ViewGroup anchorView=null;
	
	public MyMediaController(Context context){
		super(context);
	}
	
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

	private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            int pos;
            switch (msg.what) {
                case SHOW_PROGRESS:
                    pos = setProgress();
                    if (mPlayer.isPlaying()) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                    }
                    break;
            }
        }
    };
   
    private int setProgress() {
    	Log.d("progress","progress");
    	int duration = mPlayer.getDuration();
    	int position = mPlayer.getCurrentPosition();
    	if(currentTime!=null)
    		currentTime.setText(stringForTime(position));
    	if (endTime != null)
    		endTime.setText(stringForTime(duration));
    	if (duration > 0) {
    		long pos = 1000L * position / duration;
    		seekBar.setProgress( (int) pos);
    	}
    	
    	if (mPlayer.isPlaying())
    		pause.setBackgroundResource(R.drawable.ic_media_pause);
    	else
    		pause.setBackgroundResource(R.drawable.ic_media_play);
    	if(mPlayer.isMute())
    		audio.setBackgroundResource(R.drawable.ic_media_mute);
    	else
    		audio.setBackgroundResource(R.drawable.ic_media_audio);
    	if(mPlayer.isLooping())
    		loop.setBackgroundResource(R.drawable.ic_media_loop);
    	else
    		loop.setBackgroundResource(R.drawable.ic_media_loop2);
    	return 0;
    }

    private void onClickListener() {
    	seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
    		public void onStartTrackingTouch(SeekBar bar) {
    	        mHandler.removeMessages(SHOW_PROGRESS);
    	    }

    	    public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
    	        if (!fromuser) {
    	            return;
    	        }
    	        long duration =mPlayer.getDuration();
    	        long newposition = (duration * progress) / 1000L;
    	        mPlayer.seekTo( (int) newposition);
    	        if (currentTime != null)
    	            currentTime.setText(stringForTime( (int) newposition));
    	    }

    	    public void onStopTrackingTouch(SeekBar bar) {
    	        setProgress();
    	        mHandler.sendEmptyMessage(SHOW_PROGRESS);
    	    }
    	});
    	
	// TODO Auto-generated method stub
    	pause.setOnClickListener(new OnClickListener(){
    		@Override
    		public void onClick(View v) {
    			// TODO Auto-generated method stub
    			if(mPlayer.isPlaying()){
    				pause.setBackgroundResource(R.drawable.ic_media_play);
    				mPlayer.pause();
    			}
    			else
    			{
    				pause.setBackgroundResource(R.drawable.ic_media_pause);
    				mPlayer.start();
    				seekBar.setEnabled(true);
    				mHandler.sendEmptyMessage(SHOW_PROGRESS);
    			}
			}
		});
    	stop.setOnClickListener(new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mPlayer.stop();
			pause.setBackgroundResource(R.drawable.ic_media_play);
			seekBar.setEnabled(false);
			}
		});
    	loop.setOnClickListener(new OnClickListener(){
    		@Override
    		public void onClick(View v) {
    			if (mPlayer.isLooping()){
    				loop.setBackgroundResource(R.drawable.ic_media_loop2);
					mPlayer.disableLoop();
				}
				else
				{
					loop.setBackgroundResource(R.drawable.ic_media_loop);
					mPlayer.setLoop();
				}	
    			}
    		});
    	audio.setOnClickListener(new OnClickListener(){
    		@Override
    		public void onClick(View v) {
    			// TODO Auto-generated method stub
    			if(mPlayer.isMute()){
					mPlayer.audio();
					audio.setBackgroundResource(R.drawable.ic_media_audio);
				}
				else
				{
					mPlayer.mute();
					audio.setBackgroundResource(R.drawable.ic_media_mute);
				}
    			}
    		});
    	}
    
    public interface MediaPlayerControl {
    	void    start();
    	void    pause();
    	void 	setLoop();
    	void	disableLoop();
    	void 	mute();
    	void 	audio();
    	void	stop();
    	int     getDuration();
    	int     getCurrentPosition();
    	void    seekTo(int pos);
    	boolean isPlaying();
    	boolean isLooping();
    	boolean isMute();
    	}

    public void setMediaPlayer(MediaPlayerControl m) {
    	mPlayer=m;
	}
    public void show (ViewGroup view){
    	if(!isShowing()){
    		
    		anchorView=view;
    		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		mRoot=inflater.inflate(R.layout.controller, null);
    		Log.d("show","show");
    		initController(mRoot);
    		anchorView.addView(mRoot);
    	}
    	mHandler.sendEmptyMessage(SHOW_PROGRESS);		
    }
    public void hide(){
    	mHandler.removeMessages(SHOW_PROGRESS);
    	anchorView.removeView(mRoot);
    	anchorView=null;
    	
    }
    public boolean isShowing(){
    	return (anchorView!=null);
    }
    public void initController(View v){
    	pause=(ImageButton)v.findViewById(R.id.pause);
    	stop=(ImageButton)v.findViewById(R.id.stop);
    	loop=(ImageButton)v.findViewById(R.id.loop);
    	audio=(ImageButton)v.findViewById(R.id.audio);
    	seekBar=(SeekBar)v.findViewById(R.id.mediacontroller_progress);
    	endTime=(TextView)v.findViewById(R.id.time);
    	currentTime=(TextView)v.findViewById(R.id.time_current);
    	mFormatBuilder = new StringBuilder();
    	mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
    	seekBar.setMax(1000);
    	onClickListener();
}

}
