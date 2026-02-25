package com.emopet.emopet_backend.payload;

public class MoodUpsertRequest {
    private String date; // "YYYY-MM-DD"
    private Integer mood; // 1..5
    private String note;

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public Integer getMood() { return mood; }
    public void setMood(Integer mood) { this.mood = mood; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
