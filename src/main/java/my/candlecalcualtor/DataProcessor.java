package my.candlecalcualtor;

import io.micrometer.common.util.StringUtils;
import my.candlecalcualtor.api.BybitApiService;
import my.candlecalcualtor.model.Candle;
import my.candlecalcualtor.model.Period;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataProcessor {

    private BybitApiService bybitApiService;

    private final String symbol = "ARBUSDT";
    private final String interval = "D";

    private boolean fullLog = true;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public DataProcessor() {
        this.bybitApiService = new BybitApiService();
    }

    // Метод для генерации массива X (column1 или column2)
    public List<Integer> generateX1Array(String pairSymbol) {
        // Периоды для первого массива (Column 1)
        List<Period> column1Periods = new ArrayList<>();
        for (int i = 10; i >= 5; i--) {
            column1Periods.add(
                    new Period(
                            LocalDate.now(ZoneId.of("GMT")).minusDays(i),
                            LocalDate.now(ZoneId.of("GMT")).minusDays(i - 4)
                    )
            );
        }

        // Заполнение данных для Column 1
        List<Integer> column1Results = new ArrayList<>();
        for (Period period : column1Periods) {
            System.out.print("Период 1, дата начала: "
                    + period.getStartDate().format(formatter) +
                    ", дата окончания: " + period.getEndDate().format(formatter));
            String sym = StringUtils.isEmpty(pairSymbol) ? symbol : pairSymbol;
            List<Candle> candles = bybitApiService.getCandles(sym, interval, period);
            int greenCount = (int) candles.stream().filter(Candle::isGreen).count();
            System.out.println(" ---- " + greenCount);
            column1Results.add(greenCount);
        }

        // Вывод результатов
       // System.out.println("Column 1: " + column1Results);
        return column1Results;
    }

    public List<Integer> generateX2Array(String pairSymbol) {
        // Периоды для второго массива (Column 2)
        List<Period> column2Periods = new ArrayList<>();
        for (int i = 17; i >= 12; i--) {
            column2Periods.add(
                    new Period(
                            LocalDate.now(ZoneId.of("GMT")).minusDays(i),
                            LocalDate.now(ZoneId.of("GMT")).minusDays(i - 11)
                    )
            );
        }

        // Заполнение данных для Column 2
        List<Integer> column2Results = new ArrayList<>();
        for (Period period : column2Periods) {
            System.out.print("Период 2, дата начала: "
                    + period.getStartDate().format(formatter)
                    + ", дата окончания: " + period.getEndDate().format(formatter));
            String sym = StringUtils.isEmpty(pairSymbol) ? symbol : pairSymbol;
            List<Candle> candles = bybitApiService.getCandles(sym, interval, period);
            int redCount = (int) candles.stream().filter(Candle::isRed).count();
            System.out.println(" ---- " + redCount);
            column2Results.add(redCount);
        }
        //System.out.println("Column 2: " + column2Results);
        return column2Results;
    }

    // Метод для построения Y-массива (Y1 или Y2)
    public static List<String> buildYArray(List<Integer> xArray) {
        List<String> yArray = new ArrayList<>();

        for (int i = 0; i < xArray.size() - 1; i++) {
            if (xArray.get(i).equals(xArray.get(i + 1))) {
                yArray.add("-"); // Если равны, записываем "-"
            } else {
                yArray.add("+"); // Если не равны, записываем "+"
            }
        }
        return yArray;
    }

    public static int[][] generateMatrix(List<String> Y1, List<String> Y2) {
        if (Y1.size() != 5 || Y2.size() != 5) {
            throw new IllegalArgumentException("Both Y1 and Y2 must have exactly 5 elements.");
        }

        int[][] matrix = new int[4][6];

        int[][] instructions = {
                // Column 1
                {0, 1}, // 1*1: Y1[2] and Y2[1]
                {1, 0}, // 2*1: Y1[1] and Y2[2]
                {0, 0}, // 3*1: Y1[2] and Y2[2]
                {1, 1}, // 4*1: Y1[1] and Y2[1]
                {0, 1}, // 1*1: Y1[2] and Y2[1]
                {1, 0}, // 2*1: Y1[1] and Y2[2]

                // Column 2
                {1, 2}, // 1*2: Y1[3] and Y2[2]
                {2, 1}, // 2*2: Y1[2] and Y2[3]
                {1, 1}, // 3*2: Y1[3] and Y2[3]
                {2, 2}, // 4*2: Y1[2] and Y2[2]
                {1, 2}, // 1*2: Y1[3] and Y2[2]
                {2, 1}, // 2*2: Y1[2] and Y2[3]

                // Column 3
                {2, 3}, // 1*3: Y1[4] and Y2[3]
                {3, 2}, // 2*3: Y1[3] and Y2[4]
                {2, 2}, // 3*3: Y1[4] and Y2[4]
                {3, 3}, // 4*3: Y1[3] and Y2[3]
                {2, 3}, // 1*3: Y1[4] and Y2[3]
                {3, 2}, // 2*3: Y1[3] and Y2[4]

                // Column 4
                {3, 4}, // 1*4: Y1[5] and Y2[4]
                {4, 3}, // 2*4: Y1[4] and Y2[5]
                {3, 3}, // 3*4: Y1[5] and Y2[5]
                {4, 4},  // 4*4: Y1[4] and Y2[4]
                {3, 4}, // 1*4: Y1[5] and Y2[4]
                {4, 3}, // 2*4: Y1[4] and Y2[5]
        };
        int i = 0;
        int j = 0;
        for (int[] pair : instructions) {
            matrix[i][j] = countOccurrences(
                    Y1.get(pair[0]),
                    Y2.get(pair[1]
                    ),
                    j % 2 == 0 ? "+" : "-"); // 1*1
            j++;
            if (j == 6) {
                j = 0;
                i++;
            }
        }
/*
        // Filling the matrix based on the instructions
        matrix[0][0] = countOccurrences(Y1.get(0), Y2.get(1), "+"); // 1*1
        matrix[1][0] = countOccurrences(Y1.get(1), Y2.get(0), "-"); // 2*1
        matrix[2][0] = countOccurrences(Y1.get(0), Y2.get(0), "+"); // 3*1
        matrix[3][0] = countOccurrences(Y1.get(1), Y2.get(1), "-"); // 4.get(1)

        matrix[0][1] = countOccurrences(Y1.get(1), Y2.get(2), "+"); // 1*2
        matrix[1][1] = countOccurrences(Y1.get(2), Y2.get(1), "-"); // 2*2
        matrix[2][1] = countOccurrences(Y1.get(1), Y2.get(1), "+"); // 3*2
        matrix[3][1] = countOccurrences(Y1.get(2), Y2.get(2), "-"); // 4.get(2)

        matrix[0][2] = countOccurrences(Y1.get(2), Y2.get(3), "+"); // 1*3
        matrix[1][2] = countOccurrences(Y1.get(3), Y2.get(2), "-"); // 2*3
        matrix[2][2] = countOccurrences(Y1.get(2), Y2.get(2), "+"); // 3*3
        matrix[3][2] = countOccurrences(Y1.get(3), Y2.get(3), "-"); // 4.get(3)

        matrix[0][3] = countOccurrences(Y1.get(3), Y2.get(4), "+"); // 1*4
        matrix[1][3] = countOccurrences(Y1.get(4), Y2.get(3), "-"); // 2*4
        matrix[2][3] = countOccurrences(Y1.get(3), Y2.get(3), "+"); // 3*4
        matrix[3][3] = countOccurrences(Y1.get(4), Y2.get(4), "-"); // 4*4*/

        return matrix;
    }

    private static int countOccurrences(String element1, String element2, String target) {
        int count = 0;

        if (element1.equals(target)) {
            count++;
        }

        if (element2.equals(target)) {
            count++;
        }

        return count;
    }

    public static List<Integer> generateMassive4(int[][] table) {
        List<Integer> massive4 = new ArrayList<>();

        for (int row = 0; row < 4; row++) {
            int sum = 0;
            for (int col = 0; col < 6; col++) {
                sum += table[row][col];
            }
            massive4.add(sum);
        }

        return massive4;
    }

    public static int[][] generateTable5(List<String> Y1, List<String> Y2) {
        int[][] table5 = new int[4][6];
        int[][] instructions = {
                // Column 1
                {1, 0}, // 1*1: Y1[2] and Y2[1]
                {0, 1}, // 2*1: Y1[1] and Y2[2]
                {1, 1}, // 3*1: Y1[2] and Y2[2]
                {0, 0}, // 4*1: Y1[1] and Y2[1]
                {1, 0}, // 1*1: Y1[2] and Y2[1]
                {0, 1}, // 2*1: Y1[1] and Y2[2]

                // Column 2
                {2, 1}, // 1*2: Y1[3] and Y2[2]
                {1, 2}, // 2*2: Y1[2] and Y2[3]
                {2, 2}, // 3*2: Y1[3] and Y2[3]
                {1, 1}, // 4*2: Y1[2] and Y2[2]
                {2, 1}, // 1*2: Y1[3] and Y2[2]
                {1, 2}, // 2*2: Y1[2] and Y2[3]

                // Column 3
                {3, 2}, // 1*3: Y1[4] and Y2[3]
                {2, 3}, // 2*3: Y1[3] and Y2[4]
                {3, 3}, // 3*3: Y1[4] and Y2[4]
                {2, 2}, // 4*3: Y1[3] and Y2[3]
                {3, 2}, // 1*3: Y1[4] and Y2[3]
                {2, 3}, // 2*3: Y1[3] and Y2[4]

                // Column 4
                {4, 3}, // 1*4: Y1[5] and Y2[4]
                {3, 4}, // 2*4: Y1[4] and Y2[5]
                {4, 4}, // 3*4: Y1[5] and Y2[5]
                {3, 3},  // 4*4: Y1[4] and Y2[4]
                {4, 3}, // 1*4: Y1[5] and Y2[4]
                {3, 4}, // 2*4: Y1[4] and Y2[5]
        };


        int i = 0;
        int j = 0;
        for (int[] pair : instructions) {
            table5[i][j] = countOccurrences(Y1.get(pair[0]), Y2.get(pair[1]), j % 2 == 0 ? "+" : "-"); // 1*1
            j++;
            if (j == 6) {
                j = 0;
                i++;
            }
        }
        return table5;
    }

    private static boolean getResult(List<Integer> massive1, List<Integer> massive2) {
        Collections.reverse(massive2);
        return massive1.equals(massive2);
    }

    public String calculateCandles(String pairSymbol) {
        StringBuilder string = new StringBuilder();
        log("Пара: " + (StringUtils.isEmpty(pairSymbol) ? symbol : pairSymbol) + "\n", string);
        List<Integer> X1 = generateX1Array(pairSymbol);
        log("\n", string);
        List<Integer> X2 = generateX2Array(pairSymbol);

        for (int i = 0; i < 6; i++) {
            log(X1.get(i) + " " + X2.get(i) + "\n", string);
        }

        List<String> Y1 = buildYArray(X1);
        List<String> Y2 = buildYArray(X2);

        log("\nТаблица 2:", string);
        log("\n", string);
        for (int i = 0; i < 5; i++) {
            log(Y1.get(i) + " " + Y2.get(i) + "\n", string);
        }


        int[][] table3 = generateMatrix(Y1, Y2);
        log("\nТаблица 3:\n", string);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 6; j++) {
                log(table3[i][j] + " ", string);
            }
            log("\n", string);
        }

        List<Integer> massive4 = generateMassive4(table3);
        log("\nЭтап 4: \n" + massive4 + "\n", string);
        log("\nЭтап 5: \n", string);
        System.out.println();
        int[][] table5 = generateTable5(Y1, Y2);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 6; j++) {
                log(table5[i][j] + " ", string);
            }
            log("\n", string);
        }

        List<Integer> massive5 = generateMassive4(table5);
        log("\nЭтап 6: \n" + massive5 + "\n", string);

        log("\nЭтап 7:", string);
        for (int i = 0; i < 4; i++) {
            log("\n" + massive4.get(i) + " " + massive5.get(i), string);
        }
        log("\n\n", string);

        String result = getResult(massive4, massive5) ? "Сигнал!!!" : "Нет сигнала";

        string.append(result);
        return string.toString();
    }

    private void log(String log, StringBuilder string) {
        System.out.print(log);
        if (fullLog) {
            string.append(log);
        }
    }

    public void switchFullLog(boolean enable) {
        fullLog = enable;
    }
}
