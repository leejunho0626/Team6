package com.example.helloroutine;

public class GridItem
{
    private String cYear, cMonth, cDay="";
    private boolean cImg;
    private int cText=0;

    GridItem(String y, String m, boolean img)
    {
        cYear = y;
        cMonth = m;
        cImg = img;
    }

    GridItem(String y, String m, String d, int text, boolean img)
    {
        cYear = y;
        cMonth = m;
        cDay = d;
        cText = text;
        cImg = img;
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

    public Boolean img()
    {
        return cImg;
    }

    public int text()
    {
        return cText;
    }
}