package bentre.ditagis.com.capnhatthongtin.utities;

import android.content.Context;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import bentre.ditagis.com.capnhatthongtin.R;
import bentre.ditagis.com.capnhatthongtin.adapter.ThongKeAdapter;

/**
 * Created by NGUYEN HONG on 4/26/2018.
 */

public class TimePeriodReport {
    private Calendar calendar;
    private Date today;
    private List<ThongKeAdapter.Item> items;
    private Context mContext;

    public TimePeriodReport(Context context) {
        mContext = context;
        today = new Date();
        calendar = Calendar.getInstance();
        items = new ArrayList<>();
        items.add(new ThongKeAdapter.Item(1, "Tất cả", null, null, null));
        items.add(new ThongKeAdapter.Item(2, "Tháng này", formatTimeToGMT(getFirstDayofMonth()), formatTimeToGMT(getLastDayofMonth()), dayToFirstDayString(getFirstDayofMonth(), getLastDayofMonth())));
        items.add(new ThongKeAdapter.Item(3, "Tháng trước", formatTimeToGMT(getFirstDayofLastMonth()), formatTimeToGMT(getLastDayofLastMonth()), dayToFirstDayString(getFirstDayofLastMonth(), getLastDayofLastMonth())));
        items.add(new ThongKeAdapter.Item(4, "3 tháng gần nhất", formatTimeToGMT(getFirstDayofLast3Months()), formatTimeToGMT(getLastDayofLast3Months()), dayToFirstDayString(getFirstDayofLast3Months(), getLastDayofLast3Months())));
        items.add(new ThongKeAdapter.Item(5, "6 tháng gần nhất", formatTimeToGMT(getFirstDayofLast6Months()), formatTimeToGMT(getLastDayofLast6Months()), dayToFirstDayString(getFirstDayofLast6Months(), getLastDayofLast6Months())));
        items.add(new ThongKeAdapter.Item(6, "Năm nay", formatTimeToGMT(getFirstDayofYear()), formatTimeToGMT(getLastDayofYear()), dayToFirstDayString(getFirstDayofYear(), getLastDayofYear())));
        items.add(new ThongKeAdapter.Item(7, "Năm trước", formatTimeToGMT(getFirstDayoflLastYear()), formatTimeToGMT(getLastDayofLastYear()), dayToFirstDayString(getFirstDayoflLastYear(), getLastDayofLastYear())));
        items.add(new ThongKeAdapter.Item(8, "Tùy chỉnh", null, null, "-- - --"));
    }

    public List<ThongKeAdapter.Item> getItems() {
        return items;
    }

    public void setItems(List<ThongKeAdapter.Item> items) {
        this.items = items;
    }
    private String formatTimeToGMT(Date date){
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat(mContext.getString(R.string.format_day_yearfirst));
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormatGmt.format(date);
    }
    private String dayToFirstDayString(Date firstDate, Date lastDate) {
        return (String) DateFormat.format(mContext.getString(R.string.format_time_day_month_year), firstDate) + " - " + (String) DateFormat.format(mContext.getString(R.string.format_time_day_month_year), lastDate);
    }

    private Date getFirstDayofMonth() {
        resetToday();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    private Date getLastDayofMonth() {
        getActualMaximumToday();
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
    }

    private Date getFirstDayofLastMonth() {
        resetToday();
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    private Date getLastDayofLastMonth() {
        getActualMaximumToday();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
    }

    private Date getFirstDayofLast3Months() {
        resetToday();
        calendar.add(Calendar.MONTH, -2);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    private Date getLastDayofLast3Months() {
        getActualMaximumToday();
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
    }

    private Date getFirstDayofLast6Months() {
        resetToday();
        calendar.add(Calendar.MONTH, -5);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    private Date getLastDayofLast6Months() {
        getActualMaximumToday();
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
    }

    private Date getFirstDayofYear() {
        resetToday();
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTime();
    }

    private Date getLastDayofYear() {
        getActualMaximumToday();
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        calendar.set(Calendar.MONTH, 11);
        return calendar.getTime();
    }

    private Date getFirstDayoflLastYear() {
        resetToday();
        calendar.add(Calendar.YEAR, -1);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTime();
    }

    private Date getLastDayofLastYear() {
        getActualMaximumToday();
        calendar.add(Calendar.YEAR, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        calendar.set(Calendar.MONTH, 11);
        return calendar.getTime();
    }
    private void resetToday(){
        calendar.setTime(today);
        calendar.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
    }
    private void getActualMaximumToday(){
        calendar.setTime(today);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND,999);
    }
}
