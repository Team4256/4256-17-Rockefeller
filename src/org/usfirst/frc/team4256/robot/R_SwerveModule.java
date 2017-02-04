package org.usfirst.frc.team4256.robot;

import com.cyborgcats.reusable.R_CANTalon;
import com.cyborgcats.reusable.V_Compass;

import edu.wpi.first.wpilibj.Talon;

public class R_SwerveModule {
	private R_CANTalon rotator;
	private Talon drive1;
	private Talon drive2;
	private double decapitated = 1;
	
	public R_SwerveModule(final R_CANTalon rotator, final int drive1Port, final int drive2Port) {
		this.rotator = rotator;
		drive1 = new Talon(drive1Port);
		drive2 = new Talon(drive2Port);
	}
	/**
	 * 
	**/
	public void rotateTo(final double wheel_fieldAngle, final double robot_fieldAngle) {
		rotator.setDesiredAngle(decapitateAngle(convertToRobot(wheel_fieldAngle, robot_fieldAngle)));
	}
	/**
	 * 
	**/
	public void set(final double speed) {
		drive1.set(speed*decapitated);//TODO make sure this keeps direction consistent, if not then just pass in speed*Math.signum(acos(currentangle + 90))
		drive2.set(speed*decapitated);
	}
	/**
	 * 
	**/
	public boolean isThere(final double threshold) {
		return Math.abs(rotator.getCurrentError()) <= threshold;
	}
	/**
	 * This function translates angles from the robot's perspective to the field's orientation.
	 * It requires an angle and input from the gyro.
	**/
	public static double convertToField(final double wheel_robotAngle, final double robot_fieldAngle) {
		return V_Compass.validateAngle(wheel_robotAngle + robot_fieldAngle);
	}
	/**
	 * This function translates angles from the field's orientation to the robot's perspective.
	 * It requires an angle and input from the gyro.
	**/
	public static double convertToRobot(final double wheel_fieldAngle, final double robot_fieldAngle) {
		return V_Compass.validateAngle(wheel_fieldAngle - robot_fieldAngle);
	}
	/**
	 * 
	**/
	public double decapitateAngle(final double endAngle) {
		final double ghost = decapitated == -1 ? 180 : 0;
		final boolean necessary = Math.abs(rotator.findNewPath(endAngle + ghost)) > 90;
		decapitated = necessary ? -1 : 1;
		return necessary ? V_Compass.validateAngle(endAngle + 180) : V_Compass.validateAngle(endAngle);
	}
}