package org.tnmk.robocode.common.model.enemy;

import org.tnmk.robocode.common.helper.prediction.EnemyPrediction;

public class EnemyPredictionHistory extends History<EnemyPrediction> {
    private static final int HISTORY_SIZE = 30;

    private final String enemyName;
    public EnemyPredictionHistory(String enemyName) {
        super(HISTORY_SIZE);
        this.enemyName = enemyName;
    }

    /**
     * @param enemyPrediction
     * @return check the enemyPrediction has newer data compare to this current historyItems.
     */
    public boolean isNewerCurrentHistoryItems(EnemyPrediction enemyPrediction){
        return this.historyItems == null || this.historyItems.isEmpty() || enemyPrediction.getPredictionTime() > getLatestHistoryItem().getPredictionTime();
    }

    public String getEnemyName() {
        return enemyName;
    }
}
