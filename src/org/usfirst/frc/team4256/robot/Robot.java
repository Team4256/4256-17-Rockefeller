package org.usfirst.frc.team4256.robot;

import com.cyborgcats.reusable.R_CANTalon;
import com.cyborgcats.reusable.R_Gyro;
import com.cyborgcats.reusable.R_Xbox;
import com.cyborgcats.reusable.V_Fridge;
import com.cyborgcats.reusable.V_PID;

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
	//Robot Output TODO servos
	private static final R_CANTalon climber = new R_CANTalon(Parameters.Climber, 1, 0, 0, false, R_CANTalon.absolute, R_CANTalon.voltage);
	//private static final DoubleSolenoid gearer = new DoubleSolenoid(0, 1, 2);
	private static final R_CANTalon intake = new R_CANTalon(Parameters.Intake, 1, 0, 0, false, R_CANTalon.relative, R_CANTalon.speed);
	private static final Servo servos = new Servo(Parameters.Shooter_servos);
	private static final R_CANTalon flywheel = new R_CANTalon(Parameters.Shooter_flywheel, 1, 0, 0, false, R_CANTalon.relative, R_CANTalon.speed);
	private static final R_CANTalon turret = new R_CANTalon(Parameters.Shooter_rotator, 12, 135, 90, false, R_CANTalon.absolute, R_CANTalon.position);
	private static final R_SwerveModule module1 = new R_SwerveModule(Parameters.Swerve_rotator1, Parameters.Swerve_drive1, Parameters.Swerve_calibrator1);
	private static final R_SwerveModule module2 = new R_SwerveModule(Parameters.Swerve_rotator2, Parameters.Swerve_drive2, Parameters.Swerve_calibrator2);
	private static final R_SwerveModule module3 = new R_SwerveModule(Parameters.Swerve_rotator3, Parameters.Swerve_drive3, Parameters.Swerve_calibrator3);
	private static final R_SwerveModule module4 = new R_SwerveModule(Parameters.Swerve_rotator4, Parameters.Swerve_drive4, Parameters.Swerve_calibrator4);
	private static final R_DriveTrain swerve = new R_DriveTrain(gyro, module1, module2, module3, module4);
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
		}else {
			V_Fridge.toggleStates.replace("X", false);
			V_Fridge.toggleStates.replace("A", false);
			V_Fridge.toggleStates.replace("B", false);
			V_Fridge.toggleStates.replace("Y", false);
			spinError = gyro.wornPath(driver.getCurrentAngle(R_Xbox.STICK_RIGHT, true));
		}
		
		double spinOut = V_PID.get("spin", spinError);
		swerve.holonomic(driver.getCurrentAngle(R_Xbox.STICK_LEFT, true), driver.getCurrentRadius(R_Xbox.STICK_LEFT, true), spinOut);//SWERVE
		
		if (driver.getAxisPress(R_Xbox.AXIS_LT, .5)) {//CLIMBER
			climber.setVC(.5);
		}/*if (V_Fridge.freeze("RB", driver.getRawButton(R_Xbox.BUTTON_RB))) {//GEARER
			gearer.set(DoubleSolenoid.Value.kForward);
		}else {
			gearer.set(DoubleSolenoid.Value.kReverse);
		}*/if (driver.getAxisPress(R_Xbox.AXIS_RT, .5)) {//INTAKE
			intake.setRPM(60);
		}
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		turret.setAngle(R_SwerveModule.convertToRobot(gunner.getCurrentAngle(R_Xbox.STICK_LEFT, true), gyro.getCurrentAngle()));
		flywheel.setRPM(6000*gunner.getRawAxis(R_Xbox.AXIS_RT));
		if (gunner.getRawButton(R_Xbox.BUTTON_RB)) {
			servos.set(servos.get() + .01);
		}else if (gunner.getRawButton(R_Xbox.BUTTON_LB)) {
			servos.set(servos.get() - .01);
		}
		/////////////////////////////////////////
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
		if (!module1.calibrated) {
			module1.calibrate();
		}
		if (!module2.calibrated) {
			module2.calibrate();
		}
		if (!module3.calibrated) {
			module3.calibrate();
		}
		if (!module4.calibrated) {
			module4.calibrate();
		}
	}
}
