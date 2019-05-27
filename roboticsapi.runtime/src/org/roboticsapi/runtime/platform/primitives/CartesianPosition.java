package org.roboticsapi.runtime.platform.primitives;

import org.roboticsapi.runtime.core.types.RPIstring;
import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Parameter;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * Module to command the Cartesian position.
 */
public class CartesianPosition extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "CartesianPosition::CartesianPosition";

	/** Desired position (of moving frame relative to reference frame) */
	private final InPort inPosition = new InPort("inPosition");

	/** Status (nonzero means error) */
	private final OutPort outError = new OutPort("outError");

	/** Concurrent access error (device is used by another primitive) */
	private final OutPort outErrorConcurrentAccess = new OutPort("outErrorConcurrentAccess");

	/** Device error (the devic eis not operational) */
	private final OutPort outErrorDeviceFailed = new OutPort("outErrorDeviceFailed");

	/** Illegal position (the value of inPosition is invalid) */
	private final OutPort outErrorIllegalPosition = new OutPort("outErrorIllegalPosition");

	/** Name of the robot */
	private final Parameter<RPIstring> paramRobot = new Parameter<RPIstring>("Robot", new RPIstring(""));

	public CartesianPosition() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inPosition);
		add(outError);
		add(outErrorConcurrentAccess);
		add(outErrorDeviceFailed);
		add(outErrorIllegalPosition);

		// Add all parameters
		add(paramRobot);
	}

	/**
	 * Creates module to command the Cartesian position.
	 * 
	 * @param robot Name of the robot
	 */
	public CartesianPosition(RPIstring paramRobot) {
		this();

		// Set the parameters
		setRobot(paramRobot);
	}

	/**
	 * Creates module to command the Cartesian position.
	 * 
	 * @param robot Name of the robot
	 */
	public CartesianPosition(String paramRobot) {
		this(new RPIstring(paramRobot));
	}

	/**
	 * Desired position (of moving frame relative to reference frame)
	 * 
	 * @return the input port of the block
	 */
	public final InPort getInPosition() {
		return this.inPosition;
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
	 * Concurrent access error (device is used by another primitive)
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutErrorConcurrentAccess() {
		return this.outErrorConcurrentAccess;
	}

	/**
	 * Device error (the devic eis not operational)
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutErrorDeviceFailed() {
		return this.outErrorDeviceFailed;
	}

	/**
	 * Illegal position (the value of inPosition is invalid)
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutErrorIllegalPosition() {
		return this.outErrorIllegalPosition;
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
