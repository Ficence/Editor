package Commands.LavaPlayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerManager {
    private static PlayerManager INSTANCE;
    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;
    public static int QUEUE_SIZE = 0;

    public PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public GuildMusicManager getMusicManager(Guild guild){
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) ->{
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);
            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());
            return guildMusicManager;
        });
    }

    private String getDuration(long length){
        long seconds = length/1000;
        long minutes = Math.floorDiv(seconds, 60);
        long hours = Math.floorDiv(minutes,60);
        int minRemain = (int) minutes%60;
        int secRemain = (int) seconds%60;
        return String.format("%02d:%02d:%02d",hours,minRemain,secRemain);
    }

    public void loadAndPlay(SlashCommandInteractionEvent event, String trackURL){
        final GuildMusicManager musicManager = this.getMusicManager(Objects.requireNonNull(event.getGuild()));
        QUEUE_SIZE++;
        if (QUEUE_SIZE > 20){
            event.reply("Cannot exceed max queue size of `20`").setEphemeral(true).queue();
            QUEUE_SIZE--;
            return;
        }
        if (trackURL.contains("youtube.com/shorts/")){
            trackURL = trackURL.replace("shorts/", "watch?v=");
        }
        this.audioPlayerManager.loadItemOrdered(musicManager, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                musicManager.scheduler.queue(audioTrack);
                event.reply("Adding to queue **`" +
                        audioTrack.getInfo().title +
                        "` ** by ** `" +
                        audioTrack.getInfo().author +
                        "`**").queue();
                musicManager.scheduler.setGuild(event.getGuild());
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                final List<AudioTrack> tracks = audioPlaylist.getTracks();
                if (!tracks.isEmpty()){
                    if(tracks.size() == 1 || audioPlaylist.isSearchResult()) {
                        musicManager.scheduler.queue(tracks.get(0));
                        event.reply("Adding to queue **`" +
                                tracks.get(0).getInfo().title +
                                "` ** by ** `" +
                                tracks.get(0).getInfo().author +
                                "`**").queue();
                        musicManager.scheduler.setGuild(event.getGuild());
                    }
                    else{
                        if(tracks.size() + musicManager.scheduler.queue.size() <= 20){
                            for (AudioTrack track : tracks) {
                                musicManager.scheduler.queue(track);
                            }
                            event.reply("Playlist `"+audioPlaylist.getName()+"` added.").queue();
                        }
                        else{
                            event.reply("Playlist rejected.  Playlist with current queue will exceed the maximum queue size of `20`").setEphemeral(true).queue();
                        }
                    }
                }
            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException e) {
                event.reply(e.getMessage()).setEphemeral(true).queue();
                QUEUE_SIZE--;
            }
        });
    }
    public void loadAndPlay(MessageReceivedEvent event, String trackURL){
        final GuildMusicManager musicManager = this.getMusicManager(Objects.requireNonNull(event.getGuild()));
        QUEUE_SIZE++;
        if (QUEUE_SIZE > 20){
            event.getMessage().getChannel().sendMessage("Cannot exceed max queue size of `20`").queue();
            QUEUE_SIZE--;
            return;
        }
        if (trackURL.contains("youtube.com/shorts/")){
            trackURL = trackURL.replace("shorts/", "watch?v=");
        }
        this.audioPlayerManager.loadItemOrdered(musicManager, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                musicManager.scheduler.queue(audioTrack);
                event.getMessage().getChannel().sendMessage("Adding to queue **`" +
                        audioTrack.getInfo().title +
                        "` ** by ** `" +
                        audioTrack.getInfo().author +
                        "`**").queue();
                musicManager.scheduler.setGuild(event.getGuild());
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                final List<AudioTrack> tracks = audioPlaylist.getTracks();
                if (!tracks.isEmpty()){
                    if(tracks.size() == 1 || audioPlaylist.isSearchResult()) {
                        musicManager.scheduler.queue(tracks.get(0));
                        event.getMessage().getChannel().sendMessage("Adding to queue **`" +
                                tracks.get(0).getInfo().title +
                                "` ** by ** `" +
                                tracks.get(0).getInfo().author +
                                "`**").queue();
                        musicManager.scheduler.setGuild(event.getGuild());
                    }
                    else{
                        if(tracks.size() + musicManager.scheduler.queue.size() <= 20){
                            for (AudioTrack track : tracks) {
                                musicManager.scheduler.queue(track);
                            }
                            event.getMessage().getChannel().sendMessage("Playlist `"+audioPlaylist.getName()+"` added.").queue();
                        }
                        else{
                            event.getMessage().getChannel().sendMessage("Playlist rejected.  Playlist with current queue will exceed the maximum queue size of `20`").queue();
                        }
                    }
                }
            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException e) {
                event.getMessage().getChannel().sendMessage(e.getMessage()).queue();
                QUEUE_SIZE--;
            }
        });
    }

    public void pause(SlashCommandInteractionEvent event){
        final GuildMusicManager musicManager = this.getMusicManager(Objects.requireNonNull(event.getGuild()));
        String message;
        if(musicManager.scheduler.pause()){
            if(musicManager.scheduler.audioPlayer.isPaused())
                message = "Track `paused`";
            else
                message = "Track `played`";
            event.reply(message).queue();
        }
        else {
            message = "No track currently playing";
            event.reply(message).setEphemeral(true).queue();
        }
    }
    public void pause(MessageReceivedEvent event){
        final GuildMusicManager musicManager = this.getMusicManager(Objects.requireNonNull(event.getGuild()));
        String message;
        if(musicManager.scheduler.pause()){
            if(musicManager.scheduler.audioPlayer.isPaused())
                message = "Track `paused`";
            else
                message = "Track `played`";
            event.getMessage().getChannel().sendMessage(message).queue();
        }
        else {
            message = "No track currently playing";
            event.getMessage().getChannel().sendMessage(message).queue();
        }
    }

    public void skip(SlashCommandInteractionEvent event){
        final GuildMusicManager musicManager = this.getMusicManager(Objects.requireNonNull(event.getGuild()));
        event.reply("Skipping track...").queue();
        musicManager.scheduler.nextTrack();
    }
    public void skip(MessageReceivedEvent event){
        final GuildMusicManager musicManager = this.getMusicManager(Objects.requireNonNull(event.getGuild()));
        event.getMessage().getChannel().sendMessage("Skipping track...").queue();
        musicManager.scheduler.nextTrack();
    }

    public void loop(SlashCommandInteractionEvent event){
        final GuildMusicManager musicManager = this.getMusicManager(Objects.requireNonNull(event.getGuild()));
        musicManager.scheduler.setRepeat(!musicManager.scheduler.getRepeat());
        if(musicManager.scheduler.getRepeat()){
            event.reply("Enabling loop...").queue();
        }
        else
            event.reply("Disabling loop...").queue();
    }
    public void loop(MessageReceivedEvent event){
        final GuildMusicManager musicManager = this.getMusicManager(Objects.requireNonNull(event.getGuild()));
        musicManager.scheduler.setRepeat(!musicManager.scheduler.getRepeat());
        if(musicManager.scheduler.getRepeat()){
            event.getMessage().getChannel().sendMessage("Enabling loop...").queue();
        }
        else
            event.getMessage().getChannel().sendMessage("Disabling loop...").queue();
    }

    public void printQueue(SlashCommandInteractionEvent event){
        final GuildMusicManager musicManager = this.getMusicManager(Objects.requireNonNull(event.getGuild()));
        BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;

        EmbedBuilder defined = new EmbedBuilder();
        defined.setAuthor("Queue");
        defined.setDescription("List of songs on queue");
        byte size = 1;
        for(AudioTrack track : queue){
            defined.addField(size + ". " + track.getInfo().title, "By: " + track.getInfo().author + ". Duration: " + getDuration(track.getInfo().length), false);
            size++;
        }
        defined.setColor(0x00FF00);
        event.replyEmbeds(defined.build()).queue();
    }
    public void printQueue(MessageReceivedEvent event){
        final GuildMusicManager musicManager = this.getMusicManager(Objects.requireNonNull(event.getGuild()));
        BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;

        EmbedBuilder defined = new EmbedBuilder();
        defined.setAuthor("Queue");
        defined.setDescription("List of songs on queue");
        byte size = 1;
        for(AudioTrack track : queue){
            defined.addField(size + ". " + track.getInfo().title, "By: " + track.getInfo().author + ". Duration: " + getDuration(track.getInfo().length), false);
            size++;
        }
        defined.setColor(0x00FF00);
        event.getMessage().getChannel().sendMessageEmbeds(defined.build()).queue();
    }

    public void nowPlaying(SlashCommandInteractionEvent event){
        final GuildMusicManager musicManager = this.getMusicManager(Objects.requireNonNull(event.getGuild()));
        AudioTrack track = musicManager.scheduler.audioPlayer.getPlayingTrack();

        if(track != null){
            AudioTrackInfo info = track.getInfo();
            String message = "Now playing: `" + info.title + "` by `" + info.author + "`. Time elapsed: `" + getDuration(track.getPosition()) + "/" + getDuration(info.length) + "`";
            event.reply(message).queue();
        }
        else
            event.reply("No track currently playing").setEphemeral(true).queue();
    }
    public void nowPlaying(MessageReceivedEvent event){
        final GuildMusicManager musicManager = this.getMusicManager(Objects.requireNonNull(event.getGuild()));
        AudioTrack track = musicManager.scheduler.audioPlayer.getPlayingTrack();

        if(track != null){
            AudioTrackInfo info = track.getInfo();
            String message = "Now playing: `" + info.title + "` by `" + info.author + "`. Time elapsed: `" + getDuration(track.getPosition()) + "/" + getDuration(info.length) + "`";
            event.getMessage().getChannel().sendMessage(message).queue();
        }
        else
            event.getMessage().getChannel().sendMessage("No track currently playing").queue();
    }

    public void volume(SlashCommandInteractionEvent event){
        OptionMapping intensity = event.getOption("integer");
        AudioPlayer audioPlayer = PlayerManager.getInstance().getMusicManager(Objects.requireNonNull(event.getGuild())).scheduler.audioPlayer;

        if(intensity == null){
            event.reply("Volume is: " + audioPlayer.getVolume()).queue();
            return;
        }
        int value = intensity.getAsInt();
        if(value > 0 && value <= 100){
            audioPlayer.setVolume(value);
            event.reply("Volume set to: `" + value + "`").queue();
            return;
        }
        if(value > 100 && value <= 500) {
            Optional<Role> r = Objects.requireNonNull(event.getMember()).getRoles().stream().filter(role -> role.getId().equals("632272799347638294") || role.getId().equals("715921944339546175")).findFirst();
            if (r.isPresent()) {
                audioPlayer.setVolume(value);
                event.reply("Volume set to: `" + value + "`").queue();
            } else {
                event.reply("Insufficient permissions").queue();
            }
        }
    }
    public void volume(MessageReceivedEvent event, int volume){
        AudioPlayer audioPlayer = PlayerManager.getInstance().getMusicManager(Objects.requireNonNull(event.getGuild())).scheduler.audioPlayer;

        if(volume == 0){
            event.getMessage().getChannel().sendMessage("Volume is: " + audioPlayer.getVolume()).queue();
            return;
        }
        if(volume > 0 && volume <= 100){
            audioPlayer.setVolume(volume);
            event.getMessage().getChannel().sendMessage("Volume set to: `" + volume + "`").queue();
            return;
        }
        if(volume > 100 && volume <= 500) {
            Optional<Role> r = Objects.requireNonNull(event.getMember()).getRoles().stream().filter(role -> role.getId().equals("632272799347638294") || role.getId().equals("715921944339546175")).findFirst();
            if (r.isPresent()) {
                audioPlayer.setVolume(volume);
                event.getMessage().getChannel().sendMessage("Volume set to: `" + volume + "`").queue();
            } else {
                event.getMessage().getChannel().sendMessage("Insufficient permissions").queue();
            }
        }
    }

    public void remove(SlashCommandInteractionEvent event){
        OptionMapping index = event.getOption("remove");
        int pos = Objects.requireNonNull(index).getAsInt();

        final GuildMusicManager musicManager = this.getMusicManager(Objects.requireNonNull(event.getGuild()));
        BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;
        if(pos > queue.size()){
            event.reply("Position in queue does not exist!").setEphemeral(true).queue();
            return;
        }
        AtomicInteger place = new AtomicInteger();
        queue.removeIf(track -> {
            place.getAndIncrement();
            return place.get() == pos;
        });
        event.reply("Track successfully removed").queue();
    }
    public void remove(MessageReceivedEvent event, int index){
        final GuildMusicManager musicManager = this.getMusicManager(Objects.requireNonNull(event.getGuild()));
        BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;
        if(index > queue.size()){
            event.getMessage().getChannel().sendMessage("Position in queue does not exist!").queue();
            return;
        }
        AtomicInteger place = new AtomicInteger();
        queue.removeIf(track -> {
            place.getAndIncrement();
            return place.get() == index;
        });
        event.getMessage().getChannel().sendMessage("Track successfully removed").queue();
    }

    public void clear(SlashCommandInteractionEvent event){
        final GuildMusicManager musicManager = this.getMusicManager(Objects.requireNonNull(event.getGuild()));
        musicManager.scheduler.clear();
        event.reply("Queue successfully cleared").queue();
    }
    public void clear(MessageReceivedEvent event){
        final GuildMusicManager musicManager = this.getMusicManager(Objects.requireNonNull(event.getGuild()));
        musicManager.scheduler.clear();
        event.getMessage().getChannel().sendMessage("Queue successfully cleared").queue();
    }

    public void disconnect(SlashCommandInteractionEvent event){
        Objects.requireNonNull(event.getGuild()).getAudioManager().closeAudioConnection();
        event.reply("Disconnected successfully").queue();
    }
    public void disconnect(MessageReceivedEvent event){
        Objects.requireNonNull(event.getGuild()).getAudioManager().closeAudioConnection();
        event.getMessage().getChannel().sendMessage("Disconnected successfully").queue();
    }

    public static PlayerManager getInstance(){
        if(INSTANCE == null)
            INSTANCE = new PlayerManager();
        return INSTANCE;
    }
}
