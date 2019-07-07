package org.tnmk.robocode.common.gun.pattern;

import java.awt.geom.Point2D;

public class BotMovementPrediction {
    private final BotMovement botMovement;
    private final Point2D predictPosition;

    public BotMovementPrediction(BotMovement botMovement, Point2D predictPosition) {
        this.botMovement = botMovement;
        this.predictPosition = predictPosition;
    }

    public BotMovement getBotMovement() {
        return botMovement;
    }

    public Point2D getPredictPosition() {
        return predictPosition;
    }
}
