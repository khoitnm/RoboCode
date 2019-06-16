package org.tnmk.robocode.robot;

import java.util.Arrays;
import java.util.List;
import mld.Moebius;
import pez.micro.BlackWidow;
import wiki.mako.MakoHT;
import wiki.mini.GouldingiHT;

public class TheUnfoldingRobotVsMoebiusAndOthersTest extends AbstractWinRateTest {

    @Override
    public TestConfig constructTestConfig() {
        List<String> enemyNames = Arrays.asList(
                Moebius.class.getCanonicalName()
                , BlackWidow.class.getCanonicalName()
                , MakoHT.class.getCanonicalName()
                , GouldingiHT.class.getCanonicalName()
        );

        /**
         * At 2019-06-16, The champion percentage is 66.1% over 1000 rounds (matches)
         * Moebius' gun is good, but his gun alone cannot touch TheUnfoldingRobot!
         */
        TestConfig testConfig  = new TestConfig(TheUnfoldingRobot.class,enemyNames, 0.7, 20);
        return testConfig;
    }
}
