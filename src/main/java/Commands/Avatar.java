package Commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Avatar {
    public Avatar(String[] args, MessageReceivedEvent event){
        if(args[0].equals("avatar")) {
            event.getChannel().sendMessage(event.getMessage().getAuthor().getEffectiveAvatarUrl()).queue();
        }
    }
}
