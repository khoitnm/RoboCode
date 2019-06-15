package org.tnmk.robocode.common.model.enemy;

import org.tnmk.robocode.common.gun.pattern.EnemyMovePattern;
import robocode.AdvancedRobot;

public class EnemyStatisticContext {

    private final AdvancedRobot robot;
    /**
     * This field is never null
     */
    private final String enemyName;

    /**
     * This field is never null
     */
    private final EnemyHistory enemyHistory;

    private final EnemyPredictionHistory enemyPredictionHistory;


    /**
     * If this field is null, it means there's no prediction yet.
     */
    private PatternIdentification patternIdentification = null;

    public EnemyStatisticContext(AdvancedRobot robot, String enemyName, EnemyHistory enemyHistory, EnemyPredictionHistory enemyPredictionHistory) {
        this.robot = robot;
        this.enemyName = enemyName;
        this.enemyHistory = enemyHistory;
        this.enemyPredictionHistory = enemyPredictionHistory;
    }

    public PatternIdentification getPatternIdentification() {
        return patternIdentification;
    }

    public void setPatternIdentification(PatternIdentification patternIdentification) {
        this.patternIdentification = patternIdentification;
    }
//
//    public void setPattern(long predictionTime, EnemyMovePattern enemyMovePattern) {
//
//    }
//
//    public void addPrediction(EnemyPrediction enemyPrediction) {
//        this.enemyPredictionHistory.addToHistory(enemyPrediction);
//        this.patternIdentification = PatternIdentificationCertaintyCalculator.calculateHistoryCertainty(this);
//    }

    public boolean hasCertainPattern() {
        return patternIdentification != null
                && patternIdentification.getEnemyMovePattern() != EnemyMovePattern.UNIDENTIFIED
                && patternIdentification.getCertainty() > PatternIdentification.SAFE_CERTAINTY;
    }

    public String getEnemyName() {
        return enemyName;
    }

    public EnemyHistory getEnemyHistory() {
        return enemyHistory;
    }


    public long getLatestPredictionTime() {
        return patternIdentification.getPredictionTime();
    }

    public EnemyPredictionHistory getEnemyPredictionHistory() {
        return enemyPredictionHistory;
    }

    public AdvancedRobot getRobot() {
        return robot;
    }

//
//    public PatternIdentification getFinalPrediction() {
//        return patternIdentification;
//    }
//
//    public void setFinalPrediction(PatternIdentification patternIdentification) {
//        this.patternIdentification = patternIdentification;
//    }
}
