package org.tnmk.robocode.common.gun.pattern;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.tnmk.robocode.common.log.LogHelper;

public class BotBody {
    private final Point2D position;
    private final Rectangle2D botShape;

    public BotBody(Point2D position, Rectangle2D botShape) {
        this.position = position;
        this.botShape = botShape;
    }

    @Override
    public String toString() {
        return "BotBody{" +
                "position=" + LogHelper.toString(position) +
                ", botShape=" + LogHelper.toString(botShape) +
                '}';
    }

    public Point2D getPosition() {
        return position;
    }

    public Rectangle2D getBotShape() {
        return botShape;
    }
}
