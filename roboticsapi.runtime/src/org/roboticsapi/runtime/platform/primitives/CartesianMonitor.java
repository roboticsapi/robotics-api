package org.roboticsapi.runtime.platform.primitives;

import org.roboticsapi.runtime.core.types.RPIstring;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Parameter;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * Module to monitor the velocity and position of a robot base.
 */
public class CartesianMonitor extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "CartesianPosition::CartesianMonitor";

	/**
	 * Current commanded position (transformation from reference frame to moving
	 * frame)
	 */
	private final OutPort outCommandedPosition = new OutPort("outCommandedPosition");

	/**
	 * Current commanded velocity (velocity of moving frame relative to reference
	 * frame, expressed in moving frame)
	 */
	private final OutPort outCommandedVelocity = new OutPort("outCommandedVelocity");

	/** Status (nonzero means error) */
	private final OutPort outError = new OutPort("outError");

	/**
	 * Current measured position (transformation from reference frame to moving
	 * frame)
	 */
	private final OutPort outMeasuredPosition = new OutPort("outMeasuredPosition");

	/**
	 * Current measured velocity (velocity of moving frame relative to reference
	 * frame, expressed in moving frame)
	 */
	private final OutPort outMeasuredVelocity = new OutPort("outMeasuredVelocity");

	/** Name of the robot */
	private final Parameter<RPIstring> paramRobot = new Parameter<RPIstring>("Robot", new RPIstring(""));

	public CartesianMonitor() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(outCommandedPosition);
		add(outCommandedVelocity);
		add(outError);
		add(outMeasuredPosition);
		add(outMeasuredVelocity);

		// Add all parameters
		add(paramRobot);
	}

	/**
	 * Creates module to monitor the velocity and position of a robot base.
	 * 
	 * @param robot Name of the robot
	 */
	public CartesianMonitor(RPIstring paramRobot) {
		this();

		// Set the parameters
		setRobot(paramRobot);
	}

	/**
	 * Creates module to monitor the velocity and position of a robot base.
	 * 
	 * @param robot Name of the robot
	 */
	public CartesianMonitor(String paramRobot) {
		this(new RPIstring(paramRobot));
	}

	/**
	 * Current commanded position (transformation from reference frame to moving
	 * frame)
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutCommandedPosition() {
		return this.outCommandedPosition;
	}

	/**
	 * Current commanded velocity (velocity of moving frame relative to reference
	 * frame, expressed in moving frame)
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutCommandedVelocity() {
		return this.outCommandedVelocity;
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
	public final OutPort getOutMeasuredPosition() {
		return this.outMeasuredPosition;
	}

	/**
	 * Current measured velocity (velocity of moving frame relative to reference
	 * frame, expressed in moving frame)
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutMeasuredVelocity() {
		return this.outMeasuredVelocity;
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
