package my.candlecalcualtor.model;

public class Candle {
    private double open;
    private double close;

    public Candle(double open, double close) {
        this.open = open;
        this.close = close;
    }

    public boolean isGreen() {
        return close > open;
    }

    public boolean isRed() {
        return close < open;
    }
}
