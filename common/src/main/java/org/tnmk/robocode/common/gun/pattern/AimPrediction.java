package org.tnmk.robocode.common.gun.pattern;

import org.tnmk.robocode.common.helper.prediction.EnemyPrediction;

import java.awt.geom.Point2D;

public class AimPrediction {
    private final EnemyPrediction enemyPrediction;
    private final Point2D predictRobotPosition;
    private final double gunTurnLeftRadian;

    public AimPrediction(EnemyPrediction enemyPrediction, Point2D predictRobotPosition, double gunTurnLeftRadian) {
        this.enemyPrediction = enemyPrediction;
        this.predictRobotPosition = predictRobotPosition;
        this.gunTurnLeftRadian = gunTurnLeftRadian;
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
}
