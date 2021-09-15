package org.techtown.hanieum;

public class Career {
    private int no;
    private String jobName;
    private String jobCode;
    private String compName;
    private String position;
    private String period;

    public Career(int no, String jobName, String jobCode, String compName, String position, String period) {
        this.no = no;
        this.jobName = jobName;
        this.jobCode = jobCode;
        this.compName = compName;
        this.position = position;
        this.period = period;
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

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }
}
