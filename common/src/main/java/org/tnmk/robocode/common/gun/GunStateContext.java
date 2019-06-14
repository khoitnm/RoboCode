package org.tnmk.robocode.common.gun;

/**
 * This context can help different GunStrategies knows that the gun is aiming by some strategy.<br/>
 * So they shouldn't override aiming direction which set by other gun strategies.<br/>
 * <p/>
 * This object should be created by some composition gun strategy such as TheUnfoldingGun.
 */
public class GunStateContext {

    /**
     * If null, it means there's no aiming any target.
     */
    private GunStrategy gunStrategy = null;
    private double bulletPower;

    /**
     * @param gunStrategy
     * @param bulletPower
     */
    public void saveSateAimGun(GunStrategy gunStrategy, double bulletPower) {
        this.gunStrategy = gunStrategy;
        this.bulletPower = bulletPower;
    }

    public void saveSateRest() {
        gunStrategy = null;
    }

    public GunStrategy getGunStrategy() {
        return gunStrategy;
    }

    public double getBulletPower() {
        return bulletPower;
    }

    public boolean isAiming() {
        return gunStrategy != null;
    }
}
