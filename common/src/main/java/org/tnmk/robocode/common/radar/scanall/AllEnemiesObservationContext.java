package org.tnmk.robocode.common.radar.scanall;

import robocode.AdvancedRobot;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This context data will be share by Radar, Movement and Gun.
 * However, data inside this should be added by Radar only. It shouldn't be added by Movement.
 */
public class AllEnemiesObservationContext {
    private final AdvancedRobot robot;

    private final Map<String, Enemy> enemiesMapByName = Collections.synchronizedMap(new HashMap<>());

    public AllEnemiesObservationContext(AdvancedRobot robot) {
        this.robot = robot;
    }

    public Map<String, Enemy> getEnemiesMapByName() {
        return enemiesMapByName;
    }

    public void addEnemy(Enemy enemy){
        this.enemiesMapByName.put(enemy.getName(), enemy);
    }

    public int countEnemies(){
        return this.enemiesMapByName.size();
    }

    public Enemy getEnemy(String enemyName){
        return this.enemiesMapByName.get(enemyName);
    }

    public void removeEnemy(String enemyName) {
        this.enemiesMapByName.remove(enemyName);
    }
}
