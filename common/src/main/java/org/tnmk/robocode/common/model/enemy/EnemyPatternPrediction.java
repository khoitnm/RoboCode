package org.tnmk.robocode.common.model.enemy;

import org.tnmk.robocode.common.gun.pattern.EnemyPattern;

public class EnemyPatternPrediction {
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
    private EnemyPattern enemyPattern = EnemyPattern.UNIDENTIFIED;

    public EnemyPatternPrediction(String enemyName, EnemyHistory enemyHistory) {
        this.enemyName = enemyName;
        this.enemyHistory = enemyHistory;
    }

    public boolean isIdentifiedPattern() {
        return enemyPattern != EnemyPattern.UNIDENTIFIED;
    }

    public String getEnemyName() {
        return enemyName;
    }

    public EnemyHistory getEnemyHistory() {
        return enemyHistory;
    }

    public EnemyPattern getEnemyPattern() {
        return enemyPattern;
    }

    public void setEnemyPattern(EnemyPattern enemyPattern) {
        this.enemyPattern = enemyPattern;
    }
}
