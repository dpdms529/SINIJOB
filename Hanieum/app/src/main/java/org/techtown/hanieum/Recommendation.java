package org.techtown.hanieum;

public class Recommendation implements Comparable<Recommendation> {
    private String id;
    private String companyName;
    private String title;
    private String salaryType;
    private String salary;
    private Double distance;
    private boolean bookmark;

    public Recommendation(String id, String companyName, String title, String salaryType,
                          String salary, Double distance, boolean bookmark) {
        this.id = id;
        this.companyName = companyName;
        this.title = title;
        this.salaryType = salaryType;
        this.salary = salary;
        this.distance = distance;
        this.bookmark = bookmark;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSalaryType() {
        return salaryType;
    }

    public void setSalaryType(String salaryType) {
        this.salaryType = salaryType;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public boolean getBookmark() {
        return bookmark;
    }

    public void setBookmark(boolean bookmark) {
        this.bookmark = bookmark;
    }

    @Override
    public int compareTo(Recommendation o) {
        return this.distance.compareTo(o.distance);
    }
}
