package org.tnmk.robocode.common.model.enemy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class History<T> {
    protected int historySize;
    /**
     * The first item is the last updated data.<br/>
     * The oldest data is the last item.<br/>
     * <p/>
     * This list is never empty, and items inside never null.<br/>
     */
    protected final List<T> historyItems;

    public History(int historySize) {
        this.historySize = historySize;
        this.historyItems = Collections.synchronizedList(new LinkedList<>());
    }

    public synchronized void addToHistory(T historyItem) {
        historyItems.add(0, historyItem);
        while (historyItems.size() > historySize) {
            historyItems.remove(historyItems.size() - 1);
        }
    }

    //TODO improve performance
    public synchronized void addToHistory(List<T> historyItems) {
        for (T historyItem : historyItems) {
            addToHistory(historyItem);
        }
    }

    public int countHistoryItems() {
        return historyItems.size();
    }

    public T getLatestHistoryItem() {
        return historyItems.get(0);
    }

    public List<T> getAllHistoryItems() {
        return new ArrayList<>(historyItems);
    }

    public List<T> getLatestHistoryItems(int historyItemsCount) {
        int actualItemsCount = Math.min(historyItemsCount, historyItems.size());
        return historyItems.subList(0, actualItemsCount);
    }
}
