package org.tnmk.robocode.common.robot;

import robocode.HitByBulletEvent;

public interface OnHitBulletControl {
    // Check if we are hit with full lead aim
    // Adapted from Axe's Musashi: http://robowiki.net/?Musashi
    void onHitByBullet(HitByBulletEvent hitByBulletEvent);
}
