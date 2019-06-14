package org.tnmk.robocode.common.gun.pattern;

import java.util.Random;
import robocode.Rules;

/**
 * Bullet power calculation in case we are using PatternPredictionGun.
 */
public class BulletPowerForPatternPredictionGunHelper {
    private static final double MAX_DISTANCE_TO_REACH_MIN_POWER = 700;
    private static final double MIN_DISTANCE_TO_REACH_MAX_POWER = 300;

    /**
     * This value is different from {@link Rules#MIN_BULLET_POWER}.
     * It represents the minimum power you want to fire, not the allowed minimum power defined by the game.
     */
    private static final double MIN_EXPECT_BULLET_POWER = 1.0d;

    private static final double DELTA_DISTANCE = MAX_DISTANCE_TO_REACH_MIN_POWER - MIN_DISTANCE_TO_REACH_MAX_POWER;
    /**
     * Delta is negative because the the further distance is, the smaller bullet power is.
     */
    private static final double DELTA_BULLET_POWER = MIN_EXPECT_BULLET_POWER - Rules.MAX_BULLET_POWER;
    private static final double BULLET_POWER_DISTRIBUTION_OVER_DISTANCE = DELTA_BULLET_POWER / DELTA_DISTANCE;

    private static final double INCREASE_POWER_FOR_EACH_REMAIN_ENEMIES = 0.15;

    /**
     * @param fireDistance
     * @param movePatternCertainty
     * @param enemiesCount the number of current enemies on the battle field.
     * @return
     * @see #reckonBulletPower(double, double)
     */
    public static double reckonBulletPower(double fireDistance, double movePatternCertainty, double enemiesCount) {
        double bulletPower = reckonBulletPower(fireDistance, movePatternCertainty);
        bulletPower += (enemiesCount * INCREASE_POWER_FOR_EACH_REMAIN_ENEMIES);
        bulletPower = reckonBulletPowerWithinLimit(bulletPower);
        return bulletPower;
    }

    /**
     * @param fireDistance         the distance to target.
     * @param movePatternCertainty from 0.0 to 1.0. The lower number is, the lower certainty is. It means the bullet power will reduce to avoid too much risk.
     * @return bullet power.
     * If it's zero, shouldn't fire bullet.
     */
    private static double reckonBulletPower(double fireDistance, double movePatternCertainty) {
        if (movePatternCertainty <= 0) {
            movePatternCertainty = 0.5;
        }

        double bulletPower = Rules.MAX_BULLET_POWER + (fireDistance - MIN_DISTANCE_TO_REACH_MAX_POWER) * BULLET_POWER_DISTRIBUTION_OVER_DISTANCE * movePatternCertainty;
        return bulletPower;
    }

    /**
     * @param bulletPower
     * @return if bulletPower greater than limit or lower than the limit, adjust it to be within the limit.
     * If the result is zero, it means you shouldn't fire bullet.
     */
    private static double reckonBulletPowerWithinLimit(double bulletPower) {
        double bulletPowerWithinLimit = Math.min(Rules.MAX_BULLET_POWER, bulletPower);
        if (bulletPower > Rules.MAX_BULLET_POWER) {
            bulletPowerWithinLimit = Rules.MAX_BULLET_POWER;
        } else if (bulletPower < MIN_EXPECT_BULLET_POWER) {
            bulletPowerWithinLimit = randomlyShouldFireBullet(bulletPower);
        }
        return bulletPowerWithinLimit;
    }

    /**
     * @param bulletPower bulletPower less than {@link #MIN_EXPECT_BULLET_POWER}.
     * @return randomly return 0 or {@link #MIN_EXPECT_BULLET_POWER}.<br/>
     * If value is 0, it means we shouldn't fire bullet at all.<br/>
     * Otherwise, just fire bullet with minimum power.<br/>
     * The lower bulletPower is, the more chance the result will be zero (shouldn't fire bullet).
     */
    private static double randomlyShouldFireBullet(double bulletPower) {
        //For example: if bulletPower is 0.8, 1 - bulletPower = 0.2 (20%). If we use that number for risk, it's maybe a little bit too high.
        //Don't need to be that conservative, I decide to divide that risk by 4,
        double riskCertainty = (MIN_EXPECT_BULLET_POWER - bulletPower) / 4;
        Random random = new Random();
        int randomInt = random.nextInt(100);
        if ((double) randomInt < riskCertainty * 100) {
            return 0;
        } else {
            return MIN_EXPECT_BULLET_POWER;
        }
    }
}
