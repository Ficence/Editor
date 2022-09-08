package Commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DisplayColor {
    public DisplayColor(String[] args, MessageReceivedEvent event){
        if(args[0].equals("color")) {
            if(args.length == 2) {
                String hex = "FF"+args[1];
                if(args[1].startsWith("0x")){
                    hex = "FF"+args[1].substring(2);
                }
                else if (args[1].startsWith("#")){
                    hex = "FF"+args[1].substring(1);
                }
                if (hex.length() == 8) {
                    for (int i = 0; i < hex.length(); i++) {
                        int c = hex.charAt(i);
                        boolean one = (c >= 48 && c <= 57);
                        boolean two = (c >= 65 && c <= 70);
                        boolean three = (c >= 97 && c <= 102);
                        if (!one && !two && !three) {
                            event.getChannel().sendMessage("`Invalid character found`").queue();
                            return;
                        }
                    }
                    int argb = Integer.parseUnsignedInt(hex,16);
                    BufferedImage image = null;
                    try {
                        image = ImageIO.read(new File("/home/ficence/Editor/res/Square.png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    assert image != null;
                    int width = image.getWidth();
                    int height = image.getHeight();

                    BufferedImage newImage = image;
                    int[] imagePixels = new int[image.getWidth() * image.getHeight()];
                    image.getRGB(0, 0, image.getWidth(), image.getHeight(), imagePixels, 0, image.getWidth());

                    for (int j = 0; j < height; j++) {
                        for (int i = 0; i < width; i++) {
                            int e = (imagePixels[i + j * width]);
                            int alpha1 = (e >> 24) & 0xFF;
                            int red1 = ((e >> 16) & 0xFF);
                            int green1 = ((e >> 8) & 0xFF);
                            int blue1 = (e & 0xFF);
                            if (alpha1 == 255 && red1 == 128 && green1 == 128 && blue1 == 128) {
                                newImage.setRGB(i, j, argb);
                            }
                        }
                    }
                    try {
                        ImageIO.write(newImage, "png", new File("/home/ficence/Editor/res/Square1.png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    event.getChannel().sendFile(new File("/home/ficence/Editor/res/Square1.png")).queue();
                }
            }
            if(args.length == 4) {
                if (!numericCheck(args, event))
                    return;

                int alpha = 255;
                int red = Integer.parseInt(args[1]);
                int green = Integer.parseInt(args[2]);
                int blue = Integer.parseInt(args[3]);
                int argb = ((alpha << 24) | ((red) << 16) | ((green) << 8) | (blue));

                BufferedImage image = null;
                try {
                    image = ImageIO.read(new File("res/Square.png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                assert image != null;
                int width = image.getWidth();
                int height = image.getHeight();

                BufferedImage newImage = image;
                int[] imagePixels = new int[image.getWidth() * image.getHeight()];
                image.getRGB(0, 0, image.getWidth(), image.getHeight(), imagePixels, 0, image.getWidth());

                for (int j = 0; j < height; j++) {
                    for (int i = 0; i < width; i++) {
                        int e = (imagePixels[i + j * width]);
                        int alpha1 = (e >> 24) & 0xFF;
                        int red1 = ((e >> 16) & 0xFF);
                        int green1 = ((e >> 8) & 0xFF);
                        int blue1 = (e & 0xFF);
                        if (alpha1 == 255 && red1 == 128 && green1 == 128 && blue1 == 128) {
                            newImage.setRGB(i, j, argb);
                        }
                    }
                }
                try {
                    ImageIO.write(newImage, "png", new File("res/Square1.png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                event.getChannel().sendFile(new File("res/Square1.png")).queue();
            }
        }
    }

    public boolean numericCheck(String [] args, MessageReceivedEvent event){
        if (!isNumeric(args[1]) || !isNumeric(args[2]) || !isNumeric(args[3])){
            if(!isNumeric(args[1])){
                if(!isNumeric(args[2])){
                    if(!isNumeric(args[3])){
                        event.getChannel().sendMessage("`" + args[1] + ", " + args[2] + " and " + args[3] + " are not numbers`").queue();
                    }
                    else {
                        event.getChannel().sendMessage("`" + args[1] + " and " + args[2] + " are not numbers`").queue();
                    }
                }
                else if (!isNumeric(args[4])){
                    event.getChannel().sendMessage("`" + args[1] + "and " + args[3] + " are not numbers`").queue();
                }
                else {
                    event.getChannel().sendMessage("`" + args[1] + " is not a number`").queue();
                }
            }
            else if(!isNumeric(args[2])){
                if(!isNumeric(args[3])){
                    event.getChannel().sendMessage("`" + args[2] + " and " + args[3] + " are not numbers`").queue();
                }
                else {
                    event.getChannel().sendMessage("`" + args[2] + " is not a number`").queue();
                }
            }
            else if(!isNumeric(args[3])){
                event.getChannel().sendMessage("`" + args[3] + " is not a number`").queue();
            }
            return false;
        }
        if(Integer.parseInt(args[1]) < 0 || Integer.parseInt(args[1]) > 255 || Integer.parseInt(args[2]) < 0 || Integer.parseInt(args[2]) > 255 ||
                Integer.parseInt(args[3]) < 0 || Integer.parseInt(args[3]) > 255){
            event.getChannel().sendMessage("`Please make the numbers from 0 to 255 (RGB)`").queue();
            return false;
        }
        return true;
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}
