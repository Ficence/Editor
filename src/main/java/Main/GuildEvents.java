package Main;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class GuildEvents extends ListenerAdapter {
    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        try {
            File myObj = new File("/home/ficence/Editor/res/welcome.txt");
            Scanner myReader = new Scanner(myObj);
            String[] data;
            while (myReader.hasNextLine()) {
                data = myReader.nextLine().split("\\s");
                if(data[0].equals(event.getGuild().getId())){
                    if(Boolean.parseBoolean(data[1])){
                        TextChannel ch = (TextChannel) event.getGuild().getGuildChannelById(data[2]);
                        assert ch != null;
                        EmbedBuilder defined = new EmbedBuilder();
                        if(event.getUser().getAvatarUrl() == null){
                            defined.setThumbnail(event.getUser().getDefaultAvatarUrl());
                            defined.setAuthor(event.getMember().getUser().getAsTag(), event.getUser().getDefaultAvatarUrl(),event.getUser().getDefaultAvatarUrl());
                        }
                        else {
                            defined.setThumbnail(event.getUser().getAvatarUrl());
                            defined.setAuthor(event.getMember().getUser().getAsTag(), event.getMember().getUser().getAvatarUrl(),event.getMember().getUser().getAvatarUrl());
                        }

                        defined.setDescription(event.getMember().getAsMention() + " was uploaded to the server!");

                        defined.setColor(0xFF00FF00);
                        ch.sendMessageEmbeds(defined.build()).queue();
//                        ch.sendMessage("```\n"+ Objects.requireNonNull(event.getMember()).getEffectiveName()+" ("+event.getMember().getAsMention()+") was uploaded to the server\n```").queue();
                    }
                    break;
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        try {
            File myObj = new File("/home/ficence/Editor/res/welcome.txt");
            Scanner myReader = new Scanner(myObj);
            String[] data;
            while (myReader.hasNextLine()) {
                data = myReader.nextLine().split("\\s");
                if(data[0].equals(event.getGuild().getId())){
                    if(Boolean.parseBoolean(data[1])){
                        TextChannel ch = (TextChannel) event.getGuild().getGuildChannelById(data[2]);
                        assert ch != null;
                        EmbedBuilder defined = new EmbedBuilder();
                        if(event.getUser().getAvatarUrl() == null){
                            defined.setThumbnail(event.getUser().getDefaultAvatarUrl());
                            defined.setAuthor(event.getUser().getAsTag(), event.getUser().getDefaultAvatarUrl(),event.getUser().getDefaultAvatarUrl());
                        }
                        else {
                            defined.setThumbnail(event.getUser().getAvatarUrl());
                            defined.setAuthor(event.getUser().getAsTag(), event.getUser().getAvatarUrl(),event.getUser().getAvatarUrl());
                        }
                        defined.setDescription(event.getUser().getAsMention() + " left the server :(");

                        defined.setColor(0xFFFF0000);
                        ch.sendMessageEmbeds(defined.build()).queue();
//                        ch.sendMessage("```\n"+ Objects.requireNonNull(event.getMember()).getEffectiveName()+" ("+event.getMember().getAsMention()+") was uploaded to the server\n```").queue();
                    }
                    break;
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
