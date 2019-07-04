package org.tnmk.robocode.robot.theunfolding.oneonone;

import java.util.Arrays;
import java.util.List;
import org.tnmk.robocode.robot.AbstractWinRateTest;
import org.tnmk.robocode.robot.TestRounds;
import org.tnmk.robocode.robot.TheUnfoldingRobot;
import rz.HawkOnFireOS;

public class Vs_HawkOnFireTest extends AbstractWinRateTest {

    @Override
    public TestConfig constructTestConfig() {
        List<String> enemyNames = Arrays.asList(
                HawkOnFireOS.class.getCanonicalName()
        );


        TestConfig testConfig = new TestConfig(TheUnfoldingRobot.class, enemyNames, 0.85, TestRounds.NUM_ROUNDS_QUICK);
        return testConfig;
    }
}
