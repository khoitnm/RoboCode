package org.tnmk.common.collection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.tnmk.robocode.common.movement.strategy.antigravity.RiskArea;

public final class ListUtils {
    public static <E> List<E> firstElements(List<E> list, int count) {
        if (list == null) {
            return null;
        }
        ArrayList<E> rs = new ArrayList<>();
        if (list.size() <= count) {
            rs.addAll(list);
        } else {
            for (int i = 0; i < count; i++) {
                rs.add(list.get(i));
            }
        }
        return rs;
    }

    public static <E> List<E> toList(E[][] array2D) {
        List<E> list = new ArrayList<>();
        for (int i = 0; i < array2D.length; i++) {
            E[] row = array2D[i];
            for (int j = 0; j < row.length; j++) {
                list.add(row[j]);
            }
        }
        return list;
    }


    /**
     * //TODO improve performance: don't need sorting.
     *
     * @param list
     * @param keyExtractor The function to get value from item in the list. For example, <code>{@link RiskArea}::getRisk()</code>
     * @param <E>          the type of item
     * @param <U>          the type of item's attribute which we want to compare.
     * @return
     */
    public static <E, U extends Comparable<? super U>> List<E> findLeastValueItems(List<E> list, Function<? super E, ? extends U> keyExtractor) {
        List<E> sortedByValueItems = list.stream()
                .sorted(Comparator.comparing(keyExtractor))
                .collect(Collectors.toList());
        List<E> leastValueItems = new ArrayList<>();
        U leastValue = null;
        for (E sortedItem : sortedByValueItems) {
            U sortedValue = keyExtractor.apply(sortedItem);
            if (leastValue == null || leastValue.compareTo(sortedValue) >= 0) {
                leastValue = sortedValue;
                leastValueItems.add(sortedItem);
            } else {
                break;
            }
        }
        return leastValueItems;
    }
}
