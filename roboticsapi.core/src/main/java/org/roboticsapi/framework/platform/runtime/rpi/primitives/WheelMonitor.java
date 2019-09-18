package org.roboticsapi.framework.platform.runtime.rpi.primitives;

import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIint;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIstring;

/**
 * Module to monitor the velocity and position of a robot base wheel.
 */
public class WheelMonitor extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "RobotBase::WheelMonitor";

	/** Current position */
	private final OutPort outPos = new OutPort("outPos");

	/** Current velocity */
	private final OutPort outVel = new OutPort("outVel");

	/** Name of the robot */
	private final Parameter<RPIstring> paramRobot = new Parameter<RPIstring>("Robot", new RPIstring(""));

	/** Number of the wheel */
	private final Parameter<RPIint> paramWheel = new Parameter<RPIint>("Wheel", new RPIint("0"));

	public WheelMonitor() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(outPos);
		add(outVel);

		// Add all parameters
		add(paramRobot);
		add(paramWheel);
	}

	/**
	 * Creates module to monitor the velocity and position of a robot base wheel.
	 * 
	 * @param robot Name of the robot
	 * @param wheel Number of the wheel
	 */
	public WheelMonitor(RPIstring paramRobot, RPIint paramWheel) {
		this();

		// Set the parameters
		setRobot(paramRobot);
		setWheel(paramWheel);
	}

	/**
	 * Creates module to monitor the velocity and position of a robot base wheel.
	 * 
	 * @param robot Name of the robot
	 * @param wheel Number of the wheel
	 */
	public WheelMonitor(String paramRobot, Integer paramWheel) {
		this(new RPIstring(paramRobot), new RPIint(paramWheel));
	}

	/**
	 * Current position
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutPos() {
		return this.outPos;
	}

	/**
	 * Current velocity
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutVel() {
		return this.outVel;
	}

	/**
	 * Name of the robot
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<RPIstring> getRobot() {
		return this.paramRobot;
	}

	/**
	 * Sets a parameter of the block: Name of the robot
	 * 
	 * @param value new value of the parameter
	 */
	public final void setRobot(RPIstring value) {
		this.paramRobot.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Name of the robot
	 * 
	 * @param value new value of the parameter
	 */
	public final void setRobot(String value) {
		this.setRobot(new RPIstring(value));
	}

	/**
	 * Number of the wheel
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<RPIint> getWheel() {
		return this.paramWheel;
	}

	/**
	 * Sets a parameter of the block: Number of the wheel
	 * 
	 * @param value new value of the parameter
	 */
	public final void setWheel(RPIint value) {
		this.paramWheel.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Number of the wheel
	 * 
	 * @param value new value of the parameter
	 */
	public final void setWheel(Integer value) {
		this.setWheel(new RPIint(value));
	}

}
