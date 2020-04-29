package com.hardikdua.codeforces;

public class ContestsInfo {
    private String name,duration,starttime,date,id;

    public String getId() {
        return id;
    }

    public ContestsInfo(String name, String duration, String starttime, String date, String id) {
        this.name = name;
        this.duration = duration;
        this.starttime = starttime;
        this.date = date;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDuration() {
        return duration;
    }

    public String getStarttime() {
        return starttime;
    }

    public String getDate() {
        return date;
    }
}
