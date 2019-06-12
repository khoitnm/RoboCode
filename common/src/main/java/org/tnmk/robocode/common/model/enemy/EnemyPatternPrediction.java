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
    /**
     * This field is never null
     */
    private EnemyPatternType enemyPatternType = EnemyPatternType.UNIDENTIFIED;

    public EnemyPatternPrediction(String enemyName, EnemyHistory enemyHistory) {
        this.enemyName = enemyName;
        this.enemyHistory = enemyHistory;
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

    public void setEnemyPatternType(EnemyPatternType enemyPatternType) {
        this.enemyPatternType = enemyPatternType;
    }
}
