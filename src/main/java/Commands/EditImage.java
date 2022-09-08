package Commands;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class EditImage {
    String[] args;
    MessageReceivedEvent event;
    EventWaiter waiter;
    File finished;
    String prefix = "/home/ficence/Editor/res/";

    public EditImage(String[] args, MessageReceivedEvent event, EventWaiter waiter) {
        this.args = args;
        this.event = event;
        this.waiter = waiter;
        if(args[0].equalsIgnoreCase("ei"))
            parse();
    }
    private void parse(){
        if(args[1].equalsIgnoreCase("gen")){
            generate();
        }
    }
    private void generate(){
        if(args.length == 2) {
            String authorName = event.getMessage().getAuthor().getName();
            String description = "`Which tribe would you like to generate?`";
            String avatarURL = event.getMessage().getAuthor().getAvatarUrl();
            String[] names = new String[1];
            String[] values = new String[1];
            names[0] = "Tribes:";
            values[0] = "`MudWing, SeaWing, RainWing, NightWing, SandWing, IceWing, SkyWing, HiveWing`";

            EmbedBuilder emb1 = createEmbed(authorName, description, avatarURL, null, 1, names, values, 0x0000FF);
            event.getChannel().sendMessageEmbeds(emb1.build()).queue(message -> {
                waiter.waitForEvent(MessageReceivedEvent.class, e -> e.getAuthor().equals(event.getAuthor()) && e.getChannel().equals(event.getChannel())
                        && validTribe(e.getMessage().getContentRaw()), e -> {
                    if (e.getMessage().getContentRaw().toUpperCase().contains("WING")) {
                        String up = e.getMessage().getContentRaw().toUpperCase();
                        generateTribe(e, up);
                    } else {
                        String temp = e.getMessage().getContentRaw().toUpperCase() + "WING";
                        generateTribe(e, temp);
                    }
                }, 60, TimeUnit.SECONDS, () -> event.getChannel().sendMessageEmbeds(createEmbed(event.getMessage().getAuthor().getName(),
                        "`Message has expired`", event.getMessage().getAuthor().getAvatarUrl(), null, 0, null, null, 0xFF0000).build()).queue());
            });
        }
        else if (args.length >= 3){
            if (validTribe(args[2])) {
                String up = "";
                if (event.getMessage().getContentRaw().toUpperCase().contains("WING")) {
                    up = args[2].toUpperCase();
                } else {
                    up = args[2] + "WING";
                }
                if(args.length == 3) {
                    generateTribe(event, up);
                    }
                else{
                    String file = prefix + up + "/" + up + "_PALETTE_2.png";
                    String output = prefix + "ID_IMAGE_DUMP/"+ event.getAuthor().getIdLong() + ".png";
                    if (isValidHex(args[3])) {
                        if (args.length == 4) {
                            setBaseColor(file, output, args[3], 0xFF191919);
                            setUnderbellyAndBrow(event, up);
                        }
                        else if (args.length >= 5){
                            if(isValidHex(args[4])) {
                                setBaseColor(file, output, args[3], 0xFF191919);
                                file = prefix + "ID_IMAGE_DUMP/"+ event.getAuthor().getIdLong() + ".png";
                                finished = setBaseColor(file, output, args[4], 0xFF323232);
                                if(args.length == 5)
                                    setScales(event, up);
                                if(args.length >= 6){
                                    if(isValidHex(args[5])) {
                                        finished = setBaseColor(file, output, args[5], 0xFF4b4b4b);
                                        if(args.length == 6)
                                            setLegsWingsAndFace(event, up);
                                        if (args.length >= 7) {
                                            if(isValidHex(args[6])) {
                                                finished = setBaseColor(file, output, args[6], 0xFF646464);
                                                if(args.length == 7)
                                                    setSpines(event, up);
                                                else if (args.length == 8) {
                                                    if(isValidHex(args[7])) {
                                                        finished = setBaseColor(file, output, args[7], 0xFF969696);
                                                        compileImages(finished, prefix+up+"/"+up+"_OUTLINE_2.png", up);
                                                        display(compileImages(finished, prefix+up+"/"+up+"_OUTLINE_2.png", up));
                                                        deleteFiles(file, null);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    //RED HIGHLIGHT
    public File process(MessageReceivedEvent ev, String colorBaseFile, String outlineFile, String hex, int selection) {
        if (args[0].equalsIgnoreCase("ei")) {
            int argb = getColor(hex);
            BufferedImage image = null;
            try {
                image = ImageIO.read(new File(colorBaseFile));
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
//                    int alpha1 = (e >> 24) & 0xFF;
                    int red1 = ((e >> 16) & 0xFF);
                    int green1 = ((e >> 8) & 0xFF);
                    int blue1 = (e & 0xFF);

                    if((red1 == selection || red1 == selection + 1 || red1 == selection -1) &&
                            (green1 == selection || green1 == selection + 1 || green1 == selection -1) &&
                            (blue1 == selection || blue1 == selection + 1 || blue1 == selection - 1)){
                        newImage.setRGB(i, j, argb);
                    }
                }
            }
            try {
                image = ImageIO.read(new File(outlineFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert image != null;
            int[] imagePixels2 = new int[image.getWidth() * image.getHeight()];
            image.getRGB(0, 0, image.getWidth(), image.getHeight(), imagePixels2, 0, image.getWidth());

            for (int j = 0; j < height; j++) {
                for (int i = 0; i < width; i++) {
                    int e = (imagePixels2[i + j * width]);
                    int alpha1 = (e >> 24) & 0xFF;

                    if(alpha1 > 50){
                        newImage.setRGB(i, j, e);
                    }
                }
            }
            String fName = prefix + "ID_IMAGE_DUMP/" + ev.getAuthor().getIdLong() +".png";
            try {
                ImageIO.write(newImage, "png", new File(fName));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new File(fName);
        }
        return null;
    }
    //SAVED COLOR
    public File setBaseColor(String file, String output, String hex, int selection) {
        if (args[0].equalsIgnoreCase("ei")) {
            int argb = getColor(hex);
            BufferedImage image = null;
            try {
                image = ImageIO.read(new File(file));
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
                    //TODO MEMBRANE
                    int alpha1 = (e >> 24) & 0xFF;
                    int red1 = ((e >> 16) & 0xFF);
                    int green1 = ((e >> 8) & 0xFF);
                    int blue1 = (e & 0xFF);

                    int alphaS = (selection >> 24) & 0xFF;
                    int redS = ((selection >> 16) & 0xFF);
                    int greenS = ((selection >> 8) & 0xFF);
                    int blueS = (selection & 0xFF);

                    boolean rClose = (red1 == redS) || (red1 == redS+1) || (red1 == redS -1);
                    boolean gClose = (green1 == greenS) || (green1 == greenS+1) || (green1 == greenS -1);
                    boolean bClose = (blue1 == blueS) || (blue1 == blueS+1) || (blue1 == blueS -1);

                    if(e == selection || (rClose && gClose && bClose) && alpha1 > 100){
                        newImage.setRGB(i, j, argb);
                    }
                }
            }
            try {
                ImageIO.write(newImage, "png", new File(output));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new File(output);
        }
        return null;
    }
    private File compileImages(File baseFile, String outlineFile, String tribe){
        if (args[0].equalsIgnoreCase("ei")) {
            BufferedImage image = null;
            try {
                image = ImageIO.read(baseFile);
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
                    newImage.setRGB(i, j, e);
                }
            }
            BufferedImage image2 = null;
            try {
                image2 = ImageIO.read(new File(outlineFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert image2 != null;
            int[] imagePixels2 = new int[image2.getWidth() * image2.getHeight()];
            image2.getRGB(0, 0, image.getWidth(), image.getHeight(), imagePixels2, 0, image.getWidth());
//
            for (int j = 0; j < height; j++) {
                for (int i = 0; i < width; i++) {
                    int e = (imagePixels2[i + j * width]);
                    int f = (imagePixels[i+j*width]);
                    //FRONT
                    int alpha1 = (e >> 24) & 0xFF;
                    int red1 = ((e >> 16) & 0xFF);
                    int green1 = ((e >> 8) & 0xFF);
                    int blue1 = (e & 0xFF);

                    //BACK
                    int alpha2 = (f >> 24) & 0xFF;
                    int red2 = ((f >> 16) & 0xFF);
                    int green2 = ((f >> 8) & 0xFF);
                    int blue2 = (f & 0xFF);

                    boolean isBlack = (red1 == 0 && green1 == 0 && blue1 == 0);
                    boolean isWhite = (red1 == 255 && green1 == 255 && blue1 == 255);


                    if(alpha1 > 200)
                        newImage.setRGB(i, j, e);
                    else if(alpha2 > 200 && alpha1 > 128){
//                        System.out.println("a: " + alpha1 + " r: " + red1 + " g: " + green1 + " b: " + blue1);
                        float percentage = alpha1/255f;
                        int red3 = (int)(red1 * (1 - percentage)) + (int)((red2*percentage));
                        int green3 = (int)(green1 * (1 - percentage)) + (int)((green2*percentage));
                        int blue3 = (int)(blue1 * (1 - percentage)) + (int)((blue2*percentage));
//                        System.out.println(red3 + " " + green3 + " " + blue3);
                        int pixel = (0xFF << 24 | ((red3) << 16) | ((green3) << 8) | (blue3));
                        newImage.setRGB(i, j, pixel);
                    }
                    else if (alpha1 < 255 && alpha2 < 20){
                        newImage.setRGB(i, j, e);
                    }
                }
            }
            String output = prefix+tribe+"/"+tribe+"_COMPILED.png";
            try {
                ImageIO.write(newImage, "png", new File(output));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new File(output);
        }
        return null;
    }
    public int getColor(String hex){
        String hex1 = hex;
        if(hex.startsWith("0x")){
            hex1 = hex.substring(2);
        }
        hex1 = "FF" + hex1;
        return Integer.parseUnsignedInt(hex1, 16);
    }
    private boolean isValidHex(String hex){
        if(hex.length() == 6){
            for (int i = 0; i < hex.length(); i++) {
                int c = hex.charAt(i);
                boolean one = (c >= 48 && c <= 57);
                boolean two = (c >= 65 && c <= 70);
                boolean three = (c >= 97 && c <= 102);
                if (!one && !two && !three) {
                    return false;
                }
            }
            return true;
        }
        else if (hex.length() == 8){
            if(hex.startsWith("0x")){
                String hex1 = hex.substring(2);
                for (int i = 0; i < hex1.length(); i++) {
                    int c = hex1.charAt(i);
                    boolean one = (c >= 48 && c <= 57);
                    boolean two = (c >= 65 && c <= 70);
                    boolean three = (c >= 97 && c <= 102);
                    if (!one && !two && !three) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
    private boolean validTribe(String input){
        boolean mudWing = input.equalsIgnoreCase("MudWing") || input.equalsIgnoreCase("Mud");
        boolean seaWing = input.equalsIgnoreCase("SeaWing") || input.equalsIgnoreCase("Sea");
        boolean rainWing = input.equalsIgnoreCase("RainWing") || input.equalsIgnoreCase("Rain");
        boolean nightWing = input.equalsIgnoreCase("NightWing") || input.equalsIgnoreCase("Night");
        boolean sandWing = input.equalsIgnoreCase("SandWing") || input.equalsIgnoreCase("Sand");
        boolean iceWing = input.equalsIgnoreCase("IceWing") || input.equalsIgnoreCase("Ice");
        boolean skyWing = input.equalsIgnoreCase("SkyWing") || input.equalsIgnoreCase("Sky");
        boolean hiveWing = input.equalsIgnoreCase("HiveWing") || input.equalsIgnoreCase("Hive");

        return mudWing || seaWing || rainWing || nightWing || sandWing || iceWing || skyWing || hiveWing;
    }
    private boolean yesOrNo(String answer){
        return answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("n");
    }
    private EmbedBuilder createEmbed(String title, String description, String avatar, String sudoImage, int loop, String[] names, String[] values, int hex){
        EmbedBuilder defined = new EmbedBuilder();
        defined.setAuthor(title);
        defined.setDescription(description);

        if(sudoImage != null)
            defined.setImage("attachment://"+sudoImage);
        if(avatar != null)
            defined.setThumbnail(avatar);
        if(names != null && values != null) {
            for (int i = 0; i < loop; i++)
                defined.addField(names[i], values[i], false);
        }
        defined.setColor(hex);
        return defined;
    }

    private void generateTribe(MessageReceivedEvent event, String tribe){
//        File file = new File(prefix + tribe + "/" + tribe + "_PALETTE_2.png");

        File colorBaseFile = new File(prefix + tribe + "/" + tribe + "_PALETTE_2.png");
        String outlineFile = prefix + tribe + "/" + tribe + "_OUTLINE_2.png";
        File file = compileImages(colorBaseFile, outlineFile, tribe);

        String name = event.getAuthor().getName();
        String des = "`Here is the IceWing base. Are you ready to begin coloring? (y/n)`";
        String URL = event.getAuthor().getAvatarUrl();

        EmbedBuilder emb2 = createEmbed(name, des, URL, tribe + ".png",0, null, null, 0x0000FF);
        assert file != null;
        event.getChannel().sendMessageEmbeds(emb2.build()).addFile(file, tribe + ".png").queue();
        waiter.waitForEvent(MessageReceivedEvent.class, e -> e.getAuthor().equals(event.getAuthor()) && e.getChannel().equals(event.getChannel())
                && yesOrNo(e.getMessage().getContentRaw()), e -> {
            if(e.getMessage().getContentRaw().equalsIgnoreCase("y")){
                setMembrane(e, tribe);
            }
        },  60, TimeUnit.SECONDS, () -> event.getChannel().sendMessageEmbeds(createEmbed(event.getMessage().getAuthor().getName(),
                "`Message has expired`", event.getMessage().getAuthor().getAvatarUrl(), null, 0, null, null, 0xFF0000).build()).queue());
    }

    private void setMembrane(MessageReceivedEvent e, String tribe){
        String name2 = e.getMessage().getAuthor().getName();
        String des2 = "`What color do you want the wing membrane to be? (in hex)`";
        String URL2 = e.getMessage().getAuthor().getAvatarUrl();
        EmbedBuilder emb3 = createEmbed(name2, des2, URL2, tribe + ".png", 0, null, null, 0x0000FF);

        String baseColorFile = prefix + tribe + "/" + tribe + "_PALETTE_2.png";
        String outlineFile = prefix + tribe + "/" + tribe + "_OUTLINE_2.png";
        File membrane = setBaseColor(baseColorFile, prefix+"ID_IMAGE_DUMP/"+e.getMessage().getAuthor().getIdLong()+".png", "FF0000", 0xFF191919);
//        process(e, baseColorFile, outlineFile, "FF0000", 25);
        e.getChannel().sendMessageEmbeds(emb3.build()).addFile(Objects.requireNonNull(compileImages(membrane, outlineFile, tribe)), tribe + ".png").queue();

        waiter.waitForEvent(MessageReceivedEvent.class, f -> f.getAuthor().equals(e.getAuthor()) && f.getChannel().equals(e.getChannel())
                && isValidHex(f.getMessage().getContentRaw()), f -> {
            String name3 = f.getMessage().getAuthor().getName();
            String des3 = "`Is this correct? (y/n)`";
            String URL3 = f.getMessage().getAuthor().getAvatarUrl();
            EmbedBuilder emb4 = createEmbed(name3, des3, URL3, tribe + ".png", 0, null, null, 0x0000FF);

            String input = prefix + "ID_IMAGE_DUMP/" + f.getAuthor().getIdLong() +".png";
            String output = prefix + "ID_IMAGE_DUMP/TEMP_" + f.getAuthor().getIdLong() +".png";
            File temp = setBaseColor(input, output, f.getMessage().getContentRaw(), 0xFFFF0000);
            e.getChannel().sendMessageEmbeds(emb4.build()).addFile(Objects.requireNonNull(compileImages(temp, outlineFile, tribe)), tribe + ".png").queue();

            waiter.waitForEvent(MessageReceivedEvent.class, g -> g.getAuthor().equals(f.getAuthor()) && g.getChannel().equals(f.getChannel())
                    && yesOrNo(g.getMessage().getContentRaw()), g -> {
                if (g.getMessage().getContentRaw().equalsIgnoreCase("y")) {
                    setBaseColor(input, input, f.getMessage().getContentRaw(), 0xFFFF0000);
                    setUnderbellyAndBrow(g, tribe);
                }
                else{
                    setMembrane(g, tribe);
                }
            },  60, TimeUnit.SECONDS, () -> cleanUp(f, f.getMessage().getAuthor().getName(), f.getMessage().getAuthor().getAvatarUrl(), f.getAuthor().getIdLong()));
        },  60, TimeUnit.SECONDS, () -> cleanUp(e, e.getMessage().getAuthor().getName(), e.getMessage().getAuthor().getAvatarUrl(), e.getAuthor().getIdLong()));
    }
    private void setUnderbellyAndBrow(MessageReceivedEvent e, String tribe){
        String name2 = e.getMessage().getAuthor().getName();
        String des2 = "`What color do you want the underbelly and brow to be? (in hex)`";
        String URL2 = e.getMessage().getAuthor().getAvatarUrl();
        EmbedBuilder emb3 = createEmbed(name2, des2, URL2, tribe + ".png", 0, null, null, 0x0000FF);

        String baseColorFile = prefix + "ID_IMAGE_DUMP/" + e.getAuthor().getIdLong() +".png";
        String outlineFile = prefix + tribe + "/" + tribe + "_OUTLINE_2.png";
        File membrane = setBaseColor(baseColorFile, prefix+"ID_IMAGE_DUMP/"+e.getMessage().getAuthor().getIdLong()+".png", "FF0000", 0xFF323232);
        e.getChannel().sendMessageEmbeds(emb3.build()).addFile(Objects.requireNonNull(compileImages(membrane, outlineFile, tribe)), tribe + ".png").queue();

        waiter.waitForEvent(MessageReceivedEvent.class, f -> f.getAuthor().equals(e.getAuthor()) && f.getChannel().equals(e.getChannel())
                && isValidHex(f.getMessage().getContentRaw()), f -> {
            String name3 = f.getMessage().getAuthor().getName();
            String des3 = "`Is this correct? (y/n)`";
            String URL3 = f.getMessage().getAuthor().getAvatarUrl();
            EmbedBuilder emb4 = createEmbed(name3, des3, URL3, tribe + ".png", 0, null, null, 0x0000FF);

            String input = prefix + "ID_IMAGE_DUMP/" + f.getAuthor().getIdLong() +".png";
            String output = prefix + "ID_IMAGE_DUMP/TEMP_" + f.getAuthor().getIdLong() +".png";
            File temp = setBaseColor(input, output, f.getMessage().getContentRaw(), 0xFFFF0000);
            e.getChannel().sendMessageEmbeds(emb4.build()).addFile(Objects.requireNonNull(compileImages(temp, outlineFile, tribe)), tribe + ".png").queue();

            waiter.waitForEvent(MessageReceivedEvent.class, g -> g.getAuthor().equals(f.getAuthor()) && g.getChannel().equals(f.getChannel())
                    && yesOrNo(g.getMessage().getContentRaw()), g -> {
                if (g.getMessage().getContentRaw().equalsIgnoreCase("y")) {
                    setBaseColor(input, input, f.getMessage().getContentRaw(), 0xFFFF0000);
                    setScales(g, tribe);
                }
                else{
                    setUnderbellyAndBrow(g, tribe);
                }
            },  60, TimeUnit.SECONDS, () -> f.getChannel().sendMessageEmbeds(createEmbed(f.getMessage().getAuthor().getName(),
                    "`Message has expired`", f.getMessage().getAuthor().getAvatarUrl(), null, 0, null, null, 0xFF0000).build()).queue());
        },  60, TimeUnit.SECONDS, () -> e.getChannel().sendMessageEmbeds(createEmbed(e.getMessage().getAuthor().getName(),
                "`Message has expired`", e.getMessage().getAuthor().getAvatarUrl(), null, 0, null, null, 0xFF0000).build()).queue());
    }
    private void setScales(MessageReceivedEvent e, String tribe){
        String name2 = e.getMessage().getAuthor().getName();
        String des2 = "`What color do you want the scales to be? (in hex)`";
        String URL2 = e.getMessage().getAuthor().getAvatarUrl();
        EmbedBuilder emb3 = createEmbed(name2, des2, URL2, tribe + ".png", 0, null, null, 0x0000FF);

        String baseColorFile = prefix + "ID_IMAGE_DUMP/" + e.getAuthor().getIdLong() +".png";
        String outlineFile = prefix + tribe + "/" + tribe + "_OUTLINE_2.png";
        File membrane = setBaseColor(baseColorFile, prefix+"ID_IMAGE_DUMP/"+e.getMessage().getAuthor().getIdLong()+".png", "FF0000", 0xFF4B4B4B);
        e.getChannel().sendMessageEmbeds(emb3.build()).addFile(Objects.requireNonNull(compileImages(membrane, outlineFile, tribe)), tribe + ".png").queue();

        waiter.waitForEvent(MessageReceivedEvent.class, f -> f.getAuthor().equals(e.getAuthor()) && f.getChannel().equals(e.getChannel())
                && isValidHex(f.getMessage().getContentRaw()), f -> {
            String name3 = f.getMessage().getAuthor().getName();
            String des3 = "`Is this correct? (y/n)`";
            String URL3 = f.getMessage().getAuthor().getAvatarUrl();
            EmbedBuilder emb4 = createEmbed(name3, des3, URL3, tribe + ".png", 0, null, null, 0x0000FF);

            String input = prefix + "ID_IMAGE_DUMP/" + f.getAuthor().getIdLong() +".png";
            String output = prefix + "ID_IMAGE_DUMP/TEMP_" + f.getAuthor().getIdLong() +".png";
            File temp = setBaseColor(input, output, f.getMessage().getContentRaw(), 0xFFFF0000);
            e.getChannel().sendMessageEmbeds(emb4.build()).addFile(Objects.requireNonNull(compileImages(temp, outlineFile, tribe)), tribe + ".png").queue();

            waiter.waitForEvent(MessageReceivedEvent.class, g -> g.getAuthor().equals(f.getAuthor()) && g.getChannel().equals(f.getChannel())
                    && yesOrNo(g.getMessage().getContentRaw()), g -> {
                if (g.getMessage().getContentRaw().equalsIgnoreCase("y")) {
                    setBaseColor(input, input, f.getMessage().getContentRaw(), 0xFFFF0000);
                    setLegsWingsAndFace(g, tribe);
                }
                else{
                    setScales(g, tribe);
                }
            },  60, TimeUnit.SECONDS, () -> f.getChannel().sendMessageEmbeds(createEmbed(f.getMessage().getAuthor().getName(),
                    "`Message has expired`", f.getMessage().getAuthor().getAvatarUrl(), null, 0, null, null, 0xFF0000).build()).queue());
        },  60, TimeUnit.SECONDS, () -> e.getChannel().sendMessageEmbeds(createEmbed(e.getMessage().getAuthor().getName(),
                "`Message has expired`", e.getMessage().getAuthor().getAvatarUrl(), null, 0, null, null, 0xFF0000).build()).queue());
    }
    private void setLegsWingsAndFace(MessageReceivedEvent e, String tribe){
        String name2 = e.getMessage().getAuthor().getName();
        String des2 = "`What color do you want the legs and face to be? (in hex)`";
        String URL2 = e.getMessage().getAuthor().getAvatarUrl();
        EmbedBuilder emb3 = createEmbed(name2, des2, URL2, tribe + ".png", 0, null, null, 0x0000FF);

        String baseColorFile = prefix + "ID_IMAGE_DUMP/" + e.getAuthor().getIdLong() +".png";
        String outlineFile = prefix + tribe + "/" + tribe + "_OUTLINE_2.png";
        File membrane = setBaseColor(baseColorFile, prefix+"ID_IMAGE_DUMP/"+e.getMessage().getAuthor().getIdLong()+".png", "FF0000", 0xFF646464);
        e.getChannel().sendMessageEmbeds(emb3.build()).addFile(Objects.requireNonNull(compileImages(membrane, outlineFile, tribe)), tribe + ".png").queue();

        waiter.waitForEvent(MessageReceivedEvent.class, f -> f.getAuthor().equals(e.getAuthor()) && f.getChannel().equals(e.getChannel())
                && isValidHex(f.getMessage().getContentRaw()), f -> {
            String name3 = f.getMessage().getAuthor().getName();
            String des3 = "`Is this correct? (y/n)`";
            String URL3 = f.getMessage().getAuthor().getAvatarUrl();
            EmbedBuilder emb4 = createEmbed(name3, des3, URL3, tribe + ".png", 0, null, null, 0x0000FF);

            String input = prefix + "ID_IMAGE_DUMP/" + f.getAuthor().getIdLong() +".png";
            String output = prefix + "ID_IMAGE_DUMP/TEMP_" + f.getAuthor().getIdLong() +".png";
            File temp = setBaseColor(input, output, f.getMessage().getContentRaw(), 0xFFFF0000);
            e.getChannel().sendMessageEmbeds(emb4.build()).addFile(Objects.requireNonNull(compileImages(temp, outlineFile, tribe)), tribe + ".png").queue();

            waiter.waitForEvent(MessageReceivedEvent.class, g -> g.getAuthor().equals(f.getAuthor()) && g.getChannel().equals(f.getChannel())
                    && yesOrNo(g.getMessage().getContentRaw()), g -> {
                if (g.getMessage().getContentRaw().equalsIgnoreCase("y")) {
                    setBaseColor(input, input, f.getMessage().getContentRaw(), 0xFFFF0000);
                    setSpines(g, tribe);
                }
                else{
                    setLegsWingsAndFace(g, tribe);
                }
            },  60, TimeUnit.SECONDS, () -> f.getChannel().sendMessageEmbeds(createEmbed(f.getMessage().getAuthor().getName(),
                    "`Message has expired`", f.getMessage().getAuthor().getAvatarUrl(), null, 0, null, null, 0xFF0000).build()).queue());
        },  60, TimeUnit.SECONDS, () -> e.getChannel().sendMessageEmbeds(createEmbed(e.getMessage().getAuthor().getName(),
                "`Message has expired`", e.getMessage().getAuthor().getAvatarUrl(), null, 0, null, null, 0xFF0000).build()).queue());
    }
    private void setSpines(MessageReceivedEvent e, String tribe){
        String name2 = e.getMessage().getAuthor().getName();
        String des2 = "`What color do you want the spines to be? (in hex)`";
        String URL2 = e.getMessage().getAuthor().getAvatarUrl();
        EmbedBuilder emb3 = createEmbed(name2, des2, URL2, tribe + ".png", 0, null, null, 0x0000FF);

        String baseColorFile = prefix + "ID_IMAGE_DUMP/" + e.getAuthor().getIdLong() +".png";
        String outlineFile = prefix + tribe + "/" + tribe + "_OUTLINE_2.png";
        File membrane = setBaseColor(baseColorFile, prefix+"ID_IMAGE_DUMP/"+e.getMessage().getAuthor().getIdLong()+".png", "FF0000", 0xFF969696);
        e.getChannel().sendMessageEmbeds(emb3.build()).addFile(Objects.requireNonNull(compileImages(membrane, outlineFile, tribe)), tribe + ".png").queue();

        waiter.waitForEvent(MessageReceivedEvent.class, f -> f.getAuthor().equals(e.getAuthor()) && f.getChannel().equals(e.getChannel())
                && isValidHex(f.getMessage().getContentRaw()), f -> {
            String name3 = f.getMessage().getAuthor().getName();
            String des3 = "`Is this correct? (y/n)`";
            String URL3 = f.getMessage().getAuthor().getAvatarUrl();
            EmbedBuilder emb4 = createEmbed(name3, des3, URL3, tribe + ".png", 0, null, null, 0x0000FF);

            String input = prefix + "ID_IMAGE_DUMP/" + f.getAuthor().getIdLong() +".png";
            String output = prefix + "ID_IMAGE_DUMP/TEMP_" + f.getAuthor().getIdLong() +".png";
            File temp = setBaseColor(input, output, f.getMessage().getContentRaw(), 0xFFFF0000);
            e.getChannel().sendMessageEmbeds(emb4.build()).addFile(Objects.requireNonNull(compileImages(temp, outlineFile, tribe)), tribe + ".png").queue();

            waiter.waitForEvent(MessageReceivedEvent.class, g -> g.getAuthor().equals(f.getAuthor()) && g.getChannel().equals(f.getChannel())
                    && yesOrNo(g.getMessage().getContentRaw()), g -> {
                if (g.getMessage().getContentRaw().equalsIgnoreCase("y")) {
                    setBaseColor(input, input, f.getMessage().getContentRaw(), 0xFFFF0000);
                    display(compileImages(new File(input), outlineFile, tribe));
                    deleteFiles(input, output);
                }
                else{
                    setSpines(g, tribe);
                }
            },  60, TimeUnit.SECONDS, () -> f.getChannel().sendMessageEmbeds(createEmbed(f.getMessage().getAuthor().getName(),
                    "`Message has expired`", f.getMessage().getAuthor().getAvatarUrl(), null, 0, null, null, 0xFF0000).build()).queue());
        },  60, TimeUnit.SECONDS, () -> e.getChannel().sendMessageEmbeds(createEmbed(e.getMessage().getAuthor().getName(),
                "`Message has expired`", e.getMessage().getAuthor().getAvatarUrl(), null, 0, null, null, 0xFF0000).build()).queue());
    }

    private void deleteFiles(String file1, String file2){
        try {
            Thread.sleep(100);
            Path p1 = Paths.get(file1);
            Files.delete(p1);
            if(file2 != null) {
                Path p2 = Paths.get(file2);
                Files.delete(p2);
            }
        }
        catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
    }
    private void cleanUp(MessageReceivedEvent e, String title, String avatar, long ID){
        e.getChannel().sendMessageEmbeds(createEmbed(title, "`Message has expired`", avatar, null, 0, null, null, 0xFF0000).build()).queue();
        String input = prefix + "ID_IMAGE_DUMP/" + ID +".png";
        String output = prefix + "ID_IMAGE_DUMP/TEMP_" + ID +".png";
        deleteFiles(input, output);
    }
    private void display(File file){
        event.getChannel().sendMessage("`FINAL RESULT!`").addFile(file).queue();
    }
}
