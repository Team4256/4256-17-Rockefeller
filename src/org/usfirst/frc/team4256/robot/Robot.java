package org.usfirst.frc.team4256.robot;

import com.cyborgcats.reusable.R_CANTalon;
import com.cyborgcats.reusable.R_Gyro;
import com.cyborgcats.reusable.R_XboxV2;
import com.cyborgcats.reusable.V_Fridge;
import com.cyborgcats.reusable.V_PID;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
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
	private static final R_XboxV2 driver = new R_XboxV2(0);
	private static final R_XboxV2 gunner = new R_XboxV2(1);
	//Robot Input
	private static final R_Gyro gyro = new R_Gyro(Parameters.Gyrometer_updateHz, 0, 0);
	//Robot Output
	//private static final R_CANTalon climber = new R_CANTalon(Parameters.Climber, 1, 0, 0, false, R_CANTalon.absolute, R_CANTalon.voltage); //Uncommented 2/21/17
	private static final R_CANTalon climber = new R_CANTalon(Parameters.Climber, 17, R_CANTalon.percent); //Ian 2/21/17
	private static final DoubleSolenoid gearer = new DoubleSolenoid(0, 0, 1);
	private static final R_CANTalon intake = new R_CANTalon(Parameters.Intake, 1, R_CANTalon.percent);
	//private static final Servo servos = new Servo(Parameters.Shooter_servos);
	private static final R_CANTalon flywheel = new R_CANTalon(Parameters.Shooter_flywheel, 1, R_CANTalon.percent);
	private static final R_CANTalon turret = new R_CANTalon(Parameters.Shooter_rotator, 12, R_CANTalon.percent);
	private static final DoubleSolenoid kicker = new DoubleSolenoid(0, 2, 3);
	private static final R_DriveTrain swerve = new R_DriveTrain(gyro, true, true, true, true);
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		chooser.addDefault("Default Auto", defaultAuto);
		chooser.addObject("My Auto", customAuto);
		SmartDashboard.putData("Auto choices", chooser);
		
//		climber.init();
//		climber.setVoltageCompensationRampRate(24);
//		intake.init();
//		flyWheel.init();
//		turret.init();
		swerve.init();
		
		V_PID.set("spin", Parameters.spinP, Parameters.spinI, Parameters.spinD);
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
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
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
	@Override
	public void teleopPeriodic() {
		double spinError = 0;
		double spinOut = 0;
		if (driver.getCurrentRadius(R_XboxV2.STICK_RIGHT, true) == 0) {
			if (V_Fridge.freeze("X", driver.getRawButton(R_XboxV2.BUTTON_X))) {
				spinError = gyro.wornPath(Parameters.leftGear);
			}else if (V_Fridge.freeze("A", driver.getRawButton(R_XboxV2.BUTTON_A))) {
				spinError = gyro.wornPath(Parameters.centerGear);
			}else if (V_Fridge.freeze("B", driver.getRawButton(R_XboxV2.BUTTON_B))) {
				spinError = gyro.wornPath(Parameters.rightGear);
			}else if (V_Fridge.freeze("Y", driver.getRawButton(R_XboxV2.BUTTON_Y))) {
				spinError = gyro.wornPath(Parameters.loadingStation);
			}
			spinOut = V_PID.get("spin", spinError);
		}else {
			V_Fridge.toggleStates.replace("X", false);
			V_Fridge.toggleStates.replace("A", false);
			V_Fridge.toggleStates.replace("B", false);
			V_Fridge.toggleStates.replace("Y", false);
			//spinError = gyro.wornPath(driver.getCurrentAngle(R_XboxV2.STICK_RIGHT, true));//for use with absolutely angled drive
			spinOut = driver.getDeadbandedAxis(R_XboxV2.AXIS_RIGHT_X);
			spinOut *= spinOut * Math.signum(spinOut);
		}
		double speed = driver.getCurrentRadius(R_XboxV2.STICK_LEFT, true);
		swerve.holonomic(driver.getCurrentAngle(R_XboxV2.STICK_LEFT, true), speed*speed, spinOut);//SWERVE
		
		/*if (driver.getAxisPress(R_XboxV2.AXIS_LT, .5)) {//CLIMBER
			climber.setVC(.5);
		}if (V_Fridge.freeze("RB", driver.getRawButton(R_XboxV2.BUTTON_RB))) {//GEARER
			gearer.set(DoubleSolenoid.Value.kForward);
		}else {
			gearer.set(DoubleSolenoid.Value.kReverse);
		}if (driver.getAxisPress(R_XboxV2.AXIS_RT, .5)) {//INTAKE
			intake.setRPM(60);
		}else {
			intake.setRPM(0);
		}*/
		if (gunner.getRawButton(R_XboxV2.BUTTON_A)) { //ian 2/21/17
			climber.set(-1*.7);//ian 2/21/17
		}else {//ian 2/21/17
			climber.set(0);//ian 2/21/17
		}//ian 2/21/17
		turret.set(.2*gunner.getRawAxis(R_XboxV2.AXIS_LEFT_X));
		
		if (driver.getRawButton(R_XboxV2.BUTTON_LB)) {
			flywheel.set(-.5);
		}else {
			flywheel.set(0);
		}
		if (V_Fridge.freeze("kicker", driver.getRawButton(R_XboxV2.BUTTON_RB))) {
			kicker.set(Value.kForward);
		}else {
			kicker.set(Value.kReverse);
		}
	 	intake.set(gunner.getRawAxis(R_XboxV2.AXIS_RIGHT_Y));
		if (gyro.netAcceleration() >= 1) {
			driver.setRumble(RumbleType.kLeftRumble, 1);
		}else {
			driver.setRumble(RumbleType.kLeftRumble, 0);
		}
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
		swerve.align(.001);
	}
}
