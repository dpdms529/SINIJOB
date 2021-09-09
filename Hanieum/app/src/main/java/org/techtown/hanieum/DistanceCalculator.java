package org.techtown.hanieum;

public class DistanceCalculator {
    private static Double start_x; // 시작지점 x좌표
    private static Double start_y; // 시작지점 y좌표
    private static Double end_x; // 끝지점 x좌표
    private static Double end_y; // 끝지점 y좌표

    public DistanceCalculator(String start_x, String start_y, String end_x, String end_y) {
        this.start_x = Double.valueOf(start_x);
        this.start_y = Double.valueOf(start_y);
        this.end_x = Double.valueOf(end_x);
        this.end_y = Double.valueOf(end_y);
    }

    static double getStraightDist() {
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
