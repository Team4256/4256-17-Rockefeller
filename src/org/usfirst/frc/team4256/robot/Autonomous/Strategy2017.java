package org.usfirst.frc.team4256.robot.Autonomous;

import com.cyborgcats.reusable.Autonomous.Events;
import com.cyborgcats.reusable.Autonomous.Leash;
import com.cyborgcats.reusable.Autonomous.Odometer;
import com.cyborgcats.reusable.Autonomous.Strategy;

import com.cyborgcats.reusable.Autonomous.Events.Command;

public abstract class Strategy2017 extends Strategy {
	public static final double Yi = 2.82;//initial y value
	public static final double leftPeg = 60, centerPeg = 0, rightPeg = 330, loadingStation = 320;
	
	protected final StartingPosition posI;
	
	protected Strategy2017(final StartingPosition posI, final Odometer odometer) {
		super(odometer);
		this.posI = posI;
		odometer.setOrigin(odometer.getX(false) - posI.x, odometer.getY(false) - Yi);
	}
	
	
	@Override
	protected Leash getLeash() {
		switch (posI) {
		case LEFT: return leftLeash();
		case CENTER: return centerLeash();
		case RIGHT: return rightLeash();
		default: return centerLeash();
		}
	}
	@Override
	protected Events getEvents() {
		switch (posI) {
		case LEFT: return leftEvents();
		case CENTER: return centerEvents();
		case RIGHT: return rightEvents();
		default: return centerEvents();
		}
	}
	
	protected Leash leftLeash() {return super.getLeash();}
	protected Leash centerLeash() {return super.getLeash();}
	protected Leash rightLeash() {return super.getLeash();}
	protected Events leftEvents() {return super.getEvents();}
	protected Events centerEvents() {return super.getEvents();}
	protected Events rightEvents() {return super.getEvents();}
	
	
	public static enum StartingPosition {
		LEFT(-11.38),
		CENTER(0.184),
		RIGHT(8.16);
		
		private final double x;
		
		StartingPosition(final double initialX) {this.x = initialX;}
		
		protected double x() {return x;}
	}
	public static enum FieldPieceConfig {LEFT, RIGHT}

	
	
	/**
	 * @param instructions a 2D array of Strings with any number of rows and 4 columns<br>
	 * first column: gear action ({@linkplain org.usfirst.frc.team4256.robot.Gearer.Abilities Abilities})<br>
	 * second column: robot orientation (degrees)<br>
	 * third column: maximum spin speed (percent)<br>
	 * fourth column: whether to pause code until actual orientation matches desired orientation (<code>"wait"</code> or <code>"pass"</code>)
	 * 
	 * @return an array of executable commands
	 * @see Command
	*/
	public static Command[] getFromArray(final String[][] instructions) {
		Command[] commands = new Command[instructions.length];
		
		for (int i = 0; i < instructions.length; i++) {
			final String[] instruction = instructions[i];
			
			commands[i] = (drive, sys) -> {
				final String gearAction = instruction[0];
				final double desiredAngle = Double.parseDouble(instruction[1]),
							 maxSpin = Double.parseDouble(instruction[2])/100.0;
				final boolean wait = instruction[3] == "wait";
				
				if (wait) {
					drive.setSpeed(0.0);
					while (Math.abs(drive.face((double)desiredAngle, maxSpin)) > 5.0) drive.completeLoopUpdate();
				}else drive.face((double)desiredAngle, maxSpin);
				
				sys.get("Gearer").perform(gearAction, null);
			};
		}
		
		return commands;
	}
}
