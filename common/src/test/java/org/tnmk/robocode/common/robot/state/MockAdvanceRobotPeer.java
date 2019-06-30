package org.tnmk.robocode.common.robot.state;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.util.List;
import robocode.*;
import robocode.robotinterfaces.peer.IAdvancedRobotPeer;

public class MockAdvanceRobotPeer implements IAdvancedRobotPeer {
    private String name;
    private long time;

    private double x;
    private double y;
    private double velocity;
    private double energy;
    private double gunHeat;

    private double maxVelocity;
    private double maxTurnRate;

    private double distanceRemaining;
    private double bodyTurnRemaining;
    private double gunTurnRemaining;
    private double radarTurnRemaining;

    private double bodyHeading;
    private double gunHeading;
    private double radarHeading;

    private int others;
    private int numSentries;
    private int roundNum;

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getMaxVelocity() {
        return maxVelocity;
    }

    @Override
    public void setMaxVelocity(double maxVelocity) {
        this.maxVelocity = maxVelocity;
    }

    @Override
    public void waitFor(Condition condition) {

    }

    @Override
    public void setInterruptible(boolean b) {

    }

    @Override
    public void setEventPriority(String s, int i) {

    }

    @Override
    public int getEventPriority(String s) {
        return 0;
    }

    @Override
    public void addCustomEvent(Condition condition) {

    }

    @Override
    public void removeCustomEvent(Condition condition) {

    }

    @Override
    public void clearAllEvents() {

    }

    @Override
    public List<Event> getAllEvents() {
        return null;
    }

    @Override
    public List<StatusEvent> getStatusEvents() {
        return null;
    }

    @Override
    public List<BulletMissedEvent> getBulletMissedEvents() {
        return null;
    }

    @Override
    public List<BulletHitBulletEvent> getBulletHitBulletEvents() {
        return null;
    }

    @Override
    public List<BulletHitEvent> getBulletHitEvents() {
        return null;
    }

    @Override
    public List<HitByBulletEvent> getHitByBulletEvents() {
        return null;
    }

    @Override
    public List<HitRobotEvent> getHitRobotEvents() {
        return null;
    }

    @Override
    public List<HitWallEvent> getHitWallEvents() {
        return null;
    }

    @Override
    public List<RobotDeathEvent> getRobotDeathEvents() {
        return null;
    }

    @Override
    public List<ScannedRobotEvent> getScannedRobotEvents() {
        return null;
    }

    @Override
    public File getDataDirectory() {
        return null;
    }

    @Override
    public File getDataFile(String s) {
        return null;
    }

    @Override
    public long getDataQuotaAvailable() {
        return 0;
    }

    public double getMaxTurnRate() {
        return maxTurnRate;
    }

    @Override
    public boolean isAdjustGunForBodyTurn() {
        return true;
    }

    @Override
    public boolean isAdjustRadarForGunTurn() {
        return true;
    }

    @Override
    public boolean isAdjustRadarForBodyTurn() {
        return true;
    }

    @Override
    public void setStop(boolean b) {

    }

    @Override
    public void setResume() {

    }

    @Override
    public void setMove(double v) {

    }

    @Override
    public void setTurnBody(double v) {

    }

    @Override
    public void setTurnGun(double v) {

    }

    @Override
    public void setTurnRadar(double v) {

    }
    @Override
    public void setMaxTurnRate(double maxTurnRate) {
        this.maxTurnRate = maxTurnRate;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getVelocity() {
        return velocity;
    }

    @Override
    public double getBodyHeading() {
        return this.bodyHeading;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    @Override
    public double getEnergy() {
        return energy;
    }

    @Override
    public double getX() {
        return this.x;
    }

    @Override
    public double getY() {
        return this.y;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    @Override
    public double getGunHeat() {
        return gunHeat;
    }

    @Override
    public double getBattleFieldWidth() {
        return 1200;
    }

    @Override
    public double getBattleFieldHeight() {
        return 1200;
    }

    public void setGunHeat(double gunHeat) {
        this.gunHeat = gunHeat;
    }

    public double getDistanceRemaining() {
        return distanceRemaining;
    }


    public void setBodyTurnRemaining(double bodyTurnRemaining) {
        this.bodyTurnRemaining = bodyTurnRemaining;
    }

    public void setBodyHeading(double bodyHeading) {
        this.bodyHeading = bodyHeading;
    }

    @Override
    public double getBodyTurnRemaining() {
        return this.bodyTurnRemaining;
    }

    public void setDistanceRemaining(double distanceRemaining) {
        this.distanceRemaining = distanceRemaining;
    }

    @Override
    public double getGunTurnRemaining() {
        return gunTurnRemaining;
    }

    public void setGunTurnRemaining(double gunTurnRemaining) {
        this.gunTurnRemaining = gunTurnRemaining;
    }
    @Override
    public double getRadarTurnRemaining() {
        return radarTurnRemaining;
    }

    @Override
    public void execute() {

    }

    @Override
    public void move(double v) {

    }

    @Override
    public void turnBody(double v) {

    }

    @Override
    public void turnGun(double v) {

    }

    @Override
    public Bullet fire(double v) {
        return null;
    }

    @Override
    public Bullet setFire(double v) {
        return null;
    }

    @Override
    public void setBodyColor(Color color) {

    }

    @Override
    public void setGunColor(Color color) {

    }

    @Override
    public void setRadarColor(Color color) {

    }

    @Override
    public void setBulletColor(Color color) {

    }

    @Override
    public void setScanColor(Color color) {

    }

    @Override
    public void getCall() {

    }

    @Override
    public void setCall() {

    }

    @Override
    public Graphics2D getGraphics() {
        return null;
    }

    @Override
    public void setDebugProperty(String s, String s1) {

    }

    @Override
    public void rescan() {

    }

    public void setRadarTurnRemaining(double radarTurnRemaining) {
        this.radarTurnRemaining = radarTurnRemaining;
    }

    @Override
    public double getGunHeading() {
        return gunHeading;
    }

    public void setGunHeading(double gunHeading) {
        this.gunHeading = gunHeading;
    }

    @Override
    public double getRadarHeading() {
        return radarHeading;
    }

    public void setRadarHeading(double radarHeading) {
        this.radarHeading = radarHeading;
    }

    @Override
    public int getOthers() {
        return others;
    }

    public void setOthers(int others) {
        this.others = others;
    }
    @Override
    public int getNumSentries() {
        return numSentries;
    }

    @Override
    public int getNumRounds() {
        return 0;
    }

    public void setNumSentries(int numSentries) {
        this.numSentries = numSentries;
    }
    @Override
    public int getRoundNum() {
        return roundNum;
    }

    @Override
    public int getSentryBorderSize() {
        return 0;
    }

    @Override
    public double getGunCoolingRate() {
        return 0;
    }

    public void setRoundNum(int roundNum) {
        this.roundNum = roundNum;
    }


    @Override
    public void stop(boolean b) {

    }

    @Override
    public void resume() {

    }

    @Override
    public void turnRadar(double v) {

    }

    @Override
    public void setAdjustGunForBodyTurn(boolean b) {

    }

    @Override
    public void setAdjustRadarForGunTurn(boolean b) {

    }

    @Override
    public void setAdjustRadarForBodyTurn(boolean b) {

    }
}
