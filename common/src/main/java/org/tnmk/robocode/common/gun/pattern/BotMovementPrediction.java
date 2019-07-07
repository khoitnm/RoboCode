package org.tnmk.robocode.common.gun.pattern;

import org.tnmk.robocode.common.helper.prediction.EnemyPrediction;

public class BotMovementPrediction {
    private final BotMovement botMovement;
    private final EnemyPrediction enemyPrediction;

    public BotMovementPrediction(BotMovement botMovement, EnemyPrediction enemyPrediction) {
        this.botMovement = botMovement;
        this.enemyPrediction = enemyPrediction;
    }

    public BotMovement getBotMovement() {
        return botMovement;
    }

    public EnemyPrediction getEnemyPrediction() {
        return enemyPrediction;
    }
}
