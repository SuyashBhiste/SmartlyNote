package com.example.notes;

public class CardDetails {
    private String Title;
    private String Date;
    private String Time;
    private String Description;
    private String Image;
    private String Audio;
    private String Id;

    public CardDetails(String Title, String Date, String Time, String Description, String Image, String Audio, String Id) {
        this.Title = Title;
        this.Date = Date;
        this.Time = Time;
        this.Description = Description;
        this.Image = Image;
        this.Audio = Audio;
        this.Id = Id;
    }

    public CardDetails() {

    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String Date) {
        this.Date = Date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String Time) {
        this.Time = Time;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String Image) {
        this.Image = Image;
    }

    public String getAudio() {
        return Audio;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public void setAudio(String Audio) {
        this.Audio = Audio;
    }



}
