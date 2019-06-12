package org.tnmk.robocode.common.model.enemy;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class EnemyHistory {
    private static final int HISTORY_SIZE = 20;
    private final String name;
    /**
     * The first item is the last updated data.<br/>
     * The oldest data is the last item.<br/>
     * <p/>
     * This list is never empty, and items inside never null.<br/>
     */
    private final List<Enemy> historyItems;

    public EnemyHistory(String name, Enemy historyItem) {
        this.name = name;
        this.historyItems = Collections.synchronizedList(new LinkedList<>());

        addToHistory(historyItem);
    }

    public EnemyHistory(String name, List<Enemy> historyItems) {
        this.name = name;
        this.historyItems = Collections.synchronizedList(new LinkedList<>());

        addToHistory(historyItems);
    }

    public synchronized void addToHistory(Enemy historyItem) {
        historyItems.add(0, historyItem);
        while (historyItems.size() > HISTORY_SIZE) {
            historyItems.remove(historyItems.size() - 1);
        }
    }

    //TODO improve performance
    public synchronized void addToHistory(List<Enemy> historyItems) {
        for (Enemy historyItem : historyItems) {
            addToHistory(historyItem);
        }
    }

    public Enemy getLatestHistoryItem() {
        return historyItems.get(0);
    }

    public List<Enemy> getLatestHistoryItems(int historyItemsCount) {
        int actualItemsCount = Math.min(historyItemsCount, historyItems.size());
        return historyItems.subList(0, actualItemsCount);
    }

    public String getName() {
        return name;
    }
}
