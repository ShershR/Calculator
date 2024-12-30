package my.candlecalcualtor;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class CandleCalculatorBot extends TelegramLongPollingBot {

    private DataProcessor dataProcessor;

    private String token;

    public CandleCalculatorBot(String token) {
        this.token = token;
        this.dataProcessor = new DataProcessor();
    }

    public CandleCalculatorBot() {
        this.dataProcessor = new DataProcessor();
    }

    @Override
    public String getBotUsername() {
        return "CandleCalculatorBot";
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String userMessage = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();
            String[] splitedText = userMessage.split(" ");

            switch (splitedText[0]) {
                case "/calculate" -> {
                    sendResponse(chatId, "Ожидай, идет рассчет...");
                    String result = dataProcessor.calculateCandles(null);
                    sendResponse(chatId, result);
                }
                case "/calculate_pair" -> {
                    if (splitedText.length < 2) {
                        sendResponse(chatId, "Пара не задана");
                    } else {
                        sendResponse(chatId, "Ожидай, идет рассчет...");
                        String result = dataProcessor.calculateCandles(splitedText[1]);
                        sendResponse(chatId, result);
                    }
                }
                case "/enable_full_log" -> {
                    dataProcessor.switchFullLog(true);
                    sendResponse(chatId, "Лог включен");
                }
                case "/disable_full_log" -> {
                    dataProcessor.switchFullLog(false);
                    sendResponse(chatId, "Лог выключен");
                }
                case "/start" -> sendResponse(chatId,
                        "Используй команду /calculate для расчета пары ARBUSDT. " +
                                "Для включения полного лога - /enable_full_log. " +
                                "Для выключения полного лога - /disable_full_log");
                default -> sendResponse(chatId,"Я понимаю только команду /calculate");
            }
        }
    }

    private void sendResponse(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
