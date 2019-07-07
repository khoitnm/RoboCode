package org.tnmk.robocode.common.gun.pattern;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.stream.Collectors;
import org.tnmk.robocode.common.constant.RobotPhysics;
import robocode.Rules;

public class BotBodyFactory {
    public static List<BotBody> constructBotBodies(List<Point2D> botPositions) {
        return botPositions.stream().map(BotBodyFactory::constructBotBody).collect(Collectors.toList());
    }

    public static BotBody constructBotBody(Point2D botPosition) {
        double haftBotSize = RobotPhysics.ROBOT_WIDTH / 2.0d;
        double minX = botPosition.getX() - haftBotSize;
        double minY = botPosition.getY() - haftBotSize;
        Rectangle2D botShape = new Rectangle2D.Double(minX, minY, RobotPhysics.ROBOT_WIDTH, RobotPhysics.ROBOT_WIDTH);
        return new BotBody(botPosition, botShape);
    }
}
