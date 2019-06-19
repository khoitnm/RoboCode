package org.tnmk.robocode.common.radar.botlock;

import org.tnmk.robocode.common.model.enemy.Enemy;
import org.tnmk.robocode.common.model.enemy.EnemyMapper;
import org.tnmk.robocode.common.radar.AllEnemiesObservationContext;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

/**
 * http://old.robowiki.net/robowiki?Radar
 * RadarBot Lock
 * By PEZ
 */
public class BotLockRadar {
    private final AdvancedRobot robot;
    private final AllEnemiesObservationContext allEnemiesObservationContext;

    /**
     * How many ticks since the last time the robot see its target.
     */
    private int timeSinceLastSeenEnemy = 0;

    private double enemyAbsoluteBearing;

    public BotLockRadar(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext) {
        this.robot = robot;
        this.allEnemiesObservationContext = allEnemiesObservationContext;
    }

    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
//        Enemy enemy = EnemyMapper.toEnemy(robot, scannedRobotEvent);
//        String message = String.format("Actual enemy at time %s, position {%.2f, %.2f}", robot.getTime(), enemy.getPosition().getX(), enemy.getPosition().getY());
//        LogHelper.logRobotMovement(robot, message);
        Enemy enemy = EnemyMapper.toEnemy(this.robot, scannedRobotEvent);
        allEnemiesObservationContext.addEnemy(enemy);

        enemyAbsoluteBearing = (robot.getHeadingRadians() + scannedRobotEvent.getBearingRadians());
        timeSinceLastSeenEnemy = 0;

        setTurnRadar();
    }

    /**
     * set turn radar stick to the target.
     * This method is usually used in the main loop.
     */
    private void setTurnRadar() {
        timeSinceLastSeenEnemy++;
        double radarOffset = Double.POSITIVE_INFINITY;
        if (timeSinceLastSeenEnemy < 3) {
            radarOffset = Utils.normalRelativeAngle(robot.getRadarHeadingRadians() - enemyAbsoluteBearing);
            radarOffset += sign(radarOffset) * 0.02;
        }
        robot.setTurnRadarLeftRadians(radarOffset);
    }

    private static int sign(double radarOffset) {
        return radarOffset > 0 ? 1 : -1;
    }
}
