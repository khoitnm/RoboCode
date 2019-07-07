package org.tnmk.robocode.common.helper.prediction;

import java.awt.geom.Point2D;
import org.tnmk.robocode.common.gun.pattern.EnemyMovePattern;
import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.model.enemy.EnemyStatisticContext;

/**
 * TODO refactor this class and {@link EnemyPrediction}
 */
public class EnemyPositionPrediction extends EnemyPrediction{

    public EnemyPositionPrediction(long predictionTime, Point2D predictionPosition) {
        super(EnemyMovePattern.UNIDENTIFIED, predictionTime, predictionPosition, -1 , -1);
    }

    @Override
    public String toString(){
        return String.format("[%s] %s: %s", getPredictionTime(), LogHelper.toString(getPredictionPosition()));
    }
}
