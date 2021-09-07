package org.techtown.hanieum;

public class Region {
    private String region1; // 지역 분류(시/도)
    private String region2; // 지역 분류(구/군/시)
    private String region3; // 지역 분류(동/읍/면)
    private String bDongCode;
    private int viewType; // 지역 분류(1, 2, 3)를 구분하기 위한 viewType
    private boolean isSelected = false; // 선택되었는지

    public Region(String region1, String region2, String region3, String bDongCode, int viewType) {
        this.region1 = region1;
        this.region2 = region2;
        this.region3 = region3;
        this.bDongCode = bDongCode;
        this.viewType = viewType;
    }

    public String getRegion1() {
        return region1;
    }

    public void setRegion1(String region1) {
        this.region1 = region1;
    }

    public String getRegion2() {
        return region2;
    }

    public void setRegion2(String region2) {
        this.region2 = region2;
    }

    public String getRegion3() {
        return region3;
    }

    public void setRegion3(String region3) {
        this.region3 = region3;
    }

    public String getBDongCode() {
        return bDongCode;
    }

    public void setBDongCode(String bDongCode) {
        this.bDongCode = bDongCode;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
