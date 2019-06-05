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
		}else{
			for (int i = 0; i < count; i++) {
	            rs.add(list.get(i));
            }
		}
		return rs;
	}
}
