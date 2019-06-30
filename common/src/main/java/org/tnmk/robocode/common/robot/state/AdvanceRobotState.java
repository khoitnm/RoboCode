package org.tnmk.robocode.common.robot.state;

import java.awt.geom.Point2D;

public class AdvanceRobotState {
    private String name;
    private long time;

    private Point2D position;
    private double velocity;
    private double energy;
    private double gunHeat;

    private double heading;
    private double gunHeading;
    private double radarHeading;

    private double distanceRemaining;
    private double turnRemaining;
    private double gunTurnRemaining;
    private double radarTurnRemaining;


    private int others;
    private int numSentries;
    private int roundNum;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public double getGunHeat() {
        return gunHeat;
    }

    public void setGunHeat(double gunHeat) {
        this.gunHeat = gunHeat;
    }

    public double getDistanceRemaining() {
        return distanceRemaining;
    }

    public void setDistanceRemaining(double distanceRemaining) {
        this.distanceRemaining = distanceRemaining;
    }

    public double getTurnRemaining() {
        return turnRemaining;
    }

    public void setTurnRemaining(double turnRemaining) {
        this.turnRemaining = turnRemaining;
    }

    public double getGunTurnRemaining() {
        return gunTurnRemaining;
    }

    public void setGunTurnRemaining(double gunTurnRemaining) {
        this.gunTurnRemaining = gunTurnRemaining;
    }

    public double getRadarTurnRemaining() {
        return radarTurnRemaining;
    }

    public void setRadarTurnRemaining(double radarTurnRemaining) {
        this.radarTurnRemaining = radarTurnRemaining;
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public double getGunHeading() {
        return gunHeading;
    }

    public void setGunHeading(double gunHeading) {
        this.gunHeading = gunHeading;
    }

    public double getRadarHeading() {
        return radarHeading;
    }

    public void setRadarHeading(double radarHeading) {
        this.radarHeading = radarHeading;
    }

    public int getOthers() {
        return others;
    }

    public void setOthers(int others) {
        this.others = others;
    }

    public int getNumSentries() {
        return numSentries;
    }

    public void setNumSentries(int numSentries) {
        this.numSentries = numSentries;
    }

    public int getRoundNum() {
        return roundNum;
    }

    public void setRoundNum(int roundNum) {
        this.roundNum = roundNum;
    }
}
