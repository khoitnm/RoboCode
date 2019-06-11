package org.tnmk.robocode.common.model.enemy;

import com.sun.istack.internal.NotNull;
import java.awt.geom.Point2D;
import robocode.Robot;
import robocode.ScannedRobotEvent;

/**
 * This class represent all information of an enemy as a result of {@link Robot#onScannedRobot(ScannedRobotEvent)}
 */
public class Enemy {
    @NotNull
    private String name;

    @NotNull
    private double energy;

    @NotNull
    private double heading;

    @NotNull
    private double bearing;

    @NotNull
    private double distance;

    @NotNull
    private double velocity;

    @NotNull
    private boolean isSentryRobot;

    @NotNull
    private Point2D position;

    @NotNull
    private double time;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public double getBearing() {
        return bearing;
    }

    public void setBearing(double bearing) {
        this.bearing = bearing;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public boolean isSentryRobot() {
        return isSentryRobot;
    }

    public void setSentryRobot(boolean sentryRobot) {
        isSentryRobot = sentryRobot;
    }

    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }

    public double getBearingRadians() {
        return Math.toRadians(this.getBearing());
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }
}
