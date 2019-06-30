package org.tnmk.robocode.common.robot.state;

import org.junit.Assert;
import org.junit.Test;
import robocode.AdvancedRobot;

public class AdvanceRobotStateMapperTest {
    private static final double DOUBLE_COMPARE_PRECISION = 0.0001;

    @Test
    public void testStateMapper() {
        AdvancedRobot robot = AdvanceRobotFactory.constructAdvanceRobot();

        AdvanceRobotState state = AdvanceRobotStateMapper.toState(robot);

        Assert.assertEquals(robot.getName(), state.getName());
        Assert.assertEquals(robot.getTime(), state.getTime(), DOUBLE_COMPARE_PRECISION);
        Assert.assertEquals(robot.getEnergy(), state.getEnergy(), DOUBLE_COMPARE_PRECISION);
        Assert.assertEquals(robot.getX(), state.getPosition().getX(), DOUBLE_COMPARE_PRECISION);
        Assert.assertEquals(robot.getY(), state.getPosition().getY(), DOUBLE_COMPARE_PRECISION);

        Assert.assertEquals(robot.getVelocity(), state.getVelocity(), DOUBLE_COMPARE_PRECISION);
        Assert.assertEquals(robot.getHeading(), state.getHeading(), DOUBLE_COMPARE_PRECISION);
        Assert.assertEquals(robot.getGunHeading(), state.getGunHeading(), DOUBLE_COMPARE_PRECISION);
        Assert.assertEquals(robot.getRadarHeading(), state.getRadarHeading(), DOUBLE_COMPARE_PRECISION);

        Assert.assertEquals(robot.getDistanceRemaining(), state.getDistanceRemaining(), DOUBLE_COMPARE_PRECISION);
        Assert.assertEquals(robot.getTurnRemaining(), state.getTurnRemaining(), DOUBLE_COMPARE_PRECISION);
        Assert.assertEquals(robot.getGunTurnRemaining(), state.getGunTurnRemaining(), DOUBLE_COMPARE_PRECISION);
        Assert.assertEquals(robot.getRadarTurnRemaining(), state.getRadarTurnRemaining(), DOUBLE_COMPARE_PRECISION);

        Assert.assertEquals(robot.getGunHeat(), state.getGunHeat(), DOUBLE_COMPARE_PRECISION);
        Assert.assertEquals(robot.getOthers(), state.getOthers());
        Assert.assertEquals(robot.getRoundNum(), state.getRoundNum());
        Assert.assertEquals(robot.getNumSentries(), state.getNumSentries());

    }

}
