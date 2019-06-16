package org.tnmk.robocode.common;


import org.junit.Assert;
import org.junit.Test;
import org.tnmk.robocode.common.gun.pattern.BulletPowerHelper;

public class BulletPowerHelperTest {

    @Test
    public void testBulletPowerWhenLowEnergy(){
        int testCount = 100;
        int numBulletPowerMoreThanZero = 0;
        for (int i = 0; i < testCount; i++) {
            double bulletPower = BulletPowerHelper.reckonBulletPower(500,2, 8d);
            if (bulletPower > 0){
                numBulletPowerMoreThanZero++;
            }
        }
        System.out.println("Has bulletPower times: "+numBulletPowerMoreThanZero);
        Assert.assertTrue("Has bulletPower ",numBulletPowerMoreThanZero > 50);
    }
}
