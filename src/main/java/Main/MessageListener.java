package Main;

import Commands.*;
import Commands.Record;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class MessageListener extends ListenerAdapter {
    public static String fandomName;
    EventWaiter waiter;

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getName().equals("music"))
            new MusicPlayer(event);
    }

    public MessageListener(EventWaiter waiter){
        this.waiter = waiter;
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        final String split = event.getMessage().getContentRaw();
        final String[] args;

        if(split.startsWith(".")) {
            args = event.getMessage().getContentRaw().replaceFirst(
                    "(?i)" + Pattern.quote(Main.prefix), "").split("\\s+");
        }
        else {
            return;
        }

        if(args.length > 0) {
            new Help(args, event);
            new Avatar(args, event);
            new DisplayColor(args, event);
            new Fandom(args, event);
            new EditImage(args, event, waiter);
            new MusicPlayer(args, event);
//            new Record(args, event);
//            new OCMaker(args, event);
        }
    }
}
