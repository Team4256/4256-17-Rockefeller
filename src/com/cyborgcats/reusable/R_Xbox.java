package com.cyborgcats.reusable;//COMPLETE

import edu.wpi.first.wpilibj.XboxController;

public class R_Xbox extends XboxController {
	public static final int AXIS_LEFT_X = 0;
	public static final int AXIS_LEFT_Y = 1;
	public static final int AXIS_LT = 2;
	public static final int AXIS_RT = 3;
	public static final int AXIS_RIGHT_X = 4;
	public static final int AXIS_RIGHT_Y = 5;

	public static final int[] STICK_LEFT = new int[] {AXIS_LEFT_X, AXIS_LEFT_Y};
	public static final int[] STICK_RIGHT = new int[] {AXIS_RIGHT_X, AXIS_RIGHT_Y};

	public static final int BUTTON_A = 1;
	public static final int BUTTON_B = 2;
	public static final int BUTTON_X = 3;
	public static final int BUTTON_Y = 4;
	public static final int BUTTON_LB = 5;
	public static final int BUTTON_RB = 6;
	public static final int BUTTON_BACK = 7;
	public static final int BUTTON_START = 8;
	public static final int BUTTON_STICK_LEFT = 9;
	public static final int BUTTON_STICK_RIGHT = 10;

	public static final int POV_NORTH = 0;
	public static final int POV_NORTH_EAST = 45;
	public static final int POV_EAST = 90;
	public static final int POV_SOUTH_EAST = 135;
	public static final int POV_SOUTH = 180;
	public static final int POV_SOUTH_WEST = 225;
	public static final int POV_WEST = 270;
	public static final int POV_NORTH_WEST = 315;
	
	public R_Xbox(final int port) {
		super(port);
	}
	private double[] deadbands = new double[6];
	private double[] previousAxisValues = new double[6];
	private Long[] buttonTimes = new Long[10];
	{
		for (int i = 0; i  < deadbands.length; i++) {
			deadbands[i] = 0.2;
		}
		for (int i = 0; i < previousAxisValues.length; i++) {
			previousAxisValues[i] = 0;
		}
		for (int i = 0; i < buttonTimes.length; i++) {
			buttonTimes[i] = System.currentTimeMillis();
		}
	}
	@Override
	public boolean getRawButton(final int button) {
		buttonTimes[button - 1] = System.currentTimeMillis();
		return super.getRawButton(button);
	}
	/**
	 * This function updates the deadband value for the specified axis.
	 * It returns 0 unless |getRawAxis()| is greater than the deadband,
	 * in which case it returns getRawAxis()
	**/
	public double getDeadbandedAxis(final int axis, final double deadband) {
		deadbands[axis] = deadband;
		return Math.abs(getRawAxis(axis)) <= deadbands[axis] ? 0 : getRawAxis(axis);
	}
	/**
	 * This function returns 0 unless |getRawAxis()| is greater than the stored deadband,
	 * in which case it returns getRawAxis()
	**/
	public double getDeadbandedAxis(final int axis) {
		return getDeadbandedAxis(axis, deadbands[axis]);
	}
	/**
	 * This function returns true if the specified axis' value is greater than the specified minimum.
	 * Otherwise, it returns false.
	**/
	public boolean getAxisPress(final int axis, final double minimum) {
		return Math.abs(getRawAxis(axis)) >= minimum;
	}
	/**
	 * This function returns true if the specified axis' value has changed since the last time it was called.
	 * Otherwise, it returns false.
	**/
	public boolean getAxisActivity(final int axis) {
		final boolean activityBool = Math.abs(getRawAxis(axis) - previousAxisValues[axis]) >= 0.05;
		previousAxisValues[axis] = getRawAxis(axis);
		return activityBool;
	}
	//TODO
	public int getYoungestButton(final int[] buttons) {
		int youngest = buttons[0];
		for (int button : buttons) {
			if (buttonTimes[youngest] < buttonTimes[button]) {youngest = button;}
		}return youngest;
	}
	//TODO
	public Long lastPressTime(final int button) {
		return buttonTimes[button];
	}
	//TODO
	public void resetButtonTimes(final int[] buttons) {
		for (int button : buttons) {
			buttonTimes[button] = System.currentTimeMillis();
		}
	}
	/**
	 * This function returns true if a button is pressed, if an axis value is greater than its stored deadband, or if a POV has an angle.
	 * Otherwise, it returns false.
	**/
	public boolean isActive() {
		for (int i = 1; i < 10; i++) {
			if(getRawButton(i)) {
				return true;
			}
		}
		for (int i = 0; i < deadbands.length; i++) {
			if(getAxisActivity(i)) {
				return true;
			}
		}
		for (int i = 0; i < getPOVCount(); i++) {
			if(getPOV(i) != -1) {
				return true;
			}
		}
		return false;
	}
	/**
	 * This function returns the angle between the specified stick and the Y axis. If deadbanded is true, small movements in the middle are ignored.
	**/
	public double getCurrentAngle(final int[] stick, final boolean deadbanded) {
		final double x = getRawAxis(stick[0]);
		final double y = getRawAxis(stick[1]);
		final boolean badX = Math.abs(x) <= deadbands[stick[0]];
		final boolean badY = Math.abs(y) <= deadbands[stick[1]];
		if (deadbanded && badX && badY) {
			return V_Compass.convertToAngle(previousAxisValues[stick[0]], previousAxisValues[stick[1]]);
		}else {
			previousAxisValues[stick[0]] = x;
			previousAxisValues[stick[1]] = y;
			return V_Compass.convertToAngle(x, y);
		}
	}
	/**
	 * This function returns the length of the hypotenuse formed by the 2 axis of the specified stick. If deadbanded is true, small movements in the middle are ignored.
	**/
	public double getCurrentRadius(final int[] stick, final boolean deadbanded) {
		final double x = getRawAxis(stick[0]);
		final double y = getRawAxis(stick[1]);
		final boolean badX = Math.abs(x) <= deadbands[stick[0]];
		final boolean badY = Math.abs(y) <= deadbands[stick[1]];
		if (deadbanded && badX && badY) {
			return 0;
		}else {
			return Math.sqrt(x*x + y*y);
		}
	}
}
