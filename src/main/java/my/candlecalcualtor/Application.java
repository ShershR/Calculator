package my.candlecalcualtor;

import lombok.RequiredArgsConstructor;
import my.candlecalcualtor.api.BybitApiService;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

//@SpringBootApplication
@RequiredArgsConstructor
public class Application {

    private final BybitApiService bybitService;

    public static void main(String[] args) {
       // SpringApplication.run(Application.class, args);
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new CandleCalculatorBot());
            System.out.println("Бот запущен!");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}