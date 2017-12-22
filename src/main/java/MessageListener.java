import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

public class MessageListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        Message message = event.getMessage();
        String content = message.getContent().toUpperCase();
        if (content.equals("!PING")) {
            MessageChannel channel = event.getChannel();
            channel.sendMessage("Pong!").queue();
        } else if (content.startsWith("!BITCOIN")) {

            String[] currencyList = {"CHF", "HKD", "ISK", "TWD", "EUR", "DKK", "CLP", "USD", "CAD", "INR", "CNY", "THB", "AUD", "KRW", "SGD", "JPY", "PLN", "GBP", "SEK", "NZD", "BRL", "RUB"};
            MessageChannel channel = event.getChannel();
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet("http://blockchain.info/ticker");
            List<String> currencyArgs = new ArrayList<>(Arrays.asList(content.split(" ")));
            if (!Collections.disjoint(currencyArgs, Arrays.asList(currencyList)) && currencyArgs.size() >= 2) {
                try {
                    CloseableHttpResponse response1 = httpclient.execute(httpGet);
                    System.out.println(response1.getStatusLine());
                    HttpEntity entity = response1.getEntity();
                    JSONObject jsonResponse = new JSONObject(EntityUtils.toString(entity));
                    JSONObject bitcoinObject = jsonResponse.getJSONObject(currencyArgs.get(1));
                    float currentPrice = bitcoinObject.getInt("last");
                    String currencySymbol = bitcoinObject.getString("symbol");
                    String formattedPrice = String.format("%,.2f", currentPrice);
                    channel.sendMessage("Current Price in " + currencyArgs.get(1) + ": " + currencySymbol  + formattedPrice).queue();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                if (Collections.disjoint(currencyArgs, Arrays.asList(currencyList))) {
                    channel.sendMessage("Please use a valid currency \n CHF, HKD, ISK, TWD, EUR, DKK, CLP, USD, CAD, INR, CNY, THB, AUD, KRW, SGD, JPY, PLN, GBP, SEK, NZD, BRL, RUB").queue();
                } else if(currencyArgs.size()==1) {
                    try {
                        CloseableHttpResponse response1 = httpclient.execute(httpGet);
                        System.out.println(response1.getStatusLine());
                        HttpEntity entity = response1.getEntity();
                        JSONObject jsonResponse = new JSONObject(EntityUtils.toString(entity));
                        JSONObject bitcoinObject = jsonResponse.getJSONObject("USD");
                        float currentPrice = bitcoinObject.getInt("last");
                        String formattedPrice = String.format("%,.2f", currentPrice);
                        channel.sendMessage("Current Price in USD: $" + formattedPrice).queue();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        } else if (content.startsWith("!meme")) {
            //pass
        }
    }
}
