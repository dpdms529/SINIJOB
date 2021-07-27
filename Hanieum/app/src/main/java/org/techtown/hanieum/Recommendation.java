package org.techtown.hanieum;

import android.widget.ImageButton;

public class Recommendation {
    private String id;
    private String companyName;
    private String title;
    private String transportation;
    private String timeCost;
    private boolean bookmark;

    public Recommendation(String id, String companyName, String title, String transportation,
                          String timeCost, boolean bookmark) {
        this.id = id;
        this.companyName = companyName;
        this.title = title;
        this.transportation = transportation;
        this.timeCost = timeCost;
        this.bookmark = bookmark;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTransportation() {
        return transportation;
    }

    public void setTransportation(String transportation) {
        this.transportation = transportation;
    }

    public String getTimeCost() {
        return timeCost;
    }

    public void setTimeCost(String timeCost) {
        this.timeCost = timeCost;
    }

    public boolean getBookmark() {
        return bookmark;
    }

    public void setBookmark(boolean bookmark) {
        this.bookmark = bookmark;
    }
}
