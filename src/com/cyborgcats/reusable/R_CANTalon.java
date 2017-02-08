package com.cyborgcats.reusable;

import com.ctre.CANTalon;

public class R_CANTalon extends CANTalon {
	public static final FeedbackDevice absolute = FeedbackDevice.CtreMagEncoder_Absolute;
	public static final FeedbackDevice relative = FeedbackDevice.CtreMagEncoder_Relative;
	private double gearRatio;
	private double lastLegalDirection = 1;
	public V_Compass compass;
	
	public R_CANTalon(final int deviceNumber, final FeedbackDevice deviceType, final boolean reverseSensor, final double gearRatio) {//can also have update rate
		super(deviceNumber);
		reverseOutput(reverseSensor);//sensor must count positively as motor spins with positive speed
		setFeedbackDevice(deviceType);
		configNominalOutputVoltage(+0f, -0f);//minimum voltage draw
		configPeakOutputVoltage(+12f, -12f);//maximum voltage draw
		setProfile(0);//choose between PID loop parameter stores
		setAllowableClosedLoopErr(0);
		if (isSensorPresent(deviceType) != FeedbackDeviceStatus.FeedbackStatusPresent) {
			throw new IllegalStateException("A CANTalon4256 could not find its integrated versaplanetary encoder.");
		}
		this.gearRatio = gearRatio;
		compass = new V_Compass(0, 0);//0, 0 is tailored toward a swerve-ready CANTalon, but can be set to anything.
	}
	/**
	 * This function returns the current angle based on the tare angle. If the argument is true, the output will be between 0 and 359.999...
	**/
	public double getCurrentAngle(final boolean wraparound) {//ANGLE
		if (getControlMode() != TalonControlMode.Position) {changeControlMode(TalonControlMode.Position);}
		double currentAngle = getPosition()*360/gearRatio;
		if (wraparound) {
			currentAngle = V_Compass.validateAngle(currentAngle);
			if (0 <= currentAngle && currentAngle <= compass.getTareAngle()) {
				currentAngle += 360 - compass.getTareAngle();
			}else {
				currentAngle -= compass.getTareAngle();
			}currentAngle = V_Compass.validateAngle(currentAngle);
		}else {
			currentAngle -= compass.getTareAngle();
		}return currentAngle;
	}
	/**
	 * This function finds the shortest legal path from the current angle to the end angle and returns the size of that path in degrees.
	 * Positive means clockwise and negative means counter-clockwise.
	 * If the current angle is inside the protected zone, the path goes through the previously breached border.
	**/
	public double findNewPath(double endAngle) {//ANGLE
		endAngle = compass.legalizeAngle(endAngle);
		final double currentAngle = getCurrentAngle(true);
		double currentPathVector = V_Compass.findPath(currentAngle, endAngle);
		boolean legal = compass.legalizeAngle(currentAngle) == currentAngle;
		if (legal) {
			currentPathVector = compass.findLegalPath(currentAngle, endAngle);
			lastLegalDirection = Math.signum(currentPathVector);
		}else if (!legal && Math.signum(currentPathVector) != -lastLegalDirection) {
			currentPathVector = 360*Math.signum(-currentPathVector) + currentPathVector;
		}return currentPathVector;
	}
	/**
	 * This function updates the PID loop's target position such that the motor will rotate to the specified angle in the best way possible.
	**/
	public void setDesiredAngle(final double desiredAngle) {//ANGLE
		if (getControlMode() != TalonControlMode.Position) {changeControlMode(TalonControlMode.Position);}
		set((getCurrentAngle(false) + findNewPath(desiredAngle))*gearRatio/360);
	}
	
	public double getCurrentError() {//ANGLE AND SPEED
		if (getControlMode() == TalonControlMode.Position) {
			return getError()*360/(4096*gearRatio);//degrees
		}else if (getControlMode() == TalonControlMode.Speed) {
			return getError()*600/(4096*gearRatio);//rpm
		}else {
			return -1;//TODO make this function work for every type of control mode
		}
	}
}