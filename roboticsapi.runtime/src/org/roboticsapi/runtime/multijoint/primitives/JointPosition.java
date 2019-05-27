
package org.roboticsapi.runtime.multijoint.primitives;

import org.roboticsapi.runtime.core.types.RPIint;
import org.roboticsapi.runtime.core.types.RPIstring;
import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Parameter;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * Module for controlling a robot axis using position
 */
public class JointPosition extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "RobotArm::JointPosition";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** Destination value for selected axis */
	private final InPort inPosition = new InPort("inPosition");

	/** Result (nonzero means error) */
	private final OutPort outError = new OutPort("outError");

	/** Concurrent access error (joint is used by another primitive) */
	private final OutPort outErrorConcurrentAccess = new OutPort("outErrorConcurrentAccess");

	/** Illegal joint position (the value of inPosition is invalid) */
	private final OutPort outErrorIllegalPosition = new OutPort("outErrorIllegalPosition");

	/** Joint error (the joint is not operational) */
	private final OutPort outErrorJointFailed = new OutPort("outErrorJointFailed");

	/** Name of the robot */
	private final Parameter<RPIstring> paramRobot = new Parameter<RPIstring>("Robot", new RPIstring(""));

	/** Number of axis to control (0-based) */
	private final Parameter<RPIint> paramAxis = new Parameter<RPIint>("Axis", new RPIint("0"));

	public JointPosition() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inPosition);
		add(outError);
		add(outErrorConcurrentAccess);
		add(outErrorIllegalPosition);
		add(outErrorJointFailed);

		// Add all parameters
		add(paramRobot);
		add(paramAxis);
	}

	/**
	 * Creates module for controlling a robot axis using position
	 *
	 * @param robot Name of the robot
	 * @param axis  Number of axis to control (0-based)
	 */
	public JointPosition(RPIstring paramRobot, RPIint paramAxis) {
		this();

		// Set the parameters
		setRobot(paramRobot);
		setAxis(paramAxis);
	}

	/**
	 * Creates module for controlling a robot axis using position
	 *
	 * @param robot Name of the robot
	 * @param axis  Number of axis to control (0-based)
	 */
	public JointPosition(String paramRobot, Integer paramAxis) {
		this(new RPIstring(paramRobot), new RPIint(paramAxis));
	}

	/**
	 * Activation port
	 *
	 * @return the input port of the block
	 */
	public final InPort getInActive() {
		return this.inActive;
	}

	/**
	 * Destination value for selected axis
	 *
	 * @return the input port of the block
	 */
	public final InPort getInPosition() {
		return this.inPosition;
	}

	/**
	 * Result (nonzero means error)
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutError() {
		return this.outError;
	}

	/**
	 * Concurrent access error (joint is used by another primitive)
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutErrorConcurrentAccess() {
		return this.outErrorConcurrentAccess;
	}

	/**
	 * Illegal joint position (the value of inPosition is invalid)
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutErrorIllegalPosition() {
		return this.outErrorIllegalPosition;
	}

	/**
	 * Joint error (the joint is not operational)
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutErrorJointFailed() {
		return this.outErrorJointFailed;
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
	 * Number of axis to control (0-based)
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<RPIint> getAxis() {
		return this.paramAxis;
	}

	/**
	 * Sets a parameter of the block: Number of axis to control (0-based)
	 * 
	 * @param value new value of the parameter
	 */
	public final void setAxis(RPIint value) {
		this.paramAxis.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Number of axis to control (0-based)
	 * 
	 * @param value new value of the parameter
	 */
	public final void setAxis(Integer value) {
		this.setAxis(new RPIint(value));
	}

}
