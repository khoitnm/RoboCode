package org.tnmk.robocode.common.gun.pattern;

import java.awt.geom.Point2D;
import org.tnmk.robocode.common.log.LogHelper;

public class AimResult {
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

    public AimResult(Point2D predictRobotPosition, double gunTurnLeftRadian, double bulletPower, long timeMakingPrediction, long timeWhenBulletReachEnemy, long totalPeriodGun, long periodForTurningGun, long periodForBulletToReachEnemy) {
        this.predictRobotPosition = predictRobotPosition;
        this.gunTurnLeftRadian = gunTurnLeftRadian;
        this.bulletPower = bulletPower;
        this.timeMakingPrediction = timeMakingPrediction;
        this.timeWhenBulletReachEnemy = timeWhenBulletReachEnemy;
        this.totalPeriodGun = totalPeriodGun;
        this.periodForTurningGun = periodForTurningGun;
        this.periodForBulletToReachEnemy = periodForBulletToReachEnemy;
    }

    @Override
    public String toString() {
        return "AimResult{" +
                "predictRobotPosition=" + LogHelper.toString(predictRobotPosition )+
                ", gunTurnLeftRadian=" + LogHelper.toString(gunTurnLeftRadian) +
                ", bulletPower=" + LogHelper.toString(bulletPower) +
                ", timeMakingPrediction=" + timeMakingPrediction +
                ", timeWhenBulletReachEnemy=" + timeWhenBulletReachEnemy +
                ", totalPeriodGun=" + totalPeriodGun +
                ", periodForTurningGun=" + periodForTurningGun +
                ", periodForBulletToReachEnemy=" + periodForBulletToReachEnemy +
                '}';
    }

    public Point2D getPredictRobotPosition() {
        return predictRobotPosition;
    }

    public double getGunTurnLeftRadian() {
        return gunTurnLeftRadian;
    }

    public double getBulletPower() {
        return bulletPower;
    }

    public long getTimeMakingPrediction() {
        return timeMakingPrediction;
    }

    public long getTimeWhenBulletReachEnemy() {
        return timeWhenBulletReachEnemy;
    }

    public long getTotalPeriodGun() {
        return totalPeriodGun;
    }

    public long getPeriodForTurningGun() {
        return periodForTurningGun;
    }

    public long getPeriodForBulletToReachEnemy() {
        return periodForBulletToReachEnemy;
    }
}
