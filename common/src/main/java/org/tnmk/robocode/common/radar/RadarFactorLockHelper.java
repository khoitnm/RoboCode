package org.tnmk.robocode.common.radar;


import robocode.AdvancedRobot;
import robocode.Robot;
import robocode.ScannedRobotEvent;

/**
 * View more at http://old.robowiki.net/robowiki?Radar
 */
public class RadarFactorLockHelper {
    /**
     *  A FACTOR of 3 or greater will expand the lock so quickly it reaches the widest lock almost right away. This works the same way Infinity Lock does, but avoids slippage by keeping the enemy in the centre of the lock.
     */
    public static final double FACTOR_INFINITE_LOCK = 3;
    /**
     * 2.1 gives a cool-looking expanding lock at the start of the round.
     */
    private static final double FACTOR_STICKY_LOCK = 2.1;


    /**
     * @param robot             your robot
     * @param scannedRobotEvent scanned found robot event
     * @param factor            1.0 gives the same behaviour as Lazy Lock (above). It will sometimes slip unless you add the manual scanning code as used in Lazy Lock.
     *                          <p/>
     *                          1.0 < FACTOR < 2.0 will narrow down to the minimal lock necessary without slipping.
     *                          <p/>
     *                          1.99 gives a nice fluid narrowing-down motion at the start of the round.
     *                          <p/>
     *                          2.0 will keep a constant size lock on enemy. The size of the lock is determined largely by how close you start off to your enemy.
     *                          <p/>
     *                          &gt; 2.0 will expand the lock area until the widest lock is achieved. The larger FACTOR, the closer the behaviour approaches Infinity Lock. A FACTOR of 3 or greater will expand the lock so quickly it reaches the widest lock almost right away. This works the same way Infinity Lock does, but avoids slippage by keeping the enemy in the centre of the lock.
     *                          <p/>
     *                          2.1 gives a cool-looking expanding lock at the start of the round.
     */
    public static void factorLock(AdvancedRobot robot, ScannedRobotEvent scannedRobotEvent, double factor) {
        double absBearing = scannedRobotEvent.getBearingRadians() + scannedRobotEvent.getHeadingRadians();
        robot.setTurnRadarRightRadians(factor * robocode.util.Utils.normalRelativeAngle(absBearing - robot.getRadarHeadingRadians()));
    }

    public static void infiniteLock(AdvancedRobot robot, ScannedRobotEvent scannedRobotEvent){
        factorLock(robot, scannedRobotEvent, FACTOR_INFINITE_LOCK);
    }

    public static void stickyLock(AdvancedRobot robot, ScannedRobotEvent scannedRobotEvent){
        factorLock(robot, scannedRobotEvent, FACTOR_STICKY_LOCK);
    }
}
