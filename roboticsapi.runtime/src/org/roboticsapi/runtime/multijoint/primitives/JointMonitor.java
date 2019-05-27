
package org.roboticsapi.runtime.multijoint.primitives;

import org.roboticsapi.runtime.core.types.RPIint;
import org.roboticsapi.runtime.core.types.RPIstring;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Parameter;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * Monitor reading last commanded and measured positions from a robot
 */
public class JointMonitor extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "RobotArm::JointMonitor";

	/** Last commanded acceleration of selected axis */
	private final OutPort outCommandedAcceleration = new OutPort("outCommandedAcceleration");

	/** Last commanded position of selected axis */
	private final OutPort outCommandedPosition = new OutPort("outCommandedPosition");

	/** Last commanded velocity of selected axis */
	private final OutPort outCommandedVelocity = new OutPort("outCommandedVelocity");

	/** Status (nonzero means error) */
	private final OutPort outError = new OutPort("outError");

	/** Last measured acceleration of selected axis */
	private final OutPort outMeasuredAcceleration = new OutPort("outMeasuredAcceleration");

	/** Last measured position of selected axis */
	private final OutPort outMeasuredPosition = new OutPort("outMeasuredPosition");

	/** Last measured velocity of selected axis */
	private final OutPort outMeasuredVelocity = new OutPort("outMeasuredVelocity");

	/** Name of the LBR */
	private final Parameter<RPIstring> paramRobot = new Parameter<RPIstring>("Robot", new RPIstring(""));

	/** Number of axis to control (0-based) */
	private final Parameter<RPIint> paramAxis = new Parameter<RPIint>("Axis", new RPIint("0"));

	public JointMonitor() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(outCommandedAcceleration);
		add(outCommandedPosition);
		add(outCommandedVelocity);
		add(outError);
		add(outMeasuredAcceleration);
		add(outMeasuredPosition);
		add(outMeasuredVelocity);

		// Add all parameters
		add(paramRobot);
		add(paramAxis);
	}

	/**
	 * Creates monitor reading last commanded and measured positions from a robot
	 *
	 * @param robot Name of the LBR
	 * @param axis  Number of axis to control (0-based)
	 */
	public JointMonitor(RPIstring paramRobot, RPIint paramAxis) {
		this();

		// Set the parameters
		setRobot(paramRobot);
		setAxis(paramAxis);
	}

	/**
	 * Creates monitor reading last commanded and measured positions from a robot
	 *
	 * @param robot Name of the LBR
	 * @param axis  Number of axis to control (0-based)
	 */
	public JointMonitor(String paramRobot, Integer paramAxis) {
		this(new RPIstring(paramRobot), new RPIint(paramAxis));
	}

	/**
	 * Last commanded acceleration of selected axis
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutCommandedAcceleration() {
		return this.outCommandedAcceleration;
	}

	/**
	 * Last commanded position of selected axis
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutCommandedPosition() {
		return this.outCommandedPosition;
	}

	/**
	 * Last commanded velocity of selected axis
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
	 * Last measured acceleration of selected axis
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutMeasuredAcceleration() {
		return this.outMeasuredAcceleration;
	}

	/**
	 * Last measured position of selected axis
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutMeasuredPosition() {
		return this.outMeasuredPosition;
	}

	/**
	 * Last measured velocity of selected axis
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutMeasuredVelocity() {
		return this.outMeasuredVelocity;
	}

	/**
	 * Name of the LBR
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<RPIstring> getRobot() {
		return this.paramRobot;
	}

	/**
	 * Sets a parameter of the block: Name of the LBR
	 * 
	 * @param value new value of the parameter
	 */
	public final void setRobot(RPIstring value) {
		this.paramRobot.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Name of the LBR
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
