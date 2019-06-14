package org.tnmk.robocode.common.model.enemy;

import org.tnmk.robocode.common.gun.pattern.EnemyPatternType;

public class  EnemyPatternPrediction {
    /**
     * This field is never null
     */
    private final String enemyName;

    /**
     * This field is never null
     */
    private final EnemyHistory enemyHistory;
    //TODO add the time we predicted pattern so that we should predict pattern again after some time.
    /**
     * This field is never null
     */
    private EnemyPatternType enemyPatternType = EnemyPatternType.UNIDENTIFIED;

    private long predictedTime;

    public EnemyPatternPrediction(String enemyName, EnemyHistory enemyHistory) {
        this.enemyName = enemyName;
        this.enemyHistory = enemyHistory;
    }

    public void setEnemyPatternType(long predictionTime, EnemyPatternType enemyPatternType) {
        this.enemyPatternType = enemyPatternType;
        this.predictedTime = predictionTime;
    }

    public boolean isIdentifiedPattern() {
        return enemyPatternType != EnemyPatternType.UNIDENTIFIED;
    }

    public String getEnemyName() {
        return enemyName;
    }

    public EnemyHistory getEnemyHistory() {
        return enemyHistory;
    }

    public EnemyPatternType getEnemyPatternType() {
        return enemyPatternType;
    }



    public long getPredictedTime() {
        return predictedTime;
    }

    public void setPredictedTime(long predictedTime) {
        this.predictedTime = predictedTime;
    }
}
