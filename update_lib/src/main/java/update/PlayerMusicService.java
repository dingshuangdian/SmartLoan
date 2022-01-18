package update;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.teprinciple.updateapputils.R;

/**
 * <p>版权©️所有：大参林医药集团<p>
 * <p>作者：dingshuangdian<p>
 * <p>创建时间：2020/11/10<p>
 */
public class PlayerMusicService extends Service {
    private MediaPlayer mMediaPlayer;
    private MusicBinder binder;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.silence);
        mMediaPlayer.setLooping(true);
        binder = new MusicBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public class MusicBinder extends Binder {
        public void startPlaySong() {
            mMediaPlayer.start();
        }

        public void stopPlaySong() {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }
    }

}
