package org.techtown.hanieum;

public class ChipList { // 칩을 위한 클래스
    private String name; // 칩에 들어가는 텍스트
    private int position; // 텍스트를 받아온 위치

    public ChipList(String name, int position) {
        this.name = name;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
