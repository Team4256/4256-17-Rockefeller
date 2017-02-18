package com.cyborgcats.reusable;

import com.ctre.CANTalon;

public class R_CANTalon extends CANTalon {
	public static final FeedbackDevice absolute = FeedbackDevice.CtreMagEncoder_Absolute;
	public static final FeedbackDevice relative = FeedbackDevice.CtreMagEncoder_Relative;
	public static final TalonControlMode current = TalonControlMode.Current;
	public static final TalonControlMode percent = TalonControlMode.PercentVbus;
	public static final TalonControlMode position = TalonControlMode.Position;
	public static final TalonControlMode speed = TalonControlMode.Speed;
	public static final TalonControlMode voltage = TalonControlMode.Voltage;
	private double lastLegalDirection = 1;
	public V_Compass compass;
	private TalonControlMode controlMode;
	private double gearRatio;
	private boolean reverseCounts;
	
	public R_CANTalon(final int deviceNumber, final double gearRatio, final double protectedZoneStart, final double protectedZoneSize, final boolean reverseCounts, final FeedbackDevice deviceType, final TalonControlMode controlMode) {
		super(deviceNumber);
		setFeedbackDevice(deviceType);
		if (isSensorPresent(deviceType) != FeedbackDeviceStatus.FeedbackStatusPresent) {
			throw new IllegalStateException("A CANTalon4256 could not find its integrated versaplanetary encoder.");
		}
		compass = new V_Compass(protectedZoneStart, protectedZoneSize);
		this.controlMode = controlMode;
		this.gearRatio = gearRatio;
		this.reverseCounts = reverseCounts;
	}
	/**
	 * Initialize.
	**/
	public void init() {
		reverseOutput(reverseCounts);//sensor must count positively as motor spins with positive speed
		changeControlMode(controlMode);//this comes after reverseOutput so that PID doesn't get confused
		setProfile(0);//choose between PID loop parameter stores
		setAllowableClosedLoopErr(0);
		configNominalOutputVoltage(+0f, -0f);//minimum voltage draw
		configPeakOutputVoltage(+12f, -12f);//maximum voltage draw
	}
	/**
	 * This function returns the current angle. If wraparound is true, the output will be between 0 and 359.999...
	**/
	public double getCurrentAngle(final boolean wraparound) {//ANGLE
		if (getControlMode() != position) {return -1;}
		return wraparound ? V_Compass.validateAngle(getPosition()*360/4.2) : getPosition()*360/4.2;
	}
	/**
	 * This function finds the shortest legal path from the current angle to the end angle and returns the size of that path in degrees.
	 * Positive means clockwise and negative means counter-clockwise.
	 * If the current angle is inside the protected zone, the path goes through the previously breached border.
	**/
	public double wornPath(double endAngle) {
		endAngle = compass.legalizeAngle(endAngle + compass.getTareAngle());
		double startAngle = getCurrentAngle(true);//TODO CAN THIS GETCURRENTANGLE BE FALSE???
		double currentPathVector = V_Compass.path(startAngle, endAngle);
		boolean legal = compass.legalizeAngle(startAngle) == startAngle;
		if (legal) {
			currentPathVector = compass.legalPath(startAngle, endAngle);
			lastLegalDirection = Math.signum(currentPathVector);
		}else if (!legal && Math.signum(currentPathVector) != -lastLegalDirection) {
			currentPathVector = 360*Math.signum(-currentPathVector) + currentPathVector;
		}return currentPathVector;
	}
	/**
	 * This function updates the PID loop's target position such that the motor will rotate to the specified angle in the best way possible.
	**/
	public void setAngle(final double endAngle) {//ANGLE
		if (getControlMode() == position) {
			set((getCurrentAngle(false) + wornPath(endAngle))*gearRatio/360);
		}
	}
	/**
	 * This function updates the PID loop's target speed.
	**/
	public void setRPM(final double rpm) {//SPEED
		if (getControlMode() == speed) {
			set(rpm);
		}
	}
	/**
	 * This function updates the PID loop's target current.
	**/
	public void setAmps(final double amps) {//CURRENT
		if (getControlMode() == current) {
			set(amps);
		}
	}
	/**
	 * This function scales the input to a voltage between 0 and 12, and then uses voltage compensation mode to maintain it.
	 * setVoltageCompensationRate(voltsPerSecondOverTen) must be run before calling this function.
	**/
	public void setVC(double speed) {//VOLTAGE
		if (getControlMode() == voltage) {
			if (Math.abs(speed) > 1) {speed = Math.signum(speed);}
			speed *= 12;
			set(speed);
		}
	}
	/**
	 * This function returns the PID error for the current control mode.
	 * Position: Degrees
	 * Speed: RPM
	 * Current: Amperes
	**/
	public double getCurrentError() {//ANGLE, SPEED, CURRENT
		switch (getControlMode()) {
		case Position:return getError()*360/(4096*gearRatio);
		case Speed:return getError()*600/(4096*gearRatio);
		case Current:return getError();
		default:return -1;
		}
	}
}