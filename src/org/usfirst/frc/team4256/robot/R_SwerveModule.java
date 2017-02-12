package org.usfirst.frc.team4256.robot;

import com.ctre.CANTalon.TalonControlMode;
import com.cyborgcats.reusable.R_CANTalon;
import com.cyborgcats.reusable.V_Compass;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Talon;

public class R_SwerveModule {
	private R_CANTalon rotator;
	private Talon driver;
	private DigitalInput calibrator;
	private boolean calibrated = false;
	private double decapitated = 1;
	
	public R_SwerveModule(final R_CANTalon rotator, final int driver, final int calibrator) {
		this.rotator = rotator;
		this.driver = new Talon(driver);
		this.calibrator = new DigitalInput(calibrator);
	}
	/**
	 * 
	**/
	public boolean isCalibrated() {
		return calibrated;
	}
	/**
	 * 
	**/
	public void calibrate() {
		rotator.changeControlMode(TalonControlMode.Position);
		int iteration = 0;
		double revs = rotator.getPosition()%4.2;
		while (calibrator.get() && iteration < 840) {
			revs += 0.005;
			rotator.set(revs);
			iteration++;
		}
		rotator.compass.setTareAngle(revs%4.2*360/4.2, false);//TODO should it be true or false
		calibrated = true;//TODO make a get function to return this to other classes
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
		driver.set(speed*decapitated);
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
		decapitated = Math.abs(rotator.findNewPath(endAngle)) > 90 ? -1 : 1;
		return decapitated == -1 ? V_Compass.validateAngle(endAngle + 180) : V_Compass.validateAngle(endAngle);
	}
}