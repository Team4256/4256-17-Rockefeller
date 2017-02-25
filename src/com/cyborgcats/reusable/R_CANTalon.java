package com.cyborgcats.reusable;

import com.ctre.CANTalon;

public class R_CANTalon extends CANTalon {
	public static final FeedbackDevice absolute = FeedbackDevice.CtreMagEncoder_Absolute;
	public static final FeedbackDevice relative = FeedbackDevice.CtreMagEncoder_Relative;
	public static final TalonControlMode current = TalonControlMode.Current;
	public static final TalonControlMode follower = TalonControlMode.Follower;
	public static final TalonControlMode percent = TalonControlMode.PercentVbus;
	public static final TalonControlMode position = TalonControlMode.Position;
	public static final TalonControlMode speed = TalonControlMode.Speed;
	public static final TalonControlMode voltage = TalonControlMode.Voltage;
	private double lastLegalDirection = 1;
	public V_Compass compass;
	private double gearRatio;
	//This constructor is intended for use with an encoder on a motor with limited motion.
	public R_CANTalon(final int deviceID, final double gearRatio, final TalonControlMode controlMode, final boolean flipped, final FeedbackDevice deviceType, final double protectedZoneStart, final double protectedZoneSize) {
		super(deviceID);
		this.gearRatio = gearRatio;
		if (isSensorPresent(deviceType) == FeedbackDeviceStatus.FeedbackStatusPresent) {
			setFeedbackDevice(deviceType);
		}else {
			throw new IllegalStateException("A CANTalon4256 could not find its integrated versaplanetary encoder.");
		}
		reverseOutput(flipped);
		changeControlMode(controlMode);
		compass = new V_Compass(protectedZoneStart, protectedZoneSize);
	}
	//This constructor is intended for use with an encoder on a motor which can spin freely.
	public R_CANTalon(final int deviceID, final double gearRatio, final TalonControlMode controlMode, final boolean flipped, final FeedbackDevice deviceType) {
		super(deviceID);
		this.gearRatio = gearRatio;
		if (isSensorPresent(deviceType) == FeedbackDeviceStatus.FeedbackStatusPresent) {
			setFeedbackDevice(deviceType);
		}else {
			throw new IllegalStateException("A CANTalon4256 could not find its integrated versaplanetary encoder.");
		}
		reverseOutput(flipped);
		changeControlMode(controlMode);
		compass = new V_Compass(0, 0);
	}
	//This constructor is intended for a motor without an encoder.
	public R_CANTalon(final int deviceID, final double gearRatio, final TalonControlMode controlMode) {
		super(deviceID);
		this.gearRatio = gearRatio;
		changeControlMode(controlMode);
		compass = new V_Compass(0, 0);
	}
	/**
	 * This function prepares a motor by setting the PID profile, the closed loop error, and the minimum and maximum voltages.
	 * It then gets enslaved to the motor at the specified ID.
	**/
	public void init(final int masterID) {
		setProfile(0);//choose between PID loop parameter stores
		setAllowableClosedLoopErr(0);
		configNominalOutputVoltage(+0f, -0f);//minimum voltage draw
		configPeakOutputVoltage(+12f, -12f);//maximum voltage draw
		if (getControlMode() == follower) {
			set(masterID);
		}else {
			set(0);
		}
	}
	/**
	 * This function prepares a motor by setting the PID profile, the closed loop error, and the minimum and maximum voltages.
	**/
	public void init() {
		init(0);
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
	public double wornPath(double endAngle) {//ANGLE
		endAngle = compass.legalizeAngle(endAngle + compass.getTareAngle());
		double startAngle = getCurrentAngle(true);
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
	 * This function sets the motor's output or target setpoint based on the current control mode.
	 * Current: Milliamperes
	 * Follower: ID
	 * PercentVbus: -1 to 1
	 * Position: Degrees
	 * Speed: RPM
	 * Voltage: -1 to 1 (gets scaled to -12 to 12)
	**/
//	@Override
//	public void set(double value) {//CURRENT, ANGLE, SPEED
//		switch (getControlMode()) {
//		case Current:set(value);break;
//		case Follower:set(value);break;
//		case PercentVbus:set(value);break;
//		case Position:set((getCurrentAngle(false) + wornPath(value))*gearRatio/360);break;
//		case Speed:set(value);break;
//		case Voltage:
//			if (Math.abs(value) > 1) {value = Math.signum(value);}
//			set(value*12);
//			break;
//		default:
//			break;
//		}
//	}
	
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
	 * Current: Milliamperes
	 * Position: Degrees
	 * Speed: RPM
	**/
	public double getCurrentError() {//CURRENT, ANGLE, SPEED
		switch (getControlMode()) {
		case Current:return getError();
		case Position:return getError()*360/(4096*gearRatio);
		case Speed:return getError()*600/(4096*gearRatio);
		default:return -1;
		}
	}
}