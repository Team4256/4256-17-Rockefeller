package com.cyborgcats.reusable;

import java.util.HashMap;
import java.util.Map;

public abstract class V_Fridge {
	private static Map<String, Boolean> previousStates = new HashMap<String, Boolean>();
	public static Map<String, Boolean> toggleStates = new HashMap<String, Boolean>();
	public static Map<String, Long> stickyTimes = new HashMap<String, Long>();
	
	public static boolean freeze(final String key, final boolean currentState) {
		if (previousStates.get(key) == null) {
			previousStates.put(key, false);
		}if (toggleStates.get(key) == null) {
			toggleStates.put(key, false);
		}if (currentState && (currentState != previousStates.get(key))) {
			boolean toggleBool = !toggleStates.get(key);
			toggleStates.replace(key, toggleBool);
		}
		previousStates.replace(key, currentState);
		return toggleStates.get(key);
	}
	
	public static boolean chill(final String key, final boolean currentState, final double timeoutMS) {
		if (previousStates.get(key) == null) {
			previousStates.put(key, false);
		}if (currentState && (currentState != previousStates.get(key))) {
			stickyTimes.putIfAbsent(key, System.currentTimeMillis());
			stickyTimes.replace(key, System.currentTimeMillis());
		}
		previousStates.replace(key, currentState);
		if (stickyTimes.get(key) != null) {
			return System.currentTimeMillis() - stickyTimes.get(key) <= timeoutMS;
		}else {
			return false;
		}
	}
}