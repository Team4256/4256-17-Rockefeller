package org.usfirst.frc.team4256.robot;

import com.cyborgcats.reusable.R_CANTalon;
import com.cyborgcats.reusable.R_Gyro;
import com.cyborgcats.reusable.R_Xbox;
import com.cyborgcats.reusable.V_Fridge;
import com.cyborgcats.reusable.V_PID;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Servo;
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
	private static final R_Xbox gunner = new R_Xbox(1);
	//Robot Input
	private static final R_Gyro gyro = new R_Gyro(Parameters.Gyrometer_updateHz, 0, 0);
	//Robot Output
//	private static final R_CANTalon climber = new R_CANTalon(Parameters.Climber, 17, R_CANTalon.voltage, true, R_CANTalon.relative); //Ian 2/21/17
//	private static final DoubleSolenoid gearer = new DoubleSolenoid(0, 0, 1);
//	private static final R_CANTalon intake = new R_CANTalon(Parameters.Intake, 1, R_CANTalon.percent);
//	private static final Servo linearServo = new Servo(Parameters.Shooter_linearServo);
//	private static final R_CANTalon flywheel = new R_CANTalon(Parameters.Shooter_flywheel, 1, R_CANTalon.speed, true, R_CANTalon.relative); //ian made this
//	public static final R_CANTalon flywheel = new R_CANTalon(1, 1, R_CANTalon.percent);
	private static final R_CANTalon turret = new R_CANTalon(Parameters.Shooter_rotator, 12, R_CANTalon.position, true, R_CANTalon.absolute, 135, 90);
	private static final R_DriveTrain swerve = new R_DriveTrain(gyro, true, true, true, true);
//	private static final Compressor compressor = new Compressor(0);
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
//		flywheel.init();
//		turret.init();
		swerve.init();
		
		V_PID.set("spin", Parameters.spinP, Parameters.spinI, Parameters.spinD);
		SmartDashboard.putNumber("p", 0);
		SmartDashboard.putNumber("i", 0);
		SmartDashboard.putNumber("d", 0);
		SmartDashboard.putNumber("pos", 0);
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
		 autoSelected = SmartDashboard.getString("Auto Selector",
		 defaultAuto);
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
	private static double previousAxisValue;
	private static double lockedAngle;
	@Override
	public void teleopPeriodic() {
		
		double spinError = 0;
		double spinOut = 0;
		if (driver.getCurrentRadius(R_Xbox.STICK_RIGHT, true) == 0) {
			if (V_Fridge.freeze("X", driver.getRawButton(R_Xbox.BUTTON_X))) {
				spinError = gyro.wornPath(Parameters.leftGear);
			}else if (V_Fridge.freeze("A", driver.getRawButton(R_Xbox.BUTTON_A))) {
				spinError = gyro.wornPath(Parameters.centerGear);
			}else if (V_Fridge.freeze("B", driver.getRawButton(R_Xbox.BUTTON_B))) {
				spinError = gyro.wornPath(Parameters.rightGear);
			}else if (V_Fridge.freeze("Y", driver.getRawButton(R_Xbox.BUTTON_Y))) {
				spinError = gyro.wornPath(Parameters.loadingStation);
			}
			spinOut = V_PID.get("spin", spinError);
		}else {
			V_Fridge.toggleStates.replace("X", false);
			V_Fridge.toggleStates.replace("A", false);
			V_Fridge.toggleStates.replace("B", false);
			V_Fridge.toggleStates.replace("Y", false);
			//spinError = gyro.wornPath(driver.getCurrentAngle(R_XboxV2.STICK_RIGHT, true));//for use with absolutely angled drive
			if (driver.getDeadbandedAxis(R_Xbox.AXIS_RIGHT_X) == 0 && previousAxisValue != 0) {
				lockedAngle = gyro.getCurrentAngle();
			}
			if (driver.getDeadbandedAxis(R_Xbox.AXIS_RIGHT_X) == 0) {
				spinError = gyro.wornPath(lockedAngle);
				spinOut = V_PID.get("spin", spinError);
			}else {
				spinOut = driver.getDeadbandedAxis(R_Xbox.AXIS_RIGHT_X);
				spinOut *= spinOut * Math.signum(spinOut);
			}
		}
		double speed = driver.getCurrentRadius(R_Xbox.STICK_LEFT, true);
		swerve.holonomic(driver.getCurrentAngle(R_Xbox.STICK_LEFT, true), speed*speed, spinOut);//SWERVE
		//if (driver.getAxisPress(R_XboxV2.AXIS_LT, .5)) {//CLIMBER
		//	climber.setVC(.5);
		//}
//		if (V_Fridge.freeze("RB", driver.getRawButton(R_Xbox.BUTTON_LB))) {//GEARER changed to normal v2 changed to LB
//			gearer.set(DoubleSolenoid.Value.kForward);
//		}else {
//			gearer.set(DoubleSolenoid.Value.kReverse);
//		}
//		if (V_Fridge.freeze("LB", gunner.getRawButton(R_Xbox.BUTTON_LB))) {//INTAKE changed to normal v2
//			intake.setRPM(60);
//		}else {
//			intake.setRPM(0);
//		}
//		if (gunner.getAxisPress(R_Xbox.AXIS_LT, 0.5)) { //ian 2/21/17
//			climber.set(-1*.7);//ian 2/21/17
//		}else {//ian 2/21/17
//			climber.set(0);//ian 2/21/17
//		}//ian 2/21/17
//		turret.set(.2*gunner.getRawAxis(R_Xbox.AXIS_RIGHT_X));
//		
//		if (gunner.getRawButton(R_Xbox.BUTTON_RB)) {
//			flywheel.set(-.5);
//		}else {
//			flywheel.set(0);
//		}
////		if (V_Fridge.freeze("kicker", driver.getRawButton(R_Xbox.BUTTON_RB))) {
////			kicker.set(Value.kForward);
////		}else {
////			kicker.set(Value.kReverse);
////		}
//	 	intake.set(gunner.getRawAxis(R_Xbox.AXIS_RIGHT_Y));
//		if (gyro.netAcceleration() >= 1) {
//			driver.setRumble(RumbleType.kLeftRumble, 1);
//		}else {
//			driver.setRumble(RumbleType.kLeftRumble, 0);
//		}
//		linearServo.set(gunner.getRawAxis(R_Xbox.AXIS_LEFT_Y)*.3);
		//flywheel.set(-.48);
	
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
//		swerve.align(.002);
//		swerve.holonomic(0, 0, 0);
//		V_PID.set("spin", SmartDashboard.getNumber("p", 0), SmartDashboard.getNumber("i", 0), SmartDashboard.getNumber("d", 0));
		
		turret.setPosition(SmartDashboard.getNumber("pos", 0));
	}
}
