package org.roboticsapi.runtime.platform.primitives;

import org.roboticsapi.runtime.core.types.RPIstring;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Parameter;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * Module to monitor the velocity and position of a robot base.
 */
public class BaseMonitor extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "RobotBase::BaseMonitor";

	/**
	 * Current commanded velocity (velocity of moving frame relative to reference
	 * frame, expressed in moving frame)
	 */
	private final OutPort outCmdVel = new OutPort("outCmdVel");

	/** Status (nonzero means error) */
	private final OutPort outError = new OutPort("outError");

	/**
	 * Current measured position (transformation from reference frame to moving
	 * frame)
	 */
	private final OutPort outPos = new OutPort("outPos");

	/**
	 * Current measured velocity (velocity of moving frame relative to reference
	 * frame, expressed in moving frame)
	 */
	private final OutPort outVel = new OutPort("outVel");

	/** Name of the robot */
	private final Parameter<RPIstring> paramRobot = new Parameter<RPIstring>("Robot", new RPIstring(""));

	public BaseMonitor() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(outCmdVel);
		add(outError);
		add(outPos);
		add(outVel);

		// Add all parameters
		add(paramRobot);
	}

	/**
	 * Creates module to monitor the velocity and position of a robot base.
	 * 
	 * @param robot Name of the robot
	 */
	public BaseMonitor(RPIstring paramRobot) {
		this();

		// Set the parameters
		setRobot(paramRobot);
	}

	/**
	 * Creates module to monitor the velocity and position of a robot base.
	 * 
	 * @param robot Name of the robot
	 */
	public BaseMonitor(String paramRobot) {
		this(new RPIstring(paramRobot));
	}

	/**
	 * Current commanded velocity (velocity of moving frame relative to reference
	 * frame, expressed in moving frame)
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutCmdVel() {
		return this.outCmdVel;
	}

	/**
	 * Status (nonzero means error)
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutError() {
		return this.outError;
	}

	/**
	 * Current measured position (transformation from reference frame to moving
	 * frame)
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutPos() {
		return this.outPos;
	}

	/**
	 * Current measured velocity (velocity of moving frame relative to reference
	 * frame, expressed in moving frame)
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

}
