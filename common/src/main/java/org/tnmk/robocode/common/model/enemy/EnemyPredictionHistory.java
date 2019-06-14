package org.tnmk.robocode.common.model.enemy;

import org.tnmk.robocode.common.gun.pattern.EnemyMovePattern;
import org.tnmk.robocode.common.helper.prediction.EnemyPrediction;

public class EnemyPredictionHistory extends History<EnemyPrediction>{
    private static final int ENEMY_MOVE_PATTERN_PREDICTION_SIZE = 20;
    /**
     * This field is never null
     */
    private final String enemyName;

    /**
     * This field is never null
     */
    private final EnemyHistory enemyHistory;

    //TODO add the time we predicted pattern so that we should predict pattern again after some time.
    //  Use a List<EnemyPrediction>
    //  And a EnemyPrediction finalPrediction;
    /**
     * This field is never null
     * @deprecated use {@link #finalPrediction} instead.
     */
    @Deprecated
    private EnemyMovePattern enemyMovePattern = EnemyMovePattern.UNIDENTIFIED;

    /**
     * @deprecated use {@link #finalPrediction} instead.
     */
    @Deprecated
    private long predictedTime;

    private EnemyPrediction finalPrediction;

    public EnemyPredictionHistory(String enemyName, EnemyHistory enemyHistory) {
        super(ENEMY_MOVE_PATTERN_PREDICTION_SIZE);
        this.enemyName = enemyName;
        this.enemyHistory = enemyHistory;
    }

    //FIXME
    public void setEnemyPatternType(long predictionTime, EnemyMovePattern enemyMovePattern) {
        this.enemyMovePattern = enemyMovePattern;
        this.predictedTime = predictionTime;
    }

    public boolean isIdentifiedPattern() {
        return enemyMovePattern != EnemyMovePattern.UNIDENTIFIED;
    }

    public String getEnemyName() {
        return enemyName;
    }

    public EnemyHistory getEnemyHistory() {
        return enemyHistory;
    }

    public EnemyMovePattern getEnemyMovePattern() {
        return enemyMovePattern;
    }



    public long getPredictedTime() {
        return predictedTime;
    }

    public void setPredictedTime(long predictedTime) {
        this.predictedTime = predictedTime;
    }

    public EnemyPrediction getFinalPrediction() {
        return finalPrediction;
    }

    public void setFinalPrediction(EnemyPrediction finalPrediction) {
        this.finalPrediction = finalPrediction;
    }
}
