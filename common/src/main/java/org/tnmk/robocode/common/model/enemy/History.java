package org.tnmk.robocode.common.model.enemy;

import java.util.*;

public class History<T> {
    protected int historySize;
    /**
     * The first item is the last updated data.<br/>
     * The oldest data is the last item.<br/>
     * <p/>
     * This list should never be empty, and items inside never null.<br/>
     * FIXME I think the not empty condition is weird!!! Do we really need that prerequisite for other function to run? There could be a risk in {@link #getLatestHistoryItem()} if we don't have this prerequisite.
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
        if (historyItems.isEmpty()) {
            throw new IllegalStateException("The history is empty, cannot get the latest item");
        }
        return historyItems.get(0);
    }

    /**
     * This method will provide a clone list of the original {@link #historyItems}.<br/>
     * So even if the client code change the result list, the original list is still intact.
     * <p/>
     * If you just need to iterate the original list, use {@link #getAllHistoryItemsIterable()} instead to have a better performance and save more memory.
     *
     * @return
     */
    public List<T> getAllHistoryItems() {
        return new ArrayList<>(historyItems);
    }

    /**
     * This method provides a better performance and save more memory compare to {@link #getAllHistoryItems()}.
     * But we don't want client code use this to add/remove items in the original list which is easily cause side-effect.
     * @return
     */
    public Iterable<T> getAllHistoryItemsIterable(){
        return historyItems;
    }

    public List<T> getLatestHistoryItems(int historyItemsCount) {
        int actualItemsCount = Math.min(historyItemsCount, historyItems.size());
        return historyItems.subList(0, actualItemsCount);
    }

    public boolean isEmpty() {
        return historyItems.isEmpty();
    }
}
