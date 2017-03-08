package org.usfirst.frc.team4256.robot;

import com.cyborgcats.reusable.R_CANTalon;
import com.cyborgcats.reusable.R_Gyro;
import com.cyborgcats.reusable.R_Xbox;
import com.cyborgcats.reusable.V_Fridge;
import com.cyborgcats.reusable.V_PID;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	final String defaultAuto = "Default";
	final String customAuto = "My Auto";
	String autoSelected;
	SendableChooser<String> chooser = new SendableChooser<>();
	//Human Input
	private static final R_Xbox driver = new R_Xbox(0);
	//private static final R_XboxV2 gunner = new R_XboxV2(1);
	//Robot Input
	private static final R_Gyro gyro = new R_Gyro(Parameters.Gyrometer_updateHz, 0, 0);
	//Robot Output
	private static final Compressor compressor = new Compressor(0);
	
	private static final R_SwerveModule moduleA = new R_SwerveModule(Parameters.Swerve_rotatorA, true, Parameters.Swerve_driveAA, Parameters.Swerve_driveAB, Parameters.Swerve_calibratorA);
	private static final R_SwerveModule moduleB = new R_SwerveModule(Parameters.Swerve_rotatorB, true, Parameters.Swerve_driveBA, Parameters.Swerve_driveBB, Parameters.Swerve_calibratorB);
	private static final R_SwerveModule moduleC = new R_SwerveModule(Parameters.Swerve_rotatorC, true, Parameters.Swerve_driveCA, Parameters.Swerve_driveCB, Parameters.Swerve_calibratorC);
	private static final R_SwerveModule moduleD = new R_SwerveModule(Parameters.Swerve_rotatorD, true, Parameters.Swerve_driveDA, Parameters.Swerve_driveDB, Parameters.Swerve_calibratorD);
	private static final R_DriveTrain swerve = new R_DriveTrain(gyro, moduleA, moduleB, moduleC, moduleD);
	
	private static final R_CANTalon climber = new R_CANTalon(Parameters.Climber, 17, R_CANTalon.voltage);
	
	private static final DoubleSolenoid gearer = new DoubleSolenoid(Parameters.Gearer_module, Parameters.Gearer_forward, Parameters.Gearer_reverse);
	
	private static final R_CANTalon intake = new R_CANTalon(Parameters.Intake, 1, R_CANTalon.percent);
	
//	private static final R_CANTalon flywheel = new R_CANTalon(Parameters.Shooter_flywheel, 1, R_CANTalon.speed, true, R_CANTalon.relative);
//	private static final R_CANTalon turret = new R_CANTalon(Parameters.Shooter_rotator, 12, R_CANTalon.position, false, R_CANTalon.absolute, 135, 90);
//	private static final Servo linearServo = new Servo(Parameters.Shooter_linearServo);
	private static final DoubleSolenoid flap = new DoubleSolenoid(Parameters.Shooter_flapModule, Parameters.Shooter_flapForward, Parameters.Shooter_flapReverse);
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		chooser.addDefault("Default Auto", defaultAuto);
		chooser.addObject("My Auto", customAuto);
		SmartDashboard.putData("Auto choices", chooser);
		
		compressor.clearAllPCMStickyFaults();
		swerve.init();
		V_PID.set("spin", Parameters.spinP, Parameters.spinI, Parameters.spinD);
		climber.init();
		climber.setVoltageCompensationRampRate(24);
		intake.init();
//		flywheel.init();
//		flywheel.setPID(.025, 0, .25, .01025, 0, 24, 0);
//		turret.init();
//		turret.setPID(Parameters.swerveP, Parameters.swerveI, Parameters.swerveD);
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		autoSelected = chooser.getSelected();
		System.out.println("Auto selected: " + autoSelected);
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		switch (autoSelected) {
		case customAuto:
			// Put custom auto code here
			break;
		case defaultAuto:
		default:
			// Put default auto code here
			break;
		}
	}

	/**
	 * This function is called periodically during operator control
	 */
	Double gearAngle = null;
	@Override
	public void teleopPeriodic() {
		//DRIVER
		//start + back: align
		//X: left gear orientation, gearer out
		//A: center gear orientation, gearer out
		//B: right gear orientation, gearer out
		//Y: loading station orientation, gearer in
		//right axis: raw spin
		//left axis: speed and direction
		//RB: turbo
		//LB: gearer
		//LT: boolean climber
		//RT: raw intake in
		//dpad down: boolean intake out
		
		if (driver.getRawButton(R_Xbox.BUTTON_START) && driver.getRawButton(R_Xbox.BUTTON_BACK)) {
			swerve.align(.002);
		}
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////
		double spinOut = 0;
		if (driver.getDeadbandedAxis(R_Xbox.AXIS_RIGHT_X, .1) != 0) {
			gearAngle = null;
		}
		
		if (driver.getRawButton(R_Xbox.BUTTON_X)) {
			gearAngle = Parameters.leftGear;
		}else if (driver.getRawButton(R_Xbox.BUTTON_A)) {
			gearAngle = Parameters.centerGear;
		}else if (driver.getRawButton(R_Xbox.BUTTON_B)) {
			gearAngle = Parameters.rightGear;
		}else if (driver.getRawButton(R_Xbox.BUTTON_Y)) {
			gearAngle = Parameters.loadingStation;
		}
		
		double spinError = 0;
		if (gearAngle == null) {
			spinOut = driver.getDeadbandedAxis(R_Xbox.AXIS_RIGHT_X);
			spinOut *= .5*spinOut*Math.signum(spinOut);
		}else {
			spinError = gyro.wornPath(gearAngle);
			spinOut = V_PID.get("spin", spinError);
		}
		
		double speed = driver.getCurrentRadius(R_Xbox.STICK_LEFT, true);
		if (!driver.getRawButton(R_Xbox.BUTTON_RB)) {speed *= .6;}
		swerve.holonomic(driver.getCurrentAngle(R_Xbox.STICK_LEFT, true), speed*speed, spinOut);//SWERVE//TODO max spinning speed stationary around .75
		
		if (driver.getAxisPress(R_Xbox.AXIS_LT, .5)) {
			climber.set(-1);//CLIMBER
		}else {
			climber.set(0);
		}
		
		if (gearAngle != null && gearAngle != Parameters.loadingStation) {
			gearer.set(DoubleSolenoid.Value.kReverse);//GEARER
		}else {
			if (V_Fridge.freeze("LB", driver.getRawButton(R_Xbox.BUTTON_LB))) {
				gearer.set(DoubleSolenoid.Value.kReverse);
			}else {
				gearer.set(DoubleSolenoid.Value.kForward);
			}
		}
		
		if (driver.getAxisPress(R_Xbox.AXIS_RT, .05)) {
			intake.set(driver.getRawAxis(R_Xbox.AXIS_RT));//INTAKE
		}else if (driver.getPOV(0) == R_Xbox.POV_SOUTH) {
			intake.set(-.2);
		}else {
			intake.set(0);
		}
		
//		if (gunner.getRawButton(R_XboxV2.BUTTON_RB)) {
//			flywheel.set(6000);
//		}else {
//			flywheel.set(0);
//		}
	 	
		if (gyro.netAcceleration() >= 1) {
			driver.setRumble(RumbleType.kLeftRumble, 1);
		}else {
			driver.setRumble(RumbleType.kLeftRumble, 0);
		}

		moduleA.completeLoopUpdate();
		moduleB.completeLoopUpdate();
		moduleC.completeLoopUpdate();
		moduleD.completeLoopUpdate();
		climber.completeLoopUpdate();
		intake.completeLoopUpdate();
	}
	
	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}
