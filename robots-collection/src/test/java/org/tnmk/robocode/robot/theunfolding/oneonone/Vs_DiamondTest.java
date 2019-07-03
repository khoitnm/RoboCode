package org.tnmk.robocode.robot.theunfolding.oneonone;

import java.util.Arrays;
import java.util.List;
import org.tnmk.robocode.robot.AbstractWinRateTest;
import org.tnmk.robocode.robot.TestRounds;
import org.tnmk.robocode.robot.TheUnfoldingRobot;
import voidiousdiamond.voidious.Diamond;

public class Vs_DiamondTest extends AbstractWinRateTest {

    @Override
    public TestConfig constructTestConfig() {
        List<String> enemyNames = Arrays.asList(
                Diamond.class.getCanonicalName()
        );


        TestConfig testConfig = new TestConfig(TheUnfoldingRobot.class, enemyNames, 0.01, TestRounds.NUM_ROUNDS_QUICK);
        return testConfig;
    }
}
