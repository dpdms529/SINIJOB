package org.techtown.hanieum;

public class Career {
    private String job;
    private String jobCode;
    private String compName;
    private String position;
    private String period;

    public Career(String job, String jobCode, String compName, String position, String period) {
        this.job = job;
        this.jobCode = jobCode;
        this.compName = compName;
        this.position = position;
        this.period = period;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
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
