package com.cyborgcats.reusable;

import java.util.HashMap;
import java.util.Map;

public abstract class V_Fridge {
	private static final Map<String, Boolean> previousStates = new HashMap<String, Boolean>();
	public static final Map<String, Boolean> toggleStates = new HashMap<String, Boolean>();
	public static final Map<String, Long> toggledTimes = new HashMap<String, Long>();
	
	private static void initialize(final String key) {
		if (previousStates.get(key) == null) {
			previousStates.put(key, false);
		}if (toggleStates.get(key) == null) {
			toggleStates.put(key, false);
		}if (toggledTimes.get(key) == null) {
			toggledTimes.put(key, System.currentTimeMillis());
		}
	}
	
	public static boolean freeze(final String key, final boolean currentState) {
		initialize(key);
		if (currentState && (currentState != previousStates.get(key))) {
			boolean toggleBool = !toggleStates.get(key);
			toggleStates.replace(key, toggleBool);
			toggledTimes.replace(key, System.currentTimeMillis());
		}
		previousStates.replace(key, currentState);
		return toggleStates.get(key);
	}
	
	public static boolean becomesTrue(final String key, final boolean currentState) {
		initialize(key);
		if (currentState && (currentState != previousStates.get(key))) {
			boolean toggleBool = !toggleStates.get(key);
			toggleStates.replace(key, toggleBool);
			toggledTimes.replace(key, System.currentTimeMillis());
			previousStates.replace(key, currentState);
			return true;
		}else {
			previousStates.replace(key, currentState);
			return false;
		}
	}
	
	public static boolean chill(final String key, final boolean currentState, final double timeoutMS) {
		freeze(key, currentState);
		return System.currentTimeMillis() - toggledTimes.get(key) <= timeoutMS;
	}
	
	/**
	 * freeze() must be called in order to update states right before this function runs.
	 * This returns null if all of the specified toggles are false.
	**/
	public static String youngest(final String[] keys, final boolean onlyLookAtFrozen) {
		String youngestKey = null;
		for (String key : keys) {
			Long time = toggledTimes.get(key);
			if ((!onlyLookAtFrozen || toggleStates.get(key)) && time.compareTo(toggledTimes.get(youngestKey)) > 0) {
				youngestKey = key;
			}
		}return youngestKey;
	}
}