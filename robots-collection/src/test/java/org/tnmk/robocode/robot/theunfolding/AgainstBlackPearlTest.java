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
         * - OscillatorMovement  for 1-on-1: 1.0% (OMG!!!)
         * - RandomMovement for 1-on-1: 39.1%, 33.6%
         */
        TestConfig testConfig = new TestConfig(TheUnfoldingRobot.class, enemyNames, 0.33, 2000);
        return testConfig;
    }
}
