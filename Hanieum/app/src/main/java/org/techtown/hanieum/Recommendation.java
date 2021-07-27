package org.techtown.hanieum;

import android.widget.ImageButton;

public class Recommendation {
    String companyName;
    String title;
    String transportation;
    String timeCost;
    boolean bookmark;

    public Recommendation(String companyName, String title, String transportation, String timeCost,
                          boolean bookmark) {
        this.companyName = companyName;
        this.title = title;
        this.transportation = transportation;
        this.timeCost = timeCost;
        this.bookmark = bookmark;
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
