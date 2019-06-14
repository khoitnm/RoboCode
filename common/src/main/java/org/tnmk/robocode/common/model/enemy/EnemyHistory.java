package org.tnmk.robocode.common.model.enemy;

import java.util.List;

public class EnemyHistory extends History<Enemy>{
    private static final int HISTORY_SIZE = 20;
    private final String name;

    public EnemyHistory(String name, Enemy historyItem) {
        super(HISTORY_SIZE);
        this.name = name;
        addToHistory(historyItem);
    }

    public EnemyHistory(String name, List<Enemy> historyItems) {
        super(HISTORY_SIZE);
        this.name = name;
        addToHistory(historyItems);
    }

    public String getName() {
        return name;
    }
}
