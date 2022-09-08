package Commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Help {
    public Help(String[] args, MessageReceivedEvent event){
        if(args[0].equals("help")) {
            if(args.length == 1) {
                EmbedBuilder defined = new EmbedBuilder();
                defined.setAuthor("Command Help");
                defined.setDescription("Down below is a list of commands");
//        defined.setDescription("Down below is a list of commands, run ``.help command-name`` to get extended help on that command!");
//        defined.addField("Fun [3]", "``define``, ``thisguy``, ``wanted``", false);
                defined.addField("Util [3]", "`avatar`, `color`, `fandom`, `help`", false);
                defined.setColor(0x000000);
                event.getChannel().sendMessageEmbeds(defined.build()).queue();
            }
            if(args.length == 2){
                command(args, event);
            }
        }
    }
    private void command(String[] args, MessageReceivedEvent event){
        if(args[1].equals("avatar")){
            event.getChannel().sendMessage("``Get your avatar profile picture``").queue();
        }
        else if(args[1].equals("color")){
            event.getChannel().sendMessage("`Display hex or rgb color as an image`\n" +
                    "`For hex:` .color 0x000000 `or` .color #000000 `or` .color 000000\n" +
                    "`For rgb:` .color 0 0 0 `(.color red green blue)`").queue();
        }
        else if (args[1].equals("fandom")){
            event.getChannel().sendMessage("`Get website data from fandom of choice`\n" +
                    "`.fandom set examplewebsite`\n" +
                    "`.fandom fetch article`").queue();
        }
    }
}
