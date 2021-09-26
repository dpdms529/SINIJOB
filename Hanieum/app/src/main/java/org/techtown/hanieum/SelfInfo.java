package org.techtown.hanieum;

import org.techtown.hanieum.db.entity.CoverLetter;

import java.io.Serializable;

public class SelfInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private int no;
    private String code;    //자기소개서 종류(영상,글)
    private String first_item;
    private String second_item;
    private String third_item;
    private String title;   //자기소개서 제목

    public SelfInfo(CoverLetter coverLetter) {
        no = coverLetter.cover_letter_no;
        code = coverLetter.cover_dist_code;
        first_item = coverLetter.first_item;
        second_item = coverLetter.second_item;
        third_item = coverLetter.third_item;
        if(code.equals("0")) {
            title = "영상 자기소개서 " + no;
        } else {
            title = "일반 자기소개서 " + no;
        }
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFirst_item() {
        return first_item;
    }

    public void setFirst_item(String first_item) {
        this.first_item = first_item;
    }

    public String getSecond_item() {
        return second_item;
    }

    public void setSecond_item(String second_item) {
        this.second_item = second_item;
    }

    public String getThird_item() {
        return third_item;
    }

    public void setThird_item(String third_item) {
        this.third_item = third_item;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
