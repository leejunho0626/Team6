package com.example.helloroutine;

public class GridItem
{
    private String cYear, cMonth, cDay="";
    private int cText=0;

    GridItem(String y, String m)
    {
        cYear = y;
        cMonth = m;

    }

    GridItem(String y, String m, String d, int text)
    {
        cYear = y;
        cMonth = m;
        cDay = d;
        cText = text;
    }

    public String year()
    {
        return cYear;
    }

    public String month()
    {
        return cMonth;
    }

    public String day()
    {
        return cDay;
    }

    public int text()
    {
        return cText;
    }
}