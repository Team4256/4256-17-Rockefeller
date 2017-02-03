package org.usfirst.frc.team4256.robot;

import com.cyborgcats.reusable.R_CANTalon;
import com.cyborgcats.reusable.R_Gyrometer;
import com.cyborgcats.reusable.R_Xbox;

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
	private static final R_Xbox driverStick = new R_Xbox(0);
	//Robot Input
	private static final R_Gyrometer gyro = new R_Gyrometer(Parameters.Gyrometer_updateHz, 0, 0);
	//Robot Output
	private static final R_CANTalon rotator1 = new R_CANTalon(Parameters.Swerve_module1rotator, R_CANTalon.absolute, false, 4.2);
	//private static final R_CANTalon rotator2 = new R_CANTalon(Parameters.Swerve_module2rotator, R_CANTalon.absolute, false, 4.2);
	//private static final R_CANTalon rotator3 = new R_CANTalon(Parameters.Swerve_module3rotator, R_CANTalon.absolute, false, 4.2);
	//private static final R_CANTalon rotator4 = new R_CANTalon(Parameters.Swerve_module4rotator, R_CANTalon.absolute, false, 4.2);
	private static final R_SwerveModule module1 = new R_SwerveModule(rotator1, Parameters.Swerve_module1drive1, Parameters.Swerve_module1drive2);
	//private static final R_SwerveModule module2 = new R_SwerveModule(rotator2, Parameters.Swerve_module2drive1, Parameters.Swerve_module2drive2);
	//private static final R_SwerveModule module3 = new R_SwerveModule(rotator3, Parameters.Swerve_module3drive1, Parameters.Swerve_module3drive2);
	//private static final R_SwerveModule module4 = new R_SwerveModule(rotator4, Parameters.Swerve_module4drive1, Parameters.Swerve_module4drive2);
	//private static final R_Base base = new R_Base(gyro, module1, module2, module3, module4);
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		chooser.addDefault("Default Auto", defaultAuto);
		chooser.addObject("My Auto", customAuto);
		SmartDashboard.putData("Auto choices", chooser);
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
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
		module1.rotateTo(driverStick.getCurrentAngle(R_Xbox.STICK_LEFT, true), gyro.getCurrentAngle());
		module1.set(driverStick.getCurrentRadius(R_Xbox.STICK_LEFT, true));
	}
}
