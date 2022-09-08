package Commands;

import Commands.RecordHandler.ReceiveHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.Objects;

public class MusicPlayer {
    SlashCommandInteractionEvent event;

    private void sendLinkOrQuery(String search){
        if(!search.isEmpty()){
            if(CmdPlay.isURL(search)){
                CmdPlay.play(event,search);
                return;
            }
            CmdPlay.play(event, "ytsearch:" + search);
        }
    }

    private void addTrack(){
        OptionMapping youtube = event.getOption("youtube");
        if(youtube != null){
            String search = youtube.getAsString();
            if(CmdPlay.isUserConnected(event)){
                if(!CmdPlay.isBotConnected(event))
                    CmdPlay.connect(event);
                else
                    if(!CmdPlay.inSameVoice(event)){
                        event.reply("Bot is currently in use in another voice channel").setEphemeral(true).queue();
                        return;
                }
            }
            else{
                event.reply("You need to be in a voice channel for this command").setEphemeral(true).queue();
                return;
            }
            sendLinkOrQuery(search);
        }
    }

    private boolean assertions(){
        if(!CmdPlay.isUserConnected(event)){
            event.reply("You need to be in a voice channel for this command").setEphemeral(true).queue();
            return false;
        }
        if(!CmdPlay.isBotConnected(event)){
            event.reply("I am not connected").setEphemeral(true).queue();
            return false;
        }
        if(!CmdPlay.inSameVoice(event)){
            event.reply("Bot is currently in use in another voice channel").setEphemeral(true).queue();
            return false;
        }
        return true;
    }
    private boolean assertions(MessageReceivedEvent event){
        if(!CmdPlay.isUserConnected(event)){
            event.getMessage().getChannel().sendMessage("You need to be in a voice channel for this command").queue();
            return false;
        }
        if(!CmdPlay.isBotConnected(event)){
            event.getMessage().getChannel().sendMessage("I am not connected").queue();
            return false;
        }
        if(!CmdPlay.inSameVoice(event)){
            event.getMessage().getChannel().sendMessage("Bot is currently in use in another voice channel").queue();
            return false;
        }
        return true;
    }

    private void pause(){
        if(assertions())
            CmdPlay.pause(event);
    }

    private void skipTrack(){
        if(assertions())
            CmdPlay.skip(event);
    }

    private void loop(){
        if(assertions())
            CmdPlay.loop(event);
    }

    private void listQueue(){
        if(assertions())
            CmdPlay.queue(event);
    }

    private void nowPlaying(){
        if(assertions())
            CmdPlay.nowPlaying(event);
    }

    private void volume(){
        if (assertions())
            CmdPlay.volume(event);
    }

    private void remove(){
        if(assertions())
            CmdPlay.remove(event);
    }

    private void clear(){
        if(assertions())
            CmdPlay.clear(event);
    }

    private void disconnect(){
        if(assertions())
            CmdPlay.disconnect(event);
    }

    public MusicPlayer(SlashCommandInteractionEvent event){
        this.event = event;
        switch (Objects.requireNonNull(event.getSubcommandName())) {
            case "add" -> addTrack();
            case "pause" -> pause();
            case "skip" -> skipTrack();
            case "loop" -> loop();
            case "queue" -> listQueue();
            case "np" -> nowPlaying();
            case "volume" -> volume();
            case "remove" -> remove();
            case "clear" -> clear();
            case "disconnect" -> disconnect();
            default -> {
            }
        }
    }

    private String remainingString(String[] args){
        StringBuilder s = new StringBuilder();
        for(int i = 2; i < args.length; i++){
            if(i < args.length-1)
                s.append(args[i]).append(" ");
            else if(i == args.length-1)
                s.append(args[i]);
        }
        return s.toString();
    }

    public MusicPlayer(String[] args, MessageReceivedEvent event){
        if(args[0].equalsIgnoreCase("music")) {
            if (args.length > 1)
                if (args[1].equals("add")) {
                    if (args.length > 2) {
                        String search = remainingString(args);
                        if (CmdPlay.isUserConnected(event)) {
                            if (!CmdPlay.isBotConnected(event))
                                CmdPlay.connect(event);
                            else if (!CmdPlay.inSameVoice(event)) {
                                event.getMessage().getChannel().sendMessage("Bot is currently in use in another voice channel").queue();
                                return;
                            }
                        } else {
                            event.getMessage().getChannel().sendMessage("You need to be in a voice channel for this command").queue();
                            return;
                        }
                        if (!search.isEmpty()) {
                            if (CmdPlay.isURL(search)) {
                                CmdPlay.play(event, search);
                                return;
                            }
                            CmdPlay.play(event, "ytsearch:" + search);
                        }
                    }
                } else if (assertions(event))
                    switch (args[1]) {
                        case "pause", "p" -> CmdPlay.pause(event);
                        case "skip", "s" -> CmdPlay.skip(event);
                        case "loop", "l" -> CmdPlay.loop(event);
                        case "queue", "q" -> CmdPlay.queue(event);
                        case "np" -> CmdPlay.nowPlaying(event);
                        case "volume", "v" -> {
                            if (args.length == 2) {
                                CmdPlay.volume(event, 0);
                            } else {
                                int volume;
                                try {
                                    volume = Integer.parseInt(args[2]);
                                    if (volume > 0 && volume <= 100)
                                        CmdPlay.volume(event, Integer.parseInt(args[2]));
                                    else
                                        event.getMessage().getChannel().sendMessage("Number must be between 1-100").queue();
                                } catch (NumberFormatException e) {
                                    event.getMessage().getChannel().sendMessage("Invalid number").queue();
                                }
                            }
                        }
                        case "remove", "r" -> {
                            if (args.length > 2) {
                                int index;
                                try {
                                    index = Integer.parseInt(args[2]);
                                    if (index > 0 && index <= 20)
                                        CmdPlay.remove(event, index);
                                    else
                                        event.getMessage().getChannel().sendMessage("Number must be between 1-20").queue();
                                } catch (NumberFormatException e) {
                                    event.getMessage().getChannel().sendMessage("Invalid number").queue();
                                }
                            }
                        }
                        case "clear", "c" -> CmdPlay.clear(event);
                        case "disconnect", "dis", "d" -> CmdPlay.disconnect(event);
                        default -> {
                        }
                    }
            }
        }
}
