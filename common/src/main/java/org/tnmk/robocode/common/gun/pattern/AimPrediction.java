package org.tnmk.robocode.common.gun.pattern;

import org.tnmk.robocode.common.helper.prediction.EnemyPrediction;

import java.awt.geom.Point2D;

public class AimPrediction {
    private final EnemyPrediction enemyPrediction;
    private final Point2D predictRobotPosition;
    private final double gunTurnLeftRadian;
    private final double bulletPower;
    private final long timeMakingPrediction;
    private final long timeWhenBulletReachEnemy;
    /**
     * The total period of time since current time until the bullet reach the enemy
     */
    private final long totalPeriodGun;
    private final long periodForTurningGun;
    private final long periodForBulletToReachEnemy;

    public AimPrediction(EnemyPrediction enemyPrediction, Point2D predictRobotPosition, double gunTurnLeftRadian, double bulletPower, long timeMakingPrediction, long timeWhenBulletReachEnemy, long totalPeriodGun, long periodForTurningGun, long periodForBulletToReachEnemy) {
        this.enemyPrediction = enemyPrediction;
        this.predictRobotPosition = predictRobotPosition;
        this.gunTurnLeftRadian = gunTurnLeftRadian;
        this.bulletPower = bulletPower;
        this.timeMakingPrediction = timeMakingPrediction;
        this.timeWhenBulletReachEnemy = timeWhenBulletReachEnemy;

        this.totalPeriodGun = totalPeriodGun;
        this.periodForTurningGun = periodForTurningGun;
        this.periodForBulletToReachEnemy = periodForBulletToReachEnemy;
    }

    public long getTimeWhenBulletReachEnemy() {
        return timeWhenBulletReachEnemy;
    }

    public long getTimeMakingPrediction() {
        return timeMakingPrediction;
    }

    public long getPeriodForTurningGun() {
        return periodForTurningGun;
    }

    public long getPeriodForBulletToReachEnemy() {
        return periodForBulletToReachEnemy;
    }

    public EnemyPrediction getEnemyPrediction() {
        return enemyPrediction;
    }

    public Point2D getPredictRobotPosition() {
        return predictRobotPosition;
    }

    public double getGunTurnLeftRadian() {
        return gunTurnLeftRadian;
    }

    public long getTotalPeriodGun() {
        return totalPeriodGun;
    }

    public double getBulletPower() {
        return bulletPower;
    }
}
