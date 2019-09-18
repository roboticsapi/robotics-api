package org.roboticsapi.framework.robot.runtime.rpi.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * Direct kinematics module for general RPI robots
 */
public class Kin extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "ArmKinematics::Kin";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** Value of all joints */
	private final InPort inJoints = new InPort("inJoints");

	/** Resulting frame */
	private final OutPort outFrame = new OutPort("outFrame");

	/** Robot device */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIstring> paramRobot = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIstring>(
			"Robot", new org.roboticsapi.facet.runtime.rpi.core.types.RPIstring(""));

	public Kin() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inJoints);
		add(outFrame);

		// Add all parameters
		add(paramRobot);
	}

	/**
	 * Creates direct kinematics module for general RPI robots
	 *
	 * @param robot Robot device
	 */
	public Kin(org.roboticsapi.facet.runtime.rpi.core.types.RPIstring paramRobot) {
		this();

		// Set the parameters
		setRobot(paramRobot);
	}

	/**
	 * Creates direct kinematics module for general RPI robots
	 *
	 * @param robot Robot device
	 */
	public Kin(String paramRobot) {
		this(new org.roboticsapi.facet.runtime.rpi.core.types.RPIstring(paramRobot));
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
	 * Value of all joints
	 *
	 * @return the input port of the block
	 */
	public final InPort getInJoints() {
		return this.inJoints;
	}

	/**
	 * Resulting frame
	 *
	 * @return the output port of the block
	 */
	public final OutPort getOutFrame() {
		return this.outFrame;
	}

	/**
	 * Robot device
	 *
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIstring> getRobot() {
		return this.paramRobot;
	}

	/**
	 * Sets a parameter of the block: Robot device
	 *
	 * @param value new value of the parameter
	 */
	public final void setRobot(org.roboticsapi.facet.runtime.rpi.core.types.RPIstring value) {
		this.paramRobot.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Robot device
	 *
	 * @param value new value of the parameter
	 */
	public final void setRobot(String value) {
		this.setRobot(new org.roboticsapi.facet.runtime.rpi.core.types.RPIstring(value));
	}

}
