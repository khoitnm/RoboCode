package org.tnmk.common.collection;

import java.util.ArrayList;
import java.util.List;

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
}
