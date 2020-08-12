package com.example.notes;

import android.net.Uri;

import com.google.firebase.storage.StorageReference;

public class CardDetails {
    private String mTitle;
    private String mDate;
    private String mTime;
    private String mDescription;
    private Uri mImage;
    private Uri mAudio;

    public CardDetails(String mTitle, String mDate, String mTime, String mDescription, Uri mImage, Uri mAudio) {
        this.mTitle = mTitle;
        this.mDate = mDate;
        this.mTime = mTime;
        this.mDescription = mDescription;
        this.mImage = mImage;
        this.mAudio = mAudio;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmDate() {
        return mDate;
    }

    public String getmTime() {
        return mTime;
    }

    public String getmDescription() {
        return mDescription;
    }

    public Uri getmImage() {
        return mImage;
    }

    public Uri getmAudio() {
        return mAudio;
    }
}
