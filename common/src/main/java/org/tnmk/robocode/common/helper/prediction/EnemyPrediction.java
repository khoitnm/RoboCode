package org.tnmk.robocode.common.helper.prediction;

import java.awt.geom.Point2D;
import org.tnmk.robocode.common.gun.pattern.EnemyMovePattern;

/**
 * This class represents the prediction of an enemy at a point of time.
 * <p/>
 * While the class {@link org.tnmk.robocode.common.model.enemy.EnemyPredictionHistory} represents the history of enemy data as well as the history of prediction.
 */
public class EnemyPrediction {
    private long predictionTime;
    private EnemyMovePattern enemyMovePattern;
    private Point2D predictionPosition;

    public EnemyPrediction(EnemyMovePattern enemyMovePattern, long predictionTime, Point2D predictionPosition) {
        this.predictionTime = predictionTime;
        this.enemyMovePattern = enemyMovePattern;
        this.predictionPosition = predictionPosition;
    }

    public long getPredictionTime() {
        return predictionTime;
    }

    public void setPredictionTime(long predictionTime) {
        this.predictionTime = predictionTime;
    }

    public Point2D getPredictionPosition() {
        return predictionPosition;
    }

    public void setPredictionPosition(Point2D predictionPosition) {
        this.predictionPosition = predictionPosition;
    }

    public EnemyMovePattern getEnemyMovePattern() {
        return enemyMovePattern;
    }

    public void setEnemyMovePattern(EnemyMovePattern enemyMovePattern) {
        this.enemyMovePattern = enemyMovePattern;
    }
}
