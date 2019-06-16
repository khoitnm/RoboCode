package org.tnmk.robocode.robot;

import java.util.Arrays;
import java.util.List;
import pez.micro.BlackWidow;
import wiki.mako.MakoHT;
import wiki.mini.GouldingiHT;

public class TheUnfoldingRobotVsBlackPearlAndOthersTest extends AbstractWinRateTest {

    @Override
    public TestConfig constructTestConfig() {
        List<String> enemyNames = Arrays.asList(
                BlackPearl.class.getCanonicalName()
                , GouldingiHT.class.getCanonicalName()
                , BlackWidow.class.getCanonicalName()
                , MakoHT.class.getCanonicalName()
        );

        /**
         * At 2019-06-16, The champion percentage is 34.69% (0.3469) which was tested with 1000 rounds (matches).
         * The toughest opponents are BlackPearl.
         */
        TestConfig testConfig = new TestConfig(TheUnfoldingRobot.class, enemyNames, 0.5, 20);
        return testConfig;
    }
}
