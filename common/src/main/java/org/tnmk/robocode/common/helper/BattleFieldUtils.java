package org.tnmk.robocode.common.helper;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import robocode.Robot;

public class BattleFieldUtils {
    public static Rectangle2D constructBattleField(Robot robot) {
        return new Rectangle2D.Double(0, 0, robot.getBattleFieldWidth(), robot.getBattleFieldHeight());
    }

    public static Point2D constructRobotPosition(Robot robot) {
        return new Point2D.Double(robot.getX(), robot.getY());
    }

    public static Rectangle2D[][] splitToParts(Rectangle2D battleField, int xParts, int yParts) {
        double partWidth = battleField.getWidth() / xParts;
        double partHeight = battleField.getHeight() / yParts;
        Rectangle2D[][] battleFieldParts = new Rectangle2D[xParts][yParts];
        for (int i = 0; i < xParts; i++) {
            for (int j = 0; j < yParts; j++) {
                double minX = battleField.getMinX() + i * partWidth;
                double maxX = minX + partWidth;

                double minY = battleField.getMinY() + j * partHeight;
                double maxY = minY + partHeight;
                battleFieldParts[i][j] = new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
            }
        }
        return battleFieldParts;
    }
}
