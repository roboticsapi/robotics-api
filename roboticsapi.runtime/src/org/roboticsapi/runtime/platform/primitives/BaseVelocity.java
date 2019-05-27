package org.roboticsapi.runtime.platform.primitives;

import org.roboticsapi.runtime.core.types.RPIstring;
import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Parameter;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * Module to command the velocity of a robot base.
 */
public class BaseVelocity extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "RobotBase::BaseVelocity";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** Desired velocity */
	private final InPort inVel = new InPort("inVel");

	/** Status (nonzero means error) */
	private final OutPort outError = new OutPort("outError");

	/** Base error (the base is not operational) */
	private final OutPort outErrorBaseFailed = new OutPort("outErrorBaseFailed");

	/** Concurrent access error (base is used by another primitive) */
	private final OutPort outErrorConcurrentAccess = new OutPort("outErrorConcurrentAccess");

	/** Illegal base velocity (the value of inVelocity is invalid) */
	private final OutPort outErrorIllegalVelocity = new OutPort("outErrorIllegalVelocity");

	/** Name of the robot */
	private final Parameter<RPIstring> paramRobot = new Parameter<RPIstring>("Robot", new RPIstring(""));

	public BaseVelocity() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inVel);
		add(outError);
		add(outErrorBaseFailed);
		add(outErrorConcurrentAccess);
		add(outErrorIllegalVelocity);

		// Add all parameters
		add(paramRobot);
	}

	/**
	 * Creates module to command the velocity of a robot base.
	 * 
	 * @param robot Name of the robot
	 */
	public BaseVelocity(RPIstring paramRobot) {
		this();

		// Set the parameters
		setRobot(paramRobot);
	}

	/**
	 * Creates module to command the velocity of a robot base.
	 * 
	 * @param robot Name of the robot
	 */
	public BaseVelocity(String paramRobot) {
		this(new RPIstring(paramRobot));
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
	 * Desired velocity
	 * 
	 * @return the input port of the block
	 */
	public final InPort getInVel() {
		return this.inVel;
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
	 * Base error (the base is not operational)
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutErrorBaseFailed() {
		return this.outErrorBaseFailed;
	}

	/**
	 * Concurrent access error (base is used by another primitive)
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutErrorConcurrentAccess() {
		return this.outErrorConcurrentAccess;
	}

	/**
	 * Illegal base velocity (the value of inVelocity is invalid)
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutErrorIllegalVelocity() {
		return this.outErrorIllegalVelocity;
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
