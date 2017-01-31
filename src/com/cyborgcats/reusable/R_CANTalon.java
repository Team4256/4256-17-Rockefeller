package com.cyborgcats.reusable;

import com.ctre.CANTalon;

public class R_CANTalon extends CANTalon {
	public static final FeedbackDevice absolute = FeedbackDevice.CtreMagEncoder_Absolute;
	public static final FeedbackDevice relative = FeedbackDevice.CtreMagEncoder_Relative;
	private static final float floatiness = 1;//the tolerance when checking for equivalence of floats
	private double lastLegalDirection = 1.0;
	private double gearRatio;
	public V_Compass compass;
	
	public R_CANTalon(final int deviceNumber, final FeedbackDevice deviceType, final boolean reverseSensor, final double gearRatio) {//can also have update rate
		super(deviceNumber);
		reverseSensor(reverseSensor);//sensor must count positively as motor spins with positive speed
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
	 * This function returns the current angle based on the tare angle.
	**/
	public float getCurrentAngle() {//ANGLE
		if (getControlMode() != TalonControlMode.Position) {changeControlMode(TalonControlMode.Position);}
		float currentAngle = (float)(getPosition()*360/gearRatio);
		if (0 <= V_Compass.validateAngle((float)getPosition()) && V_Compass.validateAngle((float)getPosition()) <= compass.getTareAngle()) {
			currentAngle += 360 - compass.getTareAngle();//follows order of operations
		}else {
			currentAngle -= compass.getTareAngle();
		}return V_Compass.validateAngle(currentAngle);
	}
	/**
	 * This function finds the shortest legal path from the current angle to the end angle and returns the size of that path in degrees.
	 * Positive means clockwise and negative means counter-clockwise.
	 * If the current angle is inside the protected zone, the path goes through the previously breached border.
	**/
	public float findNewPath(float endAngle) {//ANGLE
		endAngle = compass.legalizeAngle(endAngle);
		final float currentAngle = getCurrentAngle();
		float currentPathVector = V_Compass.findPath(currentAngle, endAngle);
		boolean legal = Math.abs(compass.legalizeAngle(currentAngle) - currentAngle) <= floatiness;
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
	public void setDesiredAngle(final float desiredAngle) {//ANGLE
		if (getControlMode() != TalonControlMode.Position) {changeControlMode(TalonControlMode.Position);}
		set(getPosition() + (double)findNewPath(desiredAngle)*gearRatio/360);
	}
	
	public float getCurrentError() {//ANGLE AND SPEED
		if (getControlMode() == TalonControlMode.Position) {
			return (float)(getError()*360/(4096*gearRatio));//degrees
		}else if (getControlMode() == TalonControlMode.Speed) {
			return (float)(getError()*600/(4096*gearRatio));//rpm
		}else {
			return -1;//TODO make this function work for every type of control mode
		}
	}
}