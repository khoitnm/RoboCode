package org.tnmk.robocode.robot.theunfolding.oneonone.againstsupersample;

import java.util.Arrays;
import java.util.List;
import org.tnmk.robocode.robot.AbstractWinRateTest;
import org.tnmk.robocode.robot.TestRounds;
import org.tnmk.robocode.robot.TheUnfoldingRobot;
import supersample.SuperRamFire;

public class AgainstSuperRamFireTest extends AbstractWinRateTest {

    @Override
    public TestConfig constructTestConfig() {
        List<String> enemyNames = Arrays.asList(
                SuperRamFire.class.getCanonicalName()
        );

        /**
         * Run 100 (v.2.4.0): 78%, 84.0%
         * - v2.4.2: Apply uTurn: 500 rounds: 68.4%
         */
        TestConfig testConfig = new TestConfig(TheUnfoldingRobot.class, enemyNames, 0.7, TestRounds.NUM_ROUNDS_QUICK);
        return testConfig;
    }
}
