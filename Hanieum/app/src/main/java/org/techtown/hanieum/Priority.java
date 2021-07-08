package org.techtown.hanieum;

public class Priority {
    int num; // 우선 순위 숫자
    String contents; // 우선 순위 내용
    int viewType; // 숫자인지 내용인지 구분하기 위한 viewType

    public Priority(int num, String contents, int viewType) {
        this.num = num;
        this.contents = contents;
        this.viewType = viewType;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}
