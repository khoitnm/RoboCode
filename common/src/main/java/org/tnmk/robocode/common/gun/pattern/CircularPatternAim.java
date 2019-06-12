package org.tnmk.robocode.common.gun.pattern;

import java.awt.geom.Point2D;
import org.tnmk.common.math.AngleUtils;
import org.tnmk.common.math.MathUtils;
import org.tnmk.robocode.common.model.enemy.EnemyHistory;
import robocode.AdvancedRobot;

/**
 * https://www.ibm.com/developerworks/library/j-circular/index.html
 */
public class CircularPatternAim {
    /**
     * This function predicts the time of the intersection between the
     * bullet and the target based on a simple iteration.  It then moves
     * the gun to the correct angle to fire on the target.
     **/
    public void setGun(AdvancedRobot robot, EnemyHistory enemyHistory, double firePower) {
        Point2D enemyPosition = predictEnemPositionWhenBulletReachEnemy(robot, enemyHistory, firePower);

        /**Turn the gun to the correct angle**/
        double robotToEnemyRadian = Math.PI / 2 - Math.atan2(enemyPosition.getY() - robot.getY(), enemyPosition.getX() - robot.getX());
        double gunOffset = robot.getGunHeadingRadians() - robotToEnemyRadian;
        robot.setTurnGunLeftRadians(AngleUtils.normaliseRadian(gunOffset));
    }

    private Point2D predictEnemPositionWhenBulletReachEnemy(AdvancedRobot robot, EnemyHistory enemyHistory, double firePower){
        long time;
        long nextTime;
        Point2D enemyPosition = enemyHistory.getLatestHistoryItem().getPosition();
        for (int i = 0; i < 10; i++) {//this loop is used to improve the correctness of prediction.
            double distanceRobotToEnemy = MathUtils.distance(robot.getX(), robot.getY(), enemyPosition.getX(), enemyPosition.getY());
            nextTime = Math.round((distanceRobotToEnemy / (20 - (3 * firePower))));
            time = robot.getTime() + nextTime;
            enemyPosition = CircularGuessUtils.guessPosition(enemyHistory, time);
        }
        return enemyPosition;
    }

}
