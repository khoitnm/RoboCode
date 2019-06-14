package org.tnmk.robocode.common.model.enemy;

import org.tnmk.robocode.common.gun.pattern.EnemyMovePattern;

public class EnemyPredictionHistory {
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
     */
    private EnemyMovePattern enemyMovePattern = EnemyMovePattern.UNIDENTIFIED;

    private long predictedTime;

    public EnemyPredictionHistory(String enemyName, EnemyHistory enemyHistory) {
        this.enemyName = enemyName;
        this.enemyHistory = enemyHistory;
    }

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
}
