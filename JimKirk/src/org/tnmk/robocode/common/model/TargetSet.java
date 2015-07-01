package org.tnmk.robocode.common.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * Each Robot has a unique name.
 * @author Khoi
 * 
 * @param <E>
 */
public class TargetSet<E extends PredictedTarget> extends HashMap<String, E>{
    private static final long serialVersionUID = 649185821597070216L;

	public List<E> list() {
		Set<Map.Entry<String, E>> set = super.entrySet();
		List<E> result = new ArrayList<>(set.size());
		for (Map.Entry<String, E> entry : set) {
			result.add(entry.getValue());
		}
		return result;
	}
	public int size(){
		return this.entrySet().size();
	}
	public void set(E e){
		super.put(e.getState().getName(), e);
	}

	public boolean containsRobot(String name) {
	   return get(name) != null;
    }
}
