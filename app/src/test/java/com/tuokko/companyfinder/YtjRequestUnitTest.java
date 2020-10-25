package com.tuokko.companyfinder;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class YtjRequestUnitTest {
    @Test
    public void ytjUrl_validation_october3() {
        String correctUrl = "https://avoindata.prh.fi/bis/v1?totalResults=false&maxResults=50&companyRegistrationFrom=2020-10-02&companyRegistrationTo=2020-10-03";
        YtjRequest request = new YtjRequest();
        request.setDate(3, 10, 2020);
        assertEquals(correctUrl, request.getRequestUrl());
    }

    @Test
    public void ytjUrl_validation_january1() {
        String correctUrl = "https://avoindata.prh.fi/bis/v1?totalResults=false&maxResults=50&companyRegistrationFrom=2019-12-31&companyRegistrationTo=2020-01-01";
        YtjRequest request = new YtjRequest();
        request.setDate(1, 1, 2020);
        assertEquals(correctUrl, request.getRequestUrl());
    }

    @Test
    public void ytjUrl_validation_march1() {
        String correctUrl = "https://avoindata.prh.fi/bis/v1?totalResults=false&maxResults=50&companyRegistrationFrom=2020-02-28&companyRegistrationTo=2020-03-01";
        YtjRequest request = new YtjRequest();
        request.setDate(1, 3, 2020);
        assertEquals(correctUrl, request.getRequestUrl());
    }

    @Test
    public void ytjUrl_validation_december24() {
        String correctUrl = "https://avoindata.prh.fi/bis/v1?totalResults=false&maxResults=50&companyRegistrationFrom=2018-12-23&companyRegistrationTo=2018-12-24";
        YtjRequest request = new YtjRequest();
        request.setDate(24, 12, 2018);
        assertEquals(correctUrl, request.getRequestUrl());
    }
}