package me.zayz.socialplus.utils;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by zayz on 11/13/17.
 * <p>
 * Date util static class
 */

public class DateUtil {

    /**
     * Gets time in relation to today.
     *
     * @param epochTimeString Epoch time
     * @return Time away from today
     */
    public static String getTimeFromToday(String epochTimeString) {

        long epochTime = Long.parseLong(epochTimeString);
        TimeZone timezone = TimeZone.getTimeZone("America/Los_Angeles");
        Calendar compareCalendar = Calendar.getInstance(timezone);
        Calendar todayCalendar = Calendar.getInstance(timezone);
        compareCalendar.setTimeInMillis(epochTime);

        DateObject compareDate = new DateObject(compareCalendar);
        DateObject today = new DateObject(todayCalendar);

        return today.compareFrom(compareDate);
    }

    /**
     * Checks if given time is today.
     *
     * @param epochTimeString Epoch time
     * @return If given time is today
     */
    public static boolean isToday(String epochTimeString) {

        String result = getTimeFromToday(epochTimeString);

        return result.equals("Today");
    }

    /**
     * Proprietary date object
     */
    private static class DateObject {

        /**
         * Enum to hold days of week in relation to number
         */
        private enum Weekday {
            SUNDAY("Sunday", 1),
            MONDAY("Monday", 2),
            TUESDAY("Tuesday", 3),
            WEDNESDAY("Wednesday", 4),
            THURSDAY("Thursday", 5),
            FRIDAY("Friday", 6),
            SATURDAY("Saturday", 7);

            public String name;
            public int value;

            Weekday(String name, int value) {

                this.name = name;
                this.value = value;
            }
        }

        int year;
        int month;
        int dayOfMonth;
        int dayOfWeek;

        DateObject(Calendar date) {

            this.year = date.get(Calendar.YEAR);
            this.month = date.get(Calendar.MONTH);
            this.dayOfMonth = date.get(Calendar.DAY_OF_MONTH);
            this.dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
        }

        /**
         * Compares to another date.
         *
         * @param previousDate Previous date
         * @return Difference from given date
         */
        String compareFrom(DateObject previousDate) {

            if (year > previousDate.year) {
                int yearsAgoValue = year - previousDate.year;
                String yearsAgo = String.valueOf(yearsAgoValue);
                return yearsAgo + (yearsAgoValue == 1 ? " year ago" : " years ago");
            }

            if (month > previousDate.month) {
                int monthsAgoValue = month - previousDate.month;
                String monthsAgo = String.valueOf(monthsAgoValue);
                return monthsAgo + (monthsAgoValue == 1 ? " month ago" : " months ago");
            }

            if (dayOfMonth - previousDate.dayOfMonth > 7) {
                int daysAgoValue = dayOfMonth - previousDate.dayOfMonth;
                String daysAgo = String.valueOf(daysAgoValue);
                return daysAgo + (daysAgoValue == 1 ? " day ago" : " days ago");
            }

            if (dayOfMonth - previousDate.dayOfMonth <= 7 &&
                    dayOfMonth - previousDate.dayOfMonth > 1) {
                Weekday[] weekdays = Weekday.values();

                for (Weekday weekday : weekdays) {
                    if (weekday.value == previousDate.dayOfWeek) {
                        return weekday.name;
                    }
                }
            }

            if (dayOfMonth - previousDate.dayOfMonth == 1) {
                return "Yesterday";
            }

            return "Today";
        }
    }
}
