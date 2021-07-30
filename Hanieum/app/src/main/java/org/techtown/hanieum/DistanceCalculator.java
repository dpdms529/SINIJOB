package org.techtown.hanieum;

public class DistanceCalculator {
    private String start_x; // 시작지점 x좌표
    private String start_y; // 시작지점 y좌표
    private String end_x; // 끝지점 x좌표
    private String end_y; // 끝지점 y좌표

    public DistanceCalculator(String start_x, String start_y, String end_x, String end_y){
        this.start_x = start_x;
        this.start_y = start_y;
        this.end_x = end_x;
        this.end_y = end_y;
    }

    private static double getStraightDist(double start_x, double start_y, double end_x, double end_y) {
        double theta = start_x - end_x;
        double dist = Math.sin(deg2rad(start_y)) * Math.sin(deg2rad(end_y)) + Math.cos(deg2rad(start_y)) * Math.cos(deg2rad(end_y)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1609.344; // meter

        return (dist);
    }

    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}
