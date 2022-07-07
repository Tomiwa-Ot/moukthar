package com.ot.androidrat;

import android.content.Context;
import android.media.AudioManager;

public class Audio {

    private Context context;

    public Audio(Context context) {
        this.context = context;
    }

    public void setVolume(int stream, int volume) {
        AudioManager manager = (AudioManager) this.context.getSystemService(Context.AUDIO_SERVICE);
        int value =  manager.getStreamVolume(AudioManager.STREAM_MUSIC);
        manager.setStreamVolume(stream,volume,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }

    public Integer getVolume() {
        AudioManager manager = (AudioManager) this.context.getSystemService(Context.AUDIO_SERVICE);
        return manager.getStreamVolume(AudioManager.STREAM_MUSIC);

    }

    public void setMode(int mode) {
        AudioManager manager = (AudioManager) this.context.getSystemService(Context.AUDIO_SERVICE);
        manager.setMode(mode);
    }

    public Integer getMode(){
        AudioManager manager = (AudioManager) this.context.getSystemService(Context.AUDIO_SERVICE);
        return manager.getMode();
    }

    public void playEffect(int effect,int volume){
        AudioManager manager = (AudioManager) this.context.getSystemService(Context.AUDIO_SERVICE);
        if(volume != 0)
            manager.playSoundEffect(effect,volume);
        else
            manager.playSoundEffect(effect);

    }
}
