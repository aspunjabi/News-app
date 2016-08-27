package com.example.android.newsapp;

/**
 * Created by aspun_000 on 8/25/2016.
 */
public class News {

    private String mTitle;

    private String mSectionName;

    private String mDate;

    private String mWebUrl;

    private String mAuthor;

    public News(String title, String sectionName, String date, String webUrl, String author) {
        mTitle = title;
        mSectionName = sectionName;
        mDate = date;
        mWebUrl = webUrl;
        mAuthor = author;
    }

    public News(String title, String sectionName, String date, String webUrl) {
        mTitle = title;
        mSectionName = sectionName;
        mDate = date;
        mWebUrl = webUrl;
      }

    public String getTitle() { return mTitle; }

    public String getSectionName() { return mSectionName; }

    public String getDate() { return mDate; }

    public String getWebUrl() { return mWebUrl; }

    public String getAuthorName() {
        if (mAuthor != null) {
            return mAuthor;
        } else {
            return "Author unknown";
        }
    }
}

