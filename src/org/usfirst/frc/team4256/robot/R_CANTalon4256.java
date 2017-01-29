package org.usfirst.frc.team4256.robot;

import com.ctre.CANTalon;

public class R_CANTalon4256 extends CANTalon {
	public static final FeedbackDevice absolute = FeedbackDevice.CtreMagEncoder_Absolute;
	public static final FeedbackDevice relative = FeedbackDevice.CtreMagEncoder_Relative;
	public V_Compass4256 compass;
	public R_CANTalon4256(final int deviceNumber, final boolean reverseSensor, final FeedbackDevice deviceType) {//can also have update rate
		super(deviceNumber);
		reverseSensor(reverseSensor);//sensor must count positively as motor spins with positive speed
		setFeedbackDevice(deviceType);
		configNominalOutputVoltage(+0f, -0f);//minimum voltage draw
		configPeakOutputVoltage(+12f, -12f);//maximum voltage draw
		setProfile(0);//choose between PID loop parameter stores
		setAllowableClosedLoopErr(0);
		if (isSensorPresent(deviceType) != FeedbackDeviceStatus.FeedbackStatusPresent) {
			throw new IllegalStateException("A CANTalon4256 could not find an integrated versaplanetary encoder.");
		}
		compass = new V_Compass4256(0, 0);
	}
	private float floatiness = 1;//the tolerance when checking for equivalence of floats
	private double lastLegalDirection = 1.0;
	/**
	 * This function returns the current angle based on the tare angle.
	**/
	public float getCurrentAngle() {//ANGLE
		if (getControlMode() != TalonControlMode.Position) {changeControlMode(TalonControlMode.Position);}
		float currentAngle = (float)getPosition()*360;
		if (0 <= V_Compass4256.validateAngle((float)getPosition()) && V_Compass4256.validateAngle((float)getPosition()) <= compass.getTareAngle()) {
			currentAngle = 360 - compass.getTareAngle() + V_Compass4256.validateAngle((float)getPosition());//follows order of operations
		}else {
			currentAngle = V_Compass4256.validateAngle((float)getPosition()) - compass.getTareAngle();
		}return V_Compass4256.validateAngle(currentAngle);
	}
	/**
	 * This function finds the shortest legal path from the current angle to the end angle and returns the size of that path in degrees.
	 * Positive means clockwise and negative means counter-clockwise.
	 * If the current angle is inside the protected zone, the path goes through the previously breached border.
	**/
	public float findNewPath(float endAngle) {//ANGLE
		endAngle = compass.legalizeAngle(endAngle);
		final float currentAngle = getCurrentAngle();
		float currentPathVector = V_Compass4256.findPath(currentAngle, endAngle);
		boolean legal = Math.abs(compass.legalizeAngle(currentAngle) - currentAngle) <= floatiness;
		if (legal) {
			currentPathVector = compass.findLegalPath(currentAngle, endAngle);
			lastLegalDirection = Math.signum(currentPathVector);
		}else if (!legal && Math.signum(currentPathVector) != -lastLegalDirection) {
			currentPathVector = 360*Math.signum(-currentPathVector) + currentPathVector;
		}return currentPathVector;
	}
	//TODO documentation
	public void setDesiredAngle(final float desiredAngle) {//ANGLE
		if (getControlMode() != TalonControlMode.Position) {changeControlMode(TalonControlMode.Position);}
		set(getPosition() + (double)compass.findLegalPath(getCurrentAngle(), desiredAngle)/360);
	}
	
	public float getCurrentError() {//ANGLE AND SPEED
		if (getControlMode() == TalonControlMode.Position) {
			return (float)getError()*360/4096;//degrees
		}else if (getControlMode() == TalonControlMode.Speed) {
			return (float)getError()*600/4096;//rpm
		}else {
			return -1;//TODO make this function work for every type of control mode
		}
	}
}