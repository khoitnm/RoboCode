package org.tnmk.robocode.robot.theunfolding;

import java.util.Arrays;
import java.util.List;
import mld.Moebius;
import org.tnmk.robocode.robot.AbstractWinRateTest;
import org.tnmk.robocode.robot.TheUnfoldingRobot;

public class AgainstMoebiusTest extends AbstractWinRateTest {

    @Override
    public TestConfig constructTestConfig() {
        List<String> enemyNames = Arrays.asList(
                Moebius.class.getCanonicalName()
        );

        /**
         * 1000 rounds:
         * - (v2.4.0) RandomMovement: 97.2% (What!!!!!) (Run 100 rounds, win 86.0%!!!)
         *      2000 rounds: 98.55%
         */
        TestConfig testConfig = new TestConfig(TheUnfoldingRobot.class, enemyNames, 0.8, 1000);
        return testConfig;
    }
}
