package my.candlecalcualtor.model;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Period {
    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    private LocalDate startDate;
    private LocalDate endDate;

    public Period(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public long getStartTimestamp() {
        /*Long time = startDate.atStartOfDay().toInstant(ZoneOffset.MIN).toEpochMilli();
        System.out.println(new Date(time));*/
        /*Date date = new Date();
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        date = cal.getTime();
        System.out.println(new Date(date.getTime()));
        return date.getTime();*/
        return startDate.atStartOfDay(ZoneId.of("GMT")).toInstant().toEpochMilli();
    }

    public long getEndTimestamp() {
        return endDate.atStartOfDay(ZoneId.of("GMT")).toInstant().toEpochMilli();
    }
}
