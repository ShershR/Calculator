package my.candlecalcualtor;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@RequiredArgsConstructor
public class Application {

    public static void main(String[] args) {
        String token = System.getenv("TELEGRAM_TOKEN");
        if (token == null) {
            throw new IllegalArgumentException("Telegram token is missing!");
        }
       // SpringApplication.run(Application.class, args);
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new CandleCalculatorBot(token));
            System.out.println("Бот запущен!");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}