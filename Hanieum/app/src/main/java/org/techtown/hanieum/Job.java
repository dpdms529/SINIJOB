package org.techtown.hanieum;

public class Job {
    private String job1; // 1차 직종
    private String job2; // 2차 직종
    private boolean isSelected = false; // 선택되었는지
    private int viewType; // 1차, 2차 직종을 구분하기 위한 viewType
    public Job(String job1, String job2, int viewType) {
        this.job1 = job1;
        this.job2 = job2;
        this.viewType = viewType;
    }

    public String getJob1() {
        return job1;
    }

    public void setJob1(String job1) {
        this.job1 = job1;
    }

    public String getJob2() {
        return job2;
    }

    public void setJob2(String job2) {
        this.job2 = job2;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}
