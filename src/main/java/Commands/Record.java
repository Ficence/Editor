package Commands;

import Commands.RecordHandler.ReceiveHandler;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.audio.CombinedAudio;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Record {
    public Record(String[] args, MessageReceivedEvent event){
        if(args[0].equalsIgnoreCase("rec")) {
            if(!CmdPlay.isUserConnected(event)){
                event.getMessage().getChannel().sendMessage("You need to be in a voice channel for this command").queue();
                return;
            }
            final VoiceChannel userVoiceChannel = (VoiceChannel) Objects.requireNonNull(Objects.requireNonNull(event.getMember()).getVoiceState()).getChannel();
            assert userVoiceChannel != null;
            final VoiceChannel botVoice = (VoiceChannel) Objects.requireNonNull(Objects.requireNonNull(event.getGuild()).getSelfMember().getVoiceState()).getChannel();
            assert botVoice != null;

            if(args.length == 1) {
                if (!CmdPlay.isBotConnected(event)) {
                    userVoiceChannel.getGuild().getAudioManager().openAudioConnection(userVoiceChannel);
                    record(event, userVoiceChannel);
                }
                else if(!CmdPlay.inSameVoice(event)){
                    event.getMessage().getChannel().sendMessage("Bot is currently in use in another voice channel").queue();
                }
            }
            else if(args.length == 2) {
                if (args[1].equalsIgnoreCase("clip"))
                    ReceiveHandler.sendAudioFile(event);
                else if (args[1].equalsIgnoreCase("stop")){
                    event.getMessage().getChannel().sendMessage("Recording stopped").queue();
                    userVoiceChannel.getGuild().getAudioManager().setReceivingHandler(null);
                }
            }
        }
    }

    private void record(MessageReceivedEvent event, VoiceChannel userVoiceChannel){
        if(ReceiveHandler.receiveHandler == null) {
            ReceiveHandler.receiveHandler = new ReceiveHandler();
            event.getMessage().getChannel().sendMessage("Recording in progress...").queue();
        }
        userVoiceChannel.getGuild().getAudioManager().setReceivingHandler(ReceiveHandler.receiveHandler);
    }
    private void echo(MessageReceivedEvent event){
        final VoiceChannel userVoiceChannel = (VoiceChannel) Objects.requireNonNull(Objects.requireNonNull(event.getMember()).getVoiceState()).getChannel();

        EchoHandler echo = new EchoHandler();
        assert userVoiceChannel != null;
        userVoiceChannel.getGuild().getAudioManager().setReceivingHandler(echo);
        userVoiceChannel.getGuild().getAudioManager().setSendingHandler(echo);
    }

    private static class EchoHandler implements AudioSendHandler, AudioReceiveHandler{
        private final Queue<byte[]> queue = new ConcurrentLinkedQueue<>();
        @Override
        public boolean canReceiveCombined() {
            return queue.size() < 10;
        }

        @Override
        public void handleCombinedAudio(@NotNull CombinedAudio combinedAudio) {
            if (combinedAudio.getUsers().isEmpty())
                return;

            byte[] data = combinedAudio.getAudioData(1.0f); // volume at 100% = 1.0 (50% = 0.5 / 55% = 0.55)
            queue.add(data);
        }
        @Override
        public boolean canProvide() {
            return !queue.isEmpty();
        }

        @Nullable
        @Override
        public ByteBuffer provide20MsAudio() {
            byte[] data = queue.poll();
            return data == null ? null : ByteBuffer.wrap(data);
        }

        @Override
        public boolean isOpus() {
            return false;
        }
    }


}