package org.techtown.hanieum;

public class Career {
    private String jobName;
    private String jobCode;
    private String compName;
    private String position;
    private String periodStr;
    private int periodInt;

    public Career(String jobName, String compName, String position, String periodStr) {
        this.jobName = jobName;
        this.compName = compName;
        this.position = position;
        this.periodStr = periodStr;
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

    public String getPeriodStr() {
        return periodStr;
    }

    public void setPeriodStr(String periodStr) {
        this.periodStr = periodStr;
    }

    public int getPeriodInt() {
        return periodInt;
    }

    public void setPeriodInt(int periodInt) {
        this.periodInt = periodInt;
    }
}
