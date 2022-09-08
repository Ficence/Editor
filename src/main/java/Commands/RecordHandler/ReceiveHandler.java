package Commands.RecordHandler;

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.CombinedAudio;
import net.dv8tion.jda.api.audio.UserAudio;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ReceiveHandler implements AudioReceiveHandler {
    public static ReceiveHandler receiveHandler;
    private static final Queue<byte[]> queue = new ConcurrentLinkedQueue<>();
    private long byteSize = 0;
    private static boolean mutable = true;

    public ReceiveHandler(){
    }
    public static void sendAudioFile(MessageReceivedEvent event){
        mutable = false;
        final List<byte[]> receivedBytes = new ArrayList<>(queue);
        try {
            int size=0;
            for (byte[] bs : receivedBytes) {
                size+=bs.length;
            }
            byte[] decodedData=new byte[size];
            int i=0;
            for (byte[] bs : receivedBytes) {
                for (byte b : bs) {
                    decodedData[i++] = b;
                }
            }
            AudioFormat audioFormat = new AudioFormat(48000, 16, 2, true, true);
            AudioInputStream aIS = new AudioInputStream(new ByteArrayInputStream(decodedData),audioFormat, decodedData.length);
            File outputFile = new File("rec.wav");
            AudioSystem.write(aIS, AudioFileFormat.Type.WAVE, outputFile);
            event.getMessage().getChannel().sendFile(outputFile).queue();

        } catch (OutOfMemoryError | IOException e) {
            e.printStackTrace();
        }
        mutable = true;
    }
    @Override
    public boolean canReceiveCombined() {
        return true;
    }
    @Override
    public boolean canReceiveUser() {
        return false;
    }
    @Override
    synchronized public void handleCombinedAudio(@NotNull CombinedAudio combinedAudio) {
        try {
            if(mutable) {
                byte[] data = combinedAudio.getAudioData(1.0);
                byteSize += data.length;
                queue.add(data);

                while ((byteSize >> 20) >= 5) {
                    byteSize -= queue.element().length;
                    queue.poll();
                }
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }
    @Override
    public void handleUserAudio(UserAudio userAudio) {
        if(userAudio.getUser().getId().equals("517525692448243712")){
            byte[] data = userAudio.getAudioData(1.0);
        }
    }
}