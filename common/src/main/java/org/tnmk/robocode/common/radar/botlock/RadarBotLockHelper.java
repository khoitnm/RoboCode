package org.tnmk.robocode.common.radar.botlock;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

/**
 * http://old.robowiki.net/robowiki?Radar
 * RadarBot Lock
 * By PEZ
 */
public class RadarBotLockHelper {
    /**
     * Behavior when seeing the target
     * @param radarBotLockContext
     * @param scannedRobotEvent
     */
    public static void onScannedRobot(RadarBotLockContext radarBotLockContext, ScannedRobotEvent scannedRobotEvent) {
        AdvancedRobot robot = radarBotLockContext.getRobot();
        radarBotLockContext.setEnemyAbsoluteBearing(robot.getHeadingRadians() + scannedRobotEvent.getBearingRadians());
        radarBotLockContext.setTimeSinceLastSeenEnemy(0);
    }

    /**
     * set turn radar stick to the target.
     * @param radarBotLockContext
     * This method is usually used in the main loop.
     */
    public static void setTurnRadar(RadarBotLockContext radarBotLockContext) {
        AdvancedRobot robot = radarBotLockContext.getRobot();
        radarBotLockContext.setTimeSinceLastSeenEnemy(radarBotLockContext.getTimeSinceLastSeenEnemy()+ 1);
        double radarOffset = Double.POSITIVE_INFINITY;
        if(radarBotLockContext.getTimeSinceLastSeenEnemy() < 3) {
            radarOffset = Utils.normalRelativeAngle(robot.getRadarHeadingRadians() - radarBotLockContext.getEnemyAbsoluteBearing());
            radarOffset += sign(radarOffset) * 0.02;
        }
        robot.setTurnRadarLeftRadians(radarOffset);
    }

    private static int sign(double radarOffset) {
        return radarOffset > 0 ? 1 : -1;
    }
}
