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
         * RandomMovement:
         */
        TestConfig testConfig = new TestConfig(TheUnfoldingRobot.class, enemyNames, 0.8, 1000);
        return testConfig;
    }
}
