package org.tnmk.robocode.common.helper;

import java.awt.geom.Rectangle2D;
import robocode.Robot;

public class BattleFieldUtils {
    public static Rectangle2D constructBattleField(Robot robot) {
        return new Rectangle2D.Double(0, 0, robot.getBattleFieldWidth(), robot.getBattleFieldHeight());
    }
}
