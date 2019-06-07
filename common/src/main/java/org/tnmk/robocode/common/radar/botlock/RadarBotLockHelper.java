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
    public static void onScannedRobot(RadarBotLockContext radarBotLockContext, ScannedRobotEvent e) {
        AdvancedRobot robot = radarBotLockContext.getRobot();
        radarBotLockContext.setEnemyAbsoluteBearing(robot.getHeadingRadians() + e.getBearingRadians());
        radarBotLockContext.setTimeSinceLastSeenEnemy(0);
    }

    public static void doScanner(RadarBotLockContext radarBotLockContext) {
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
