package org.tnmk.robocode.common.gun;

/**
 * This context can help different GunStrategies knows that the gun is aiming by some strategy.<br/>
 * So they shouldn't override aiming direction which set by other gun strategies.<br/>
 * <p/>
 * This object should be created by some composition gun strategy such as TheUnfoldingGun.
 */
public class GunStateContext {

    /**
     * If null, it means there's no aiming at any target yet, no gun strategy is using yet.
     */
    private GunStrategy gunStrategy = null;
    private double bulletPower;
    /**
     * Check whether a robot is aiming or not.
     */
    private boolean isAiming;

    /**
     * This method should be trigger when the robot starts to aim (but not fire the bullet yet)
     * @param gunStrategy
     * @param bulletPower
     */
    public void saveSateAimGun(GunStrategy gunStrategy, double bulletPower) {
        this.gunStrategy = gunStrategy;
        this.bulletPower = bulletPower;
        this.isAiming = true;
    }

    /**
     * This method should be trigger when the robot finishes aiming (and starts to fire a bullet)
     */
    public void saveStateFinishedAiming() {
        this.isAiming = false;
        /* After finish aiming, we don't reset the gunStrategy because the robot may still use the same strategy */
    }

    public boolean isStrategy(GunStrategy gunStrategy) {
        return this.gunStrategy == gunStrategy;
    }

    public double getBulletPower() {
        return bulletPower;
    }

    public boolean isAiming() {
        return isAiming;
    }
}
