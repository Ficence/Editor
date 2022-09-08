package Commands;

import Commands.LavaPlayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Objects;

public class CmdPlay {
    public static boolean isUserConnected(SlashCommandInteractionEvent event){
        GuildVoiceState userVoice = Objects.requireNonNull(Objects.requireNonNull(event.getMember()).getVoiceState());
        return userVoice.inAudioChannel();
    }
    public static boolean isUserConnected(MessageReceivedEvent event){
        GuildVoiceState userVoice = Objects.requireNonNull(Objects.requireNonNull(event.getMember()).getVoiceState());
        return userVoice.inAudioChannel();
    }

    public static boolean isBotConnected(SlashCommandInteractionEvent event){
        GuildVoiceState botVoice = Objects.requireNonNull(Objects.requireNonNull(event.getGuild()).getSelfMember().getVoiceState());
        return botVoice.inAudioChannel();
    }
    public static boolean isBotConnected(MessageReceivedEvent event){
        GuildVoiceState botVoice = Objects.requireNonNull(Objects.requireNonNull(event.getGuild()).getSelfMember().getVoiceState());
        return botVoice.inAudioChannel();
    }

    public static boolean inSameVoice(SlashCommandInteractionEvent event){
        GuildVoiceState userVoice = Objects.requireNonNull(Objects.requireNonNull(event.getMember()).getVoiceState());
        GuildVoiceState botVoice = Objects.requireNonNull(Objects.requireNonNull(event.getGuild()).getSelfMember().getVoiceState());
        return userVoice.getChannel() == botVoice.getChannel();
    }
    public static boolean inSameVoice(MessageReceivedEvent event){
        GuildVoiceState userVoice = Objects.requireNonNull(Objects.requireNonNull(event.getMember()).getVoiceState());
        GuildVoiceState botVoice = Objects.requireNonNull(Objects.requireNonNull(event.getGuild()).getSelfMember().getVoiceState());
        return userVoice.getChannel() == botVoice.getChannel();
    }

    public static void connect(SlashCommandInteractionEvent event){
        final AudioManager audioManager = Objects.requireNonNull(event.getGuild()).getAudioManager();
        final VoiceChannel memberChannel = (VoiceChannel) Objects.requireNonNull(Objects.requireNonNull(event.getMember()).getVoiceState()).getChannel();

        audioManager.openAudioConnection(memberChannel);
    }
    public static void connect(MessageReceivedEvent event){
        final AudioManager audioManager = Objects.requireNonNull(event.getGuild()).getAudioManager();
        final VoiceChannel memberChannel = (VoiceChannel) Objects.requireNonNull(Objects.requireNonNull(event.getMember()).getVoiceState()).getChannel();

        audioManager.openAudioConnection(memberChannel);
    }

    public static void play(SlashCommandInteractionEvent event, String url){
        PlayerManager.getInstance().loadAndPlay(event, url);
    }
    public static void play(MessageReceivedEvent event, String url){
        PlayerManager.getInstance().loadAndPlay(event, url);
    }

    public static void pause(SlashCommandInteractionEvent event){
        PlayerManager.getInstance().pause(event);
    }
    public static void pause(MessageReceivedEvent event){
        PlayerManager.getInstance().pause(event);
    }

    public static void skip(SlashCommandInteractionEvent event){
        PlayerManager.getInstance().skip(event);
    }
    public static void skip(MessageReceivedEvent event){
        PlayerManager.getInstance().skip(event);
    }

    public static void loop(SlashCommandInteractionEvent event){PlayerManager.getInstance().loop(event);}
    public static void loop(MessageReceivedEvent event){PlayerManager.getInstance().loop(event);}

    public static void queue(SlashCommandInteractionEvent event){
        PlayerManager.getInstance().printQueue(event);
    }
    public static void queue(MessageReceivedEvent event){
        PlayerManager.getInstance().printQueue(event);
    }

    public static void nowPlaying(SlashCommandInteractionEvent event){
        PlayerManager.getInstance().nowPlaying(event);
    }
    public static void nowPlaying(MessageReceivedEvent event){
        PlayerManager.getInstance().nowPlaying(event);
    }

    public static void volume(SlashCommandInteractionEvent event){
        PlayerManager.getInstance().volume(event);
    }
    public static void volume(MessageReceivedEvent event, int volume){
        PlayerManager.getInstance().volume(event,volume);
    }

    public static void remove(SlashCommandInteractionEvent event){
        PlayerManager.getInstance().remove(event);
    }
    public static void remove(MessageReceivedEvent event, int index){
        PlayerManager.getInstance().remove(event, index);
    }

    public static void clear(SlashCommandInteractionEvent event){
        PlayerManager.getInstance().clear(event);
    }
    public static void clear(MessageReceivedEvent event){
        PlayerManager.getInstance().clear(event);
    }

    public static void disconnect(SlashCommandInteractionEvent event){
        PlayerManager.getInstance().disconnect(event);
    }
    public static void disconnect(MessageReceivedEvent event){
        PlayerManager.getInstance().disconnect(event);
    }

    private CmdPlay(){}

    public static boolean isURL(String url){
        try {
            new URL(url);
            return true;
        }
        catch (MalformedURLException e){
            return false;
        }
    }
}