package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.Joystick;

public class R_Xbox4256 extends Joystick {
	public static final int AXIS_LEFT_X = 0;
	public static final int AXIS_LEFT_Y = 1;
	public static final int AXIS_LT = 2;
	public static final int AXIS_RT = 3;
	public static final int AXIS_RIGHT_X = 4;
	public static final int AXIS_RIGHT_Y = 5;
	
	public static final int BUTTON_A = 1;
	public static final int BUTTON_B = 2;
	public static final int BUTTON_X = 3;
	public static final int BUTTON_Y = 4;
	public static final int BUTTON_LB = 5;
	public static final int BUTTON_RB = 6;
	public static final int BUTTON_BACK = 7;
	public static final int BUTTON_START = 8;
	public static final int BUTTON_LEFT_STICK = 9;
	public static final int BUTTON_RIGHT_STICK = 10;
	public static final int BUTTON_XBOX = 11;
	public static final int BUTTON_NORTH = 12;
	public static final int BUTTON_SOUTH = 13;
	public static final int BUTTON_WEST = 14;
	public static final int BUTTON_EAST = 15;
	
	public static final int POV_NORTH = 0;
	public static final int POV_NORTH_EAST = 45;
	public static final int POV_EAST = 90;
	public static final int POV_SOUTH_EAST = 135;
	public static final int POV_SOUTH = 180;
	public static final int POV_SOUTH_WEST = 225;
	public static final int POV_WEST = 270;
	public static final int POV_NORTH_WEST = 315;
	
	public R_Xbox4256(final int port) throws IllegalStateException {
		super(port);
		if (!getIsXbox()) {
			throw new IllegalStateException("Xbox4256 was used with something other than an xbox controller.");
		}
	}
	private double[] deadbands = new double[getAxisCount()];
	private double[] previousAxisValues = new double[getAxisCount()];
	{
		for (int i = 0; i  <= deadbands.length; i++) {
			deadbands[i] = 0.2;
		}
		for (int i = 0; i <= previousAxisValues.length; i++) {
			previousAxisValues[i] = 0.0;
		}
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
		return getRawAxis(axis) >= minimum;
	}
	/**
	 * This function returns true if the specified axis' value has changed since the last time it was called.
	 * Otherwise, it returns false.
	**/
	public boolean getAxisActivity(final int axis) {
		boolean activityBool = Math.abs(getRawAxis(axis) - previousAxisValues[axis]) <= 0.05 ? false : true;
		previousAxisValues[axis] = getRawAxis(axis);
		return activityBool;
	}
	/**
	 * This function returns true if a button is pressed, if an axis value is greater than its stored deadband, or if a POV has an angle.
	 * Otherwise, it returns false.
	**/
	public boolean isActive() {
		for(int i = 1; i<=getButtonCount(); i++) {
			if(getRawButton(i)) {
				return true;
			}
		}
		for(int i = 0; i<getAxisCount(); i++) {
			if(getAxisActivity(i)) {
				return true;
			}
		}
		for(int i = 0; i<getPOVCount(); i++) {
			if(getPOV(i) != -1) {
				return true;
			}
		}
		return false;
	}
}