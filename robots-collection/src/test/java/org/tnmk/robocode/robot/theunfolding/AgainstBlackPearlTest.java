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
         * - OscillatorMovement  for 1-on-1: 1.0% (OMG, it's was so so bad!!!)
         * - RandomMovement for 1-on-1 (v2.4.0): 39.1%, 33.6%
         *      Run 2000 rounds: win 64.05%
         *
         */
        TestConfig testConfig = new TestConfig(TheUnfoldingRobot.class, enemyNames, 0.33, 2000);
        return testConfig;
    }
}
