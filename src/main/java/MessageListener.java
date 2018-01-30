import net.dv8tion.jda.core.EmbedBuilder;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class MessageListener extends ListenerAdapter
{

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        if (event.getAuthor().isBot())
        {
            return;
        }
        Message message = event.getMessage();
        String content = message.getContent().toUpperCase();
        MessageChannel channel = event.getChannel();
        Random rand = new Random();
        if (content.equals("!PING"))
        {
            channel.sendMessage("Pong!").queue();
        } else if (content.startsWith("!BITCOIN"))
        {
            String[] currencyList = {"CHF", "HKD", "ISK", "TWD", "EUR", "DKK", "CLP", "USD", "CAD", "INR", "CNY", "THB", "AUD", "KRW", "SGD", "JPY", "PLN", "GBP", "SEK", "NZD", "BRL", "RUB"};
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet("http://blockchain.info/ticker");
            List<String> currencyArgs = new ArrayList<>(Arrays.asList(content.split(" ")));
            if (!Collections.disjoint(currencyArgs, Arrays.asList(currencyList)) && currencyArgs.size() >= 2)
            {
                try
                {
                    CloseableHttpResponse response1 = httpclient.execute(httpGet);
                    System.out.println(response1.getStatusLine());
                    HttpEntity entity = response1.getEntity();
                    JSONObject jsonResponse = new JSONObject(EntityUtils.toString(entity));
                    JSONObject bitcoinObject = jsonResponse.getJSONObject(currencyArgs.get(1));
                    float currentPrice = bitcoinObject.getInt("last");
                    String currencySymbol = bitcoinObject.getString("symbol");
                    String formattedPrice = String.format("%,.2f", currentPrice);
                    channel.sendMessage("Current Price in " + currencyArgs.get(1) + ": " + currencySymbol + formattedPrice).queue();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            } else
            {
                if (Collections.disjoint(currencyArgs, Arrays.asList(currencyList)))
                {
                    channel.sendMessage("Please use a valid currency \n CHF, HKD, ISK, TWD, EUR, DKK, CLP, USD, CAD, INR, CNY, THB, AUD, KRW, SGD, JPY, PLN, GBP, SEK, NZD, BRL, RUB").queue();
                } else if (currencyArgs.size() == 1)
                {
                    try
                    {
                        CloseableHttpResponse response1 = httpclient.execute(httpGet);
                        System.out.println(response1.getStatusLine());
                        HttpEntity entity = response1.getEntity();
                        JSONObject jsonResponse = new JSONObject(EntityUtils.toString(entity));
                        JSONObject bitcoinObject = jsonResponse.getJSONObject("USD");
                        float currentPrice = bitcoinObject.getInt("last");
                        String formattedPrice = String.format("%,.2f", currentPrice);
                        channel.sendMessage("Current Price in USD: $" + formattedPrice).queue();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                }
            }
        } else if (content.startsWith("!ROLL"))
        {
            EmbedBuilder eb;
            int roll;
            List<String> modifierArgs = new ArrayList<>(Arrays.asList(content.split(" ")));
            int diceSize = Integer.valueOf(modifierArgs.get(1));
            System.out.println(modifierArgs.size());
            if (modifierArgs.size() == 3)
            {
                roll = rand.nextInt(diceSize) + 1; //This results in 1 - 6 (instead of 0 - 5)
                System.out.println(modifierArgs.get(1));
                System.out.println(roll);
                String modifier = modifierArgs.get(2);
                System.out.println(modifier);
                String amount = modifier.substring(1);
                int preModifier = roll;
                if (modifier.charAt(0) == '+')
                {
                    roll = roll + Integer.valueOf(amount);
                } else
                {
                    roll = roll - Integer.valueOf(amount);
                }
//                if (roll >= diceSize)
//                {
//                    roll = diceSize;
//                }
                if (roll <= 1)
                {
                    roll = 1;
                }
                eb = new EmbedBuilder()
                        .setTitle("Roll")
                        .setColor(new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)))
                        .setDescription("Original Roll: " + preModifier + "\nModifier: " + modifierArgs.get(2) + "\nFinal Roll: " + roll);
            } else
            {
                roll = rand.nextInt(diceSize) + 1; //This results in 1 - 6 (instead of 0 - 5)
                eb = new EmbedBuilder()
                        .setTitle("Roll")
                        .setColor(new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)))
                        .setDescription("Roll: " + roll);
            }
            channel.sendMessage(eb.build()).queue();
        } else if (content.startsWith("!DEFINE"))
        {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            List<String> defineArgs = new ArrayList<>(Arrays.asList(content.split(" ")));
            event.getMessage().delete().queue();
            if (defineArgs.size() == 1)
            {
                channel.sendMessage("Please enter a word to define").queue();
                return;
            }
            HttpGet httpGet = new HttpGet("http://api.urbandictionary.com/v0/define?term=" + defineArgs.get(1));
            try
            {
                CloseableHttpResponse response1 = httpclient.execute(httpGet);
                System.out.println(response1.getStatusLine());
                HttpEntity entity = response1.getEntity();
                JSONObject jsonResponse = new JSONObject(EntityUtils.toString(entity));
                if (jsonResponse.getString("result_type").equals("exact"))
                {
                    JSONArray definitionList = jsonResponse.getJSONArray("list");
                    JSONObject listObject = definitionList.getJSONObject(0);
                    EmbedBuilder eb = new EmbedBuilder()
                            .setTitle("Definition of: " + listObject.getString("word"), listObject.getString("permalink"))
                            .setColor(new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)))
                            .setDescription(listObject.getString("definition"))
//                            .setThumbnail("https://pbs.twimg.com/profile_images/838627383057920000/m5vutv9g.jpg")
                            .setAuthor("UrbanDictionary", "http://www.urbandictionary.com", "http://is1.mzstatic.com/image/thumb/Purple118/v4/69/92/73/69927356-136b-b772-3bda-fca77248cdd0/source/175x175bb.jpg");
                    channel.sendMessage(eb.build()).queue();
                } else
                {
                    EmbedBuilder eb = new EmbedBuilder()
                            .setTitle("Definition of: " + defineArgs.get(1))
                            .setColor(new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)))
                            .setDescription("Word does not have a meaning on UrbanDictionary.com")
//                            .setThumbnail("https://pbs.twimg.com/profile_images/838627383057920000/m5vutv9g.jpg")
                            .setAuthor("UrbanDictionary", "http://www.urbandictionary.com", "http://is1.mzstatic.com/image/thumb/Purple118/v4/69/92/73/69927356-136b-b772-3bda-fca77248cdd0/source/175x175bb.jpg");
                    channel.sendMessage(eb.build()).queue();
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private String getCryptoPrice()
    {
        return null;
    }
}
