package my.candlecalcualtor.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import my.candlecalcualtor.model.Candle;
import my.candlecalcualtor.model.Period;
import org.json.JSONArray;
import org.json.JSONObject;

public class BybitApiService {

    private static final String BASE_URL = "https://api.bybit.com/v5/market/kline";

    public List<Candle> getCandles(String symbol, String interval, Period period) {
        List<Candle> candles = new ArrayList<>();
        try {
            String url = String.format(
                    "%s?category=spot&symbol=%s&interval=%s&start=%d&end=%d",
                    BASE_URL, symbol, interval, period.getStartTimestamp(), period.getEndTimestamp()
            );

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                // Обработка JSON
                JSONObject jsonResponse = new JSONObject(response.toString());
             //   System.out.println(response);
                JSONArray klineData = jsonResponse.getJSONObject("result").getJSONArray("list");

                for (int i = 0; i < klineData.length(); i++) {
                    JSONArray candleData = klineData.getJSONArray(i);
                    double open = candleData.getDouble(1);
                    double close = candleData.getDouble(4);
                    candles.add(new Candle(open, close));
                }
            } else {
                System.out.println("HTTP Error: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return candles;
    }
}