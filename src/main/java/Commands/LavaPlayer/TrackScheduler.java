package Commands.LavaPlayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Guild;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class TrackScheduler extends AudioEventAdapter implements Runnable{
    public final AudioPlayer audioPlayer;
    public final BlockingDeque<AudioTrack> queue;
    private boolean isRepeat = false;
    private Guild guild;
    private Thread thread;


    public TrackScheduler(AudioPlayer audioPlayer){
        this.audioPlayer = audioPlayer;
        this.queue = new LinkedBlockingDeque<>();
    }

    public void setGuild(Guild guild){
        this.guild = guild;
    }

    public void queue(AudioTrack track){
        if (!this.audioPlayer.startTrack(track, true)){
            if(thread != null && queue.size() == 0) {
                thread.interrupt();
                thread = null;
            }
            this.queue.offer(track);
        }
    }

    public boolean pause(){
        if(this.audioPlayer.getPlayingTrack() != null) {
            this.audioPlayer.setPaused(!this.audioPlayer.isPaused());
            return true;
        }
        return false;
    }

    public void setRepeat(boolean isRepeat) {
        this.isRepeat = isRepeat;
    }

    public boolean getRepeat() {
        return this.isRepeat;
    }

    public void clear(){
        queue.clear();
        PlayerManager.QUEUE_SIZE = 0;
    }

    public void nextTrack(){
        if(this.audioPlayer.getPlayingTrack() == null && this.queue.size() == 0){
            thread = new Thread(this);
            thread.start();
        }
        this.audioPlayer.startTrack(this.queue.poll(), false);
    }

    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason){
        if (endReason.mayStartNext){
            if(this.isRepeat)
                queue.offerFirst(track.makeClone());
            else
                if(PlayerManager.QUEUE_SIZE > 0)
                    PlayerManager.QUEUE_SIZE--;
            nextTrack();
        }
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double nanoSecondConversion = 1E9D;
        double delta = 0.0D;
        double seconds = 0.0D;

        e:while(true){
            long now = System.nanoTime();
            for(delta += (double)(now - lastTime) / nanoSecondConversion; delta >= 1.0D; delta = 0.0D) {
                seconds += 1;
                if(audioPlayer.getPlayingTrack() != null || queue.size() > 0) {
                    break e;
                }
                if(seconds >= 60.0){
                    guild.getAudioManager().closeAudioConnection();
                    break e;
                }
            }
            lastTime = now;
        }
    }
}
