package org.tnmk.common.collection;

import org.tnmk.robocode.common.movement.strategy.antigravity.RiskArea;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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
     * @param list
     * @param keyExtractor The function to get value from item in the list. For example, <code>{@link RiskArea}::getRisk()</code>
     * @param <E>          the type of item
     * @param <U>          the type of item's attribute which we want to compare.
     * @return
     */
    public static <E, U extends Comparable<? super U>> List<E> findLeastValueItems(List<E> list, Function<? super E, ? extends U> keyExtractor) {
        List<E> leastValueItems = new ArrayList<>();
        U leastValue = null;
        for (E item : list) {
            U itemValue = keyExtractor.apply(item);
            if (leastValue == null || leastValue.compareTo(itemValue) > 0) {
                leastValue = itemValue;
                leastValueItems = new ArrayList<>();
                leastValueItems.add(item);
            } else if (leastValue.compareTo(itemValue) == 0) {
                leastValueItems.add(item);
            }
        }
        return leastValueItems;
    }
}
