package org.usfirst.frc.team4256.robot;

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
	final static R_Xbox4256 driverStick = new R_Xbox4256(0);
	final static R_Xbox4256 gunnerStick = new R_Xbox4256(1);
	//Robot Input
	final static R_Gyrometer4256 gyro = new R_Gyrometer4256(Parameters.Gyrometer_updateHz, Parameters.Gyrometer_protectedZoneStart, Parameters.Gyrometer_protectedZoneSize);
	//Robot Output
	final static R_CANTalon4256 rotator1 = new R_CANTalon4256(Parameters.Swerve_module1rotator, false);
	final static R_CANTalon4256 rotator2 = new R_CANTalon4256(Parameters.Swerve_module2rotator, false);
	final static R_CANTalon4256 rotator3 = new R_CANTalon4256(Parameters.Swerve_module3rotator, false);
	final static R_CANTalon4256 rotator4 = new R_CANTalon4256(Parameters.Swerve_module4rotator, false);
	final static R_SwerveModule2017 module1 = new R_SwerveModule2017(rotator1, Parameters.Swerve_module1drive1, Parameters.Swerve_module1drive2);
	final static R_SwerveModule2017 module2 = new R_SwerveModule2017(rotator2, Parameters.Swerve_module2drive1, Parameters.Swerve_module2drive2);
	final static R_SwerveModule2017 module3 = new R_SwerveModule2017(rotator3, Parameters.Swerve_module3drive1, Parameters.Swerve_module3drive2);
	final static R_SwerveModule2017 module4 = new R_SwerveModule2017(rotator4, Parameters.Swerve_module4drive1, Parameters.Swerve_module4drive2);
	final static R_Base2017 base = new R_Base2017(module1, module2, module3, module4);
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
	}
}

