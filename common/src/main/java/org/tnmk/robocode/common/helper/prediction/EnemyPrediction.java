package org.tnmk.robocode.common.helper.prediction;

import java.awt.geom.Point2D;
import org.tnmk.robocode.common.gun.pattern.EnemyMovePattern;
import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.model.enemy.EnemyStatisticContext;

/**
 * This class represents the prediction of an enemy at a point of time.
 * <p/>
 * While the class {@link EnemyStatisticContext} represents the history of enemy data as well as the history of prediction.
 */
public class EnemyPrediction {
    /**
     * Note: this is not the time we do prediction. It's the corresponding time of the predictionPosition.
     */
    private final long predictionTime;
    private final EnemyMovePattern enemyMovePattern;
    private final Point2D predictionPosition;

    private final double enemyAvgChangeHeadingRadian;
    private final double enemyAvgVelocity;

    public EnemyPrediction(EnemyMovePattern enemyMovePattern, long predictionTime, Point2D predictionPosition, double enemyAvgChangeHeadingRadian, double enemyAvgVelocity) {
        this.predictionTime = predictionTime;
        this.enemyMovePattern = enemyMovePattern;
        this.predictionPosition = predictionPosition;
        this.enemyAvgChangeHeadingRadian = enemyAvgChangeHeadingRadian;
        this.enemyAvgVelocity = enemyAvgVelocity;
    }

    @Override
    public String toString(){
        return String.format("[%s] %s: %s", predictionTime, enemyMovePattern, LogHelper.toString(predictionPosition));
    }

    public long getPredictionTime() {
        return predictionTime;
    }

    public Point2D getPredictionPosition() {
        return predictionPosition;
    }

    public EnemyMovePattern getEnemyMovePattern() {
        return enemyMovePattern;
    }

    public double getEnemyAvgChangeHeadingRadian() {
        return enemyAvgChangeHeadingRadian;
    }

    public double getEnemyAvgVelocity() {
        return enemyAvgVelocity;
    }
}
