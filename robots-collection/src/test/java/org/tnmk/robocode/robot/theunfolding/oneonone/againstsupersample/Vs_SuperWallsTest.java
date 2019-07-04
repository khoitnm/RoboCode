package org.tnmk.robocode.robot.theunfolding.oneonone.againstsupersample;

import java.util.Arrays;
import java.util.List;
import org.tnmk.robocode.robot.AbstractWinRateTest;
import org.tnmk.robocode.robot.TestRounds;
import org.tnmk.robocode.robot.TheUnfoldingRobot;
import supersample.SuperWalls;

public class Vs_SuperWallsTest extends AbstractWinRateTest {

    @Override
    public TestConfig constructTestConfig() {
        List<String> enemyNames = Arrays.asList(
                SuperWalls.class.getCanonicalName()
        );

        /**
         * Run 100 (v.2.4.0): 79%
         * - v2.4.2: Apply uTurn: 500 rounds: 76.8%
         */
        TestConfig testConfig = new TestConfig(TheUnfoldingRobot.class, enemyNames, 0.75, TestRounds.NUM_ROUNDS_QUICK);
        return testConfig;
    }
}
