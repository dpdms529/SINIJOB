package org.techtown.hanieum;

import org.techtown.hanieum.db.entity.CvInfo;

public class Career {
    private int no;
    private String jobCode;
    private String jobName;
    private String compName;
    private String position;
    private String careerStart;
    private String carrerEnd;
    private int period;

    public Career(){}

    public Career(CvInfo cv) {
        this.no = cv.info_no;
        this.jobCode = cv.info_code;
        this.jobName = cv.info;
        this.compName = cv.company_name;
        this.position = cv.job_position;
        this.careerStart = cv.career_start;
        this.carrerEnd = cv.career_end;
        this.period = cv.period;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobCode() {
        return jobCode;
    }

    public void setJobCode(String jobCode) {
        this.jobCode = jobCode;
    }

    public String getCompName() {
        return compName;
    }

    public void setCompName(String compName) {
        this.compName = compName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCareerStart() {
        return careerStart;
    }

    public void setCareerStart(String careerStart) {
        this.careerStart = careerStart;
    }

    public String getCarrerEnd() {
        return carrerEnd;
    }

    public void setCarrerEnd(String carrerEnd) {
        this.carrerEnd = carrerEnd;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }
}
