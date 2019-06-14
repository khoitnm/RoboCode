package org.tnmk.robocode.common.model.enemy;

import com.sun.istack.internal.NotNull;
import org.tnmk.robocode.common.gun.pattern.EnemyMovePattern;

/**
 * This class represents the prediction of an enemy at a point of time.
 * <p/>
 * While the class {@link EnemyStatisticContext} represents the history of enemy data as well as the history of prediction.
 */
public class PatternIdentification {
    public static final double DEFAULT_CERTAINTY_WHEN_NO_PREDICTION_HISTORY = 0.6;
    private long predictionTime;

    @NotNull
    private EnemyMovePattern enemyMovePattern;

    public PatternIdentification(long predictionTime, EnemyMovePattern enemyMovePattern, double certainty) {
        this.predictionTime = predictionTime;
        this.enemyMovePattern = enemyMovePattern;
        this.certainty = certainty;
    }

    /**
     * The value is from 0.0 to 1.0
     */
    private double certainty = DEFAULT_CERTAINTY_WHEN_NO_PREDICTION_HISTORY;

    public PatternIdentification(long predictionTime, EnemyMovePattern enemyMovePattern) {
        this.predictionTime = predictionTime;
        this.enemyMovePattern = enemyMovePattern;
    }

    //TODO add dominantPattern and dominantPatternCertainty



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
