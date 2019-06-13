package org.tnmk.robocode.common.gun;

/**
 * This context can help different GunStrategies knows that the gun is aiming by some strategy.<br/>
 * So they shouldn't override aiming direction which set by other gun strategies.<br/>
 * <p/>
 * This object should be created by some composition gun strategy such as TheUnfoldingGun.
 */
public class GunStateContext {

    private GunState gunState = GunState.REST;
    private double bulletPower;

    public void aimGun(double bulletPower) {
        this.gunState = GunState.AIMING;
        this.bulletPower = bulletPower;
    }

    public void rest() {
        gunState = GunState.REST;
    }

    public GunState getGunState() {
        return gunState;
    }

    public double getBulletPower() {
        return bulletPower;
    }

    public boolean isAiming() {
        return gunState == GunState.AIMING;
    }
}
