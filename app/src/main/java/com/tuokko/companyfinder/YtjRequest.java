package com.tuokko.companyfinder;

import android.support.v4.content.res.TypedArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class YtjRequest {
    private final static String CLASS_NAME = "YtjRequest";

    private String mBaseUrl;
    private String mDate;

    // Maximum amount of companies for one date is set to 50
    YtjRequest() {
        mBaseUrl = "https://avoindata.prh.fi/bis/v1?totalResults=false&maxResults=50";
    }

    /**
     *  Set the founding date of the companies you want to find. To get all the companies founded at
     *  a given date we need to make a request that finds all companies founded at a time frame
     *  from the previous date to the current date.
     *
     * @param day
     * @param month
     * @param year
     */
    public void setDate(int day, final int month, int year) {
        final String METHOD_NAME = "setDate";

        // Add 0 in front of month so it is always 2 character long
        String monthString;
        String previousMonthString;
        int previousYear = year;
        if (month < 10) {
            monthString = String.format("%02d", month);
            previousMonthString = String.format("%02d", month);
        } else {
            monthString = String.valueOf(month);
            previousMonthString = String.valueOf(month);
        }
        String dayString;
        String previousDayString;

        // The day must be 2 character long always too
        if (day < 10) {
            dayString = String.format("%02d", day);
            if (day == 1) {
                // If the current date is 1, then the previous date should be 31 if
                // current month is one of: [1, 3, 5, 7, 8, 10, 12]
                // 28 if [2]
                // 30 if current month is [4, 6, 9, 11]
                int[] previousMonthContains31Days = {1, 2, 4, 6, 8, 9, 11};

                if (intArrayContains(previousMonthContains31Days, month)) {
                    previousDayString = "31";
                    // Current date is January 1st, so need to lower year by one
                    if (month == 1) {
                        previousYear -= 1;
                    }
                } else if (month == 3) {
                    previousDayString = "28";
                } else {
                    previousDayString = "30";
                }

                // Current day is 1st, so need to lower month by one
                if (month == 1) {
                    previousMonthString = "12";
                } else if (month <= 10) {
                    previousMonthString = String.format("%02d", month-1);
                } else {
                    previousMonthString = String.valueOf(month-1);
                }
            } else {
                previousDayString = String.format("%02d", day - 1);
            }
        } else {
            dayString = String.valueOf(day);
            previousDayString = String.valueOf(day-1);
        }
        mDate = "&companyRegistrationFrom=" + previousYear + "-" + previousMonthString + "-" + previousDayString + "&companyRegistrationTo=" + year + "-" + monthString + "-" + dayString;
    }

    /**
     * Get the url for finding the companies registered at a specific date
     *
     * @return The url where to send the GET request to obtain the companies
     */
    public String getRequestUrl() {
        return mBaseUrl + mDate;
    }

    /**
     * Check if int array contains an int
     *
     * @param array
     * @param integer
     * @return
     */
    private boolean intArrayContains(int[] array, int integer) {
        for (int i : array) {
            if (i == integer) {
                return true;
            }
        }
        return false;
    }
}
