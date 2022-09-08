package Commands;

import Main.MessageListener;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

public class Fandom {
    public Fandom(String[]args, MessageReceivedEvent event){
        if(args[0].equals("fandom")) {
            if(args.length > 2) {
                if (args[1].equals("set")) {
                    TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
                    };

                    try {
                        // Install the all-trusting trust manager
                        SSLContext sc = SSLContext.getInstance("SSL");
                        sc.init(null, trustAllCerts, new java.security.SecureRandom());
                        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                    } catch (NoSuchAlgorithmException | KeyManagementException e) {
                        e.printStackTrace();
                    }

                    // Create all-trusting host name verifier
                    HostnameVerifier allHostsValid = (hostname, session) -> true;
                    // Install the all-trusting host verifier
                    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

                    Document doc = null;
                    MessageListener.fandomName = "https://www." + args[2] + ".fandom.com/wiki/";
                    try {
                        doc = Jsoup.connect(MessageListener.fandomName).get();
                    } catch (IOException e) {
                        MessageListener.fandomName = null;
                        event.getChannel().sendMessage("`Invalid Fandom Name`").queue();
                        return;
                    }
                    event.getChannel().sendMessage("`Fandom is now: " + MessageListener.fandomName + "`").queue();
                }
                if (args[1].equals("fetch")) {

                    Document doc = null;
                    String newLink = "";
                    String concat = "";
                    for(int i = 2; i < args.length; i++){
                        if(i < args.length-1)
                            concat += args[i] + "_";
                        else if (i == args.length-1)
                            concat += args[i];
                    }
                    newLink = MessageListener.fandomName + concat;
                    newLink = newLink.replace("https", "http");
                    try {
                        doc = Jsoup.connect(newLink).get();
                    } catch (IOException e) {
                        String linky = MessageListener.fandomName + "Special:Search?query=" + args[2] + "&scope=internal&navigationSearch=true";
                        try {
                            doc = Jsoup.connect(linky).get();
                            String els = doc.getElementsByClass("unified-search__result").get(0).getElementsByTag("a").get(0).attr("href");
                            event.getChannel().sendMessage(els).queue();
                        } catch (IOException f) {
                            f.printStackTrace();
                            event.getChannel().sendMessage("`Invalid Article Link`").queue();
                        }
                        return;
                    }
                    event.getChannel().sendMessage(newLink).queue();
                }
            }
        }
    }
}
