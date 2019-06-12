package org.tnmk.robocode.common.gun;

public class GunUtils {
    /**
     * The formular is defined inside this page: http://robowiki.net/wiki/Selecting_Fire_Power
     *
     * @param bulletPower a bullet can be fired with any amount of energy between 0.1 and 3.0.
     * @return
     */
    public static double reckonBulletVelocity(double bulletPower) {
        return 20 - (3 * bulletPower);
    }
}
