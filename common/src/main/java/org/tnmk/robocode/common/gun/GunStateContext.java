package org.tnmk.robocode.common.gun;

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
