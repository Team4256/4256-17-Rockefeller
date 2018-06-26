package org.usfirst.frc.team4256.robot.Autonomous;

import org.usfirst.frc.team4256.robot.Autonomous.Strategy2017.StartingPosition;

import com.cyborgcats.reusable.Autonomous.Odometer;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public final class Coach {
	private Coach() {}
	
	public static final SendableChooser<Strategies> strategyChooser = new SendableChooser<Strategies>();
	public static final SendableChooser<StartingPosition> positionChooser = new SendableChooser<StartingPosition>();
	
	public static void init() {
		listOptions();
		SmartDashboard.putData("Strategy", strategyChooser);
		SmartDashboard.putData("Position", positionChooser);
	}
	
	private static void listOptions() {
		boolean addedDefault = false;
		for (Strategies strategy : Strategies.values()) {
			if (addedDefault) strategyChooser.addObject(strategy.toString(), strategy);
			else {
				strategyChooser.addDefault(strategy.toString(), strategy);
				addedDefault = true;
			}
		}
		positionChooser.addObject(StartingPosition.LEFT.name(), StartingPosition.LEFT);
		positionChooser.addDefault(StartingPosition.CENTER.name(), StartingPosition.CENTER);
		positionChooser.addObject(StartingPosition.RIGHT.name(), StartingPosition.RIGHT);
	}
	
	public static Strategy2017 selectedStrategy(final Odometer odometer) {
		switch(strategyChooser.getSelected()) {
		case Slither: return new S_Slither(odometer);
		default: return new S_Slither(odometer);
		}
	}


	public static enum Strategies {
		Slither("Slither Drive");
		
		private final String readableName;
		Strategies(final String readableName) {this.readableName = readableName;}
		@Override
		public String toString() {return readableName;}
	}
}
