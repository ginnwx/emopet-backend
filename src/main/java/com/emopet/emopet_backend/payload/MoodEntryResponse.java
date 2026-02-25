package com.emopet.emopet_backend.payload;

public class MoodEntryResponse {
    private String date;
    private Integer mood;
    private String note;

    public MoodEntryResponse(String date, Integer mood, String note) {
        this.date = date;
        this.mood = mood;
        this.note = note;
    }

    public String getDate() { return date; }
    public Integer getMood() { return mood; }
    public String getNote() { return note; }
}
