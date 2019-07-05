package org.tnmk.robocode.common.gun.pattern;

import org.tnmk.robocode.common.helper.prediction.EnemyPrediction;
import org.tnmk.robocode.common.model.enemy.Enemy;

import java.awt.geom.Rectangle2D;
import java.util.List;

public interface PatternPredictionFunction {
    /**
     * @param latestHistoryItems
     * @param timeWhenBulletReachEnemy
     * @param enemyMovementBoundaryAre this's is usually the battle field. Or the battle field excluded the sentry bot area.
     * @return
     */
    EnemyPrediction predictEnemy(List<Enemy> latestHistoryItems, long timeWhenBulletReachEnemy, Rectangle2D enemyMovementBoundaryAre);
}
