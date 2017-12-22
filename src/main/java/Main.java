import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;

public class Main {
    public static void main(String[] args) {
        try {
            JDA api = new JDABuilder(AccountType.BOT).setToken(Token.getDiscordBotToken()).buildAsync();
            api.addEventListener(new MessageListener());
        } catch (LoginException | RateLimitedException e) {
            e.printStackTrace();
        }
    }
}
