package org.techtown.hanieum;

public class Search {
    private String title; // 검색 항목의 텍스트
    private String code;
    private boolean isChecked; // 체크 박스 체크여부
    private int viewType; // 직업 검색인지 지역 검색인지 구분하기 위한 viewType

    public Search(String title, String code, int viewType) {
        this.title = title;
        this.code = code;
        this.viewType = viewType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
