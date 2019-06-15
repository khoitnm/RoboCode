package org.tnmk.robocode.common.model.enemy;

import com.sun.istack.internal.NotNull;
import org.tnmk.robocode.common.gun.pattern.EnemyMovePattern;

/**
 * This class represents the prediction of an enemy at a point of time.
 * <p/>
 * While the class {@link EnemyStatisticContext} represents the history of enemy data as well as the history of prediction.
 */
public class PatternIdentification {
    public static final double DEFAULT_CERTAINTY_WHEN_NO_PREDICTION_HISTORY = 0.5;
    public static final double SAFE_CERTAINTY = 0.8;

    private final long predictionTime;

    @NotNull
    private final EnemyMovePattern enemyMovePattern;
    /**
     * The value is from 0.0 to 1.0
     */
    private final double certainty;
    //TODO add dominantPattern and dominantPatternCertainty

    public PatternIdentification(long predictionTime, EnemyMovePattern enemyMovePattern, double certainty) {
        this.predictionTime = predictionTime;
        this.enemyMovePattern = enemyMovePattern;
        this.certainty = certainty;
    }

    @Override
    public String toString() {
        return String.format("{%s - %s %.2f}", predictionTime, enemyMovePattern, certainty);
    }


    public double getCertainty() {
        return certainty;
    }

    public long getPredictionTime() {
        return predictionTime;
    }

    public EnemyMovePattern getEnemyMovePattern() {
        return enemyMovePattern;
    }
}
