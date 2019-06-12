package org.tnmk.robocode.common.gun.pattern;

import java.awt.geom.Point2D;
import java.util.List;
import org.tnmk.common.math.AngleUtils;
import org.tnmk.common.math.MathUtils;
import org.tnmk.robocode.common.model.enemy.Enemy;
import org.tnmk.robocode.common.model.enemy.EnemyHistory;
import org.tnmk.robocode.common.model.enemy.EnemyPatternPrediction;
import org.tnmk.robocode.common.radar.AllEnemiesObservationContext;
import org.tnmk.robocode.common.robot.Scannable;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

/**
 * https://www.ibm.com/developerworks/library/j-circular/index.html
 */
public class CircularPatternGun implements Scannable {
    private final AdvancedRobot robot;
    private final AllEnemiesObservationContext allEnemiesObservationContext;

    public CircularPatternGun(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext) {
        this.robot = robot;
        this.allEnemiesObservationContext = allEnemiesObservationContext;
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        String enemyName = scannedRobotEvent.getName();
        EnemyPatternPrediction enemyPatternPrediction = allEnemiesObservationContext.getEnemyPatternPrediction(enemyName);
        aimGun(robot, enemyPatternPrediction.getEnemyHistory(), 1.9);
    }

    /**
     * This function predicts the time of the intersection between the
     * bullet and the target based on a simple iteration.  It then moves
     * the gun to the correct angle to fire on the target.
     **/
    private void aimGun(AdvancedRobot robot, EnemyHistory enemyHistory, double firePower) {
        Point2D enemyPosition = predictEnemyPositionWhenBulletReachEnemy(robot, enemyHistory, firePower);

        /**Turn the gun to the correct angle**/
        double robotToEnemyRadian = Math.PI / 2 - Math.atan2(enemyPosition.getY() - robot.getY(), enemyPosition.getX() - robot.getX());
        double gunOffset = robot.getGunHeadingRadians() - robotToEnemyRadian;
        robot.setTurnGunLeftRadians(AngleUtils.normaliseRadian(gunOffset));
        //TODO fire Gun only after turnGun finish, not now.
        robot.setFire(firePower);
    }

    private Point2D predictEnemyPositionWhenBulletReachEnemy(AdvancedRobot robot, EnemyHistory enemyHistory, double firePower) {
        long time;
        long nextTime;
        Point2D enemyPosition = enemyHistory.getLatestHistoryItem().getPosition();
        for (int i = 0; i < 10; i++) {//this loop is used to improve the correctness of prediction.
            double distanceRobotToEnemy = MathUtils.distance(robot.getX(), robot.getY(), enemyPosition.getX(), enemyPosition.getY());
            nextTime = Math.round((distanceRobotToEnemy / (20 - (3 * firePower))));
            time = robot.getTime() + nextTime;

            List<Enemy> latestHistoryItems = enemyHistory.getLatestHistoryItems(3);
            enemyPosition = CircularGuessUtils.guessPosition(latestHistoryItems, time);
        }
        return enemyPosition;
    }


}
