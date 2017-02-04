package org.usfirst.frc.team4256.robot;

import com.cyborgcats.reusable.R_CANTalon;
import com.cyborgcats.reusable.V_Compass;

import edu.wpi.first.wpilibj.Talon;

public class R_SwerveModule {
	private R_CANTalon rotator;//TODO if the one in robot is modified, does this get updated
	private Talon drive1;
	private Talon drive2;
	private double decapitated = 1;
	
	public R_SwerveModule(final R_CANTalon rotator, final int drive1Port, final int drive2Port) {
		this.rotator = rotator;
		drive1 = new Talon(drive1Port);
		drive2 = new Talon(drive2Port);
	}
	/**
	 * This function translates angles from the robot's perspective to the field's orientation.
	 * It requires an angle and input from the gyrometer.
	**/
	public static double convertToField(final double wheel_robotAngle, final double robot_fieldAngle) {
		return V_Compass.validateAngle(wheel_robotAngle + robot_fieldAngle);
	}
	/**
	 * This function translates angles from the field's orientation to the robot's perspective.
	 * It requires an angle and input from the gyrometer.
	**/
	public static double convertToRobot(final double wheel_fieldAngle, final double robot_fieldAngle) {
		return V_Compass.validateAngle(wheel_fieldAngle - robot_fieldAngle);
	}
	/**
	 * 
	**/
	public void rotateTo(final double wheel_fieldAngle, final double robot_fieldAngle) {
		final double[] decapitation = decapitateAngle(convertToRobot(wheel_fieldAngle, robot_fieldAngle));
		decapitated = decapitation[1];
		rotator.setDesiredAngle(decapitation[0]);
	}
	/**
	 * 
	**/
	public void set(final double speed) {
		drive1.set(speed*decapitated);
		drive2.set(speed*decapitated);
	}
	/**
	 * 
	**/
	public double[] decapitateAngle(final double endAngle) {
		final boolean necessary = Math.abs(rotator.findNewPath(endAngle)) > 90;
		return necessary ? new double[] {V_Compass.validateAngle(endAngle + 180), -1} : new double[] {V_Compass.validateAngle(endAngle), 1};
	}
}