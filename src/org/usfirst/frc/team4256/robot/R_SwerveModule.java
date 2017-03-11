package org.usfirst.frc.team4256.robot;

import com.cyborgcats.reusable.R_CANTalon;
import com.cyborgcats.reusable.V_Compass;

import edu.wpi.first.wpilibj.DigitalInput;

public class R_SwerveModule {
	public static final double rotatorGearRatio = 4.2;
	public static final double tractionGearRatio = 15.6;
	private boolean aligned = false;
	private boolean aligning = false;
	private double lastAngle = 0;
	private double alignmentRevs = 0;
	private double decapitated = 1;
	private R_CANTalon rotator;
	private R_CANTalon tractionA;
	private R_CANTalon tractionB;
	public DigitalInput sensor;
	
	public R_SwerveModule(final int rotatorID, final boolean flipped, final int tractionAID, final int tractionBID, final int sensorID) {
		this.rotator = new R_CANTalon(rotatorID, rotatorGearRatio, R_CANTalon.position, flipped, R_CANTalon.absolute);
		this.tractionA = new R_CANTalon(tractionAID, tractionGearRatio, R_CANTalon.percent);
		this.tractionB = new R_CANTalon(tractionBID, tractionGearRatio, R_CANTalon.follower);
		this.sensor = new DigitalInput(sensorID);
	}
	/**
	 * This function prepares each motor individually, including setting actual PID values for the rotator and enslaving the second traction motor.
	**/
	public void init() {
		rotator.init();
		rotator.enableBrakeMode(false);
		rotator.setPID(Parameters.swerveP, Parameters.swerveI, Parameters.swerveD);
		tractionA.init(0, 12f);
		tractionA.enableBrakeMode(false);
		tractionB.init(tractionA.getDeviceID(), 12f);
		tractionB.enableBrakeMode(false);
	}
	/**
	 * This function indicates whether the module has been aligned.
	**/
	public boolean isAligned() {
		return aligned;
	}
	/**
	 * This function indicates whether the module is aligning.
	**/
	public boolean isAligning() {
		return aligning;
	}
	public void align(final double increment) {
		set(0);
		if (sensor.get()) {
			if (!aligning) {
				aligned = false;
				aligning = true;
				alignmentRevs = rotator.getPosition();
			}alignmentRevs += increment;
			rotator.set(alignmentRevs, false);
		}else {
			aligning = false;
			rotator.set(alignmentRevs, false);
			rotator.compass.setTareAngle(alignmentRevs%rotatorGearRatio*360/rotatorGearRatio, false);
			aligned = true;
		}
	}
	public void setTareAngle(final double tareAngle, final boolean relativeReference) {
		rotator.compass.setTareAngle(tareAngle, relativeReference);
	}
	/**
	 * 
	**/
	public void swivelTo(final double wheel_chassisAngle) {
		swivelTo(wheel_chassisAngle, false);
	}
	/**
	 * 
	**/
	public void swivelTo(final double wheel_chassisAngle, final boolean ignore) {
		if (!ignore) {lastAngle = decapitateAngle(wheel_chassisAngle);}
		rotator.set(lastAngle);
	}
	/**
	 * 
	**/
	public void swivelWith(final double wheel_fieldAngle, final double chassis_fieldAngle) {
		swivelTo(convertToRobot(wheel_fieldAngle, chassis_fieldAngle));
	}
	/**
	 * 
	**/
	public void set(final double speed) {
		tractionA.set(speed*decapitated);
	}
	
	public void completeLoopUpdate() {
		rotator.completeLoopUpdate();
		tractionA.completeLoopUpdate();
	}
	
	/**
	 * TODO
	**/
	public boolean isThere(final double threshold) {
		return Math.abs(rotator.getCurrentError()) <= threshold;
	}
	/**
	 * This function makes sure the module rotates no more than 90 degrees from its current position.
	 * It should be used every time a new angle is being set.
	**/
	public double decapitateAngle(final double endAngle) {
		decapitated = Math.abs(rotator.wornPath(endAngle)) > 90 ? -1 : 1;
		return decapitated == -1 ? V_Compass.validateAngle(endAngle + 180) : V_Compass.validateAngle(endAngle);
	}
	/**
	 * This function translates angles from the robot's perspective to the field's orientation.
	 * It requires an angle and input from the gyro.
	**/
	public static double convertToField(final double wheel_robotAngle, final double chassis_fieldAngle) {
		return V_Compass.validateAngle(wheel_robotAngle + chassis_fieldAngle);
	}
	/**
	 * This function translates angles from the field's orientation to the robot's perspective.
	 * It requires an angle and input from the gyro.
	**/
	public static double convertToRobot(final double wheel_fieldAngle, final double chassis_fieldAngle) {
		return V_Compass.validateAngle(wheel_fieldAngle - chassis_fieldAngle);
	}
}