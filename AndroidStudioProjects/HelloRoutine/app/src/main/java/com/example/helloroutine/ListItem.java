package com.example.helloroutine;

public class ListItem {

    private String title;
    private String sub;
    private int progress;
    private boolean check;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean getCheck(){
        return check;
    }

    public void setCheck(boolean check){
        this.check = check;
    }

}