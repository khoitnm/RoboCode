package org.tnmk.robocode.robot.theunfolding;

import java.util.Arrays;
import java.util.List;
import jekl.mini.BlackPearl;
import org.tnmk.robocode.robot.AbstractWinRateTest;
import org.tnmk.robocode.robot.TheUnfoldingRobot;

public class AgainstBlackPearlTest extends AbstractWinRateTest {

    @Override
    public TestConfig constructTestConfig() {
        List<String> enemyNames = Arrays.asList(
                BlackPearl.class.getCanonicalName()
        );

        /**
         * Run 1000 rounds with win percentage:
         * - OscillatorMovement  for 1-on-1:
         * - RandomMovement for 1-on-1: 39.1%
         */
        TestConfig testConfig = new TestConfig(TheUnfoldingRobot.class, enemyNames, 0.8, 1000);
        return testConfig;
    }
}
