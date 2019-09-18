package org.roboticsapi.framework.robot.runtime.rpi.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIstring;

/**
 * Inverse kinematics module for general RPI robots
 */
public class InvKin extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "ArmKinematics::InvKin";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** Destination frame */
	private final InPort inFrame = new InPort("inFrame");

	/** Hint joints */
	private final InPort inHintJoints = new InPort("inHintJoints");

	/** Result angle array for all joints */
	private final OutPort outJoints = new OutPort("outJoints");

	/** Robot device */
	private final Parameter<RPIstring> paramRobot = new Parameter<RPIstring>("Robot", new RPIstring(""));

	public InvKin() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inFrame);
		add(inHintJoints);
		add(outJoints);

		// Add all parameters
		add(paramRobot);
	}

	/**
	 * Creates inverse kinematics module for LBR robots
	 *
	 * @param robot Robot device
	 */
	public InvKin(RPIstring paramRobot) {
		this();

		// Set the parameters
		setRobot(paramRobot);
	}

	/**
	 * Creates inverse kinematics module for LBR robots
	 *
	 * @param robot Robot device
	 */
	public InvKin(String paramRobot) {
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
	 * Destination frame
	 *
	 * @return the input port of the block
	 */
	public final InPort getInFrame() {
		return this.inFrame;
	}

	/**
	 * Hint joints
	 *
	 * @return the input port of the block
	 */
	public final InPort getInHintJoints() {
		return this.inHintJoints;
	}

	/**
	 * Result angle array for all joints
	 *
	 * @return the output port of the block
	 */
	public final OutPort getOutJoints() {
		return this.outJoints;
	}

	/**
	 * Robot device
	 *
	 * @return the parameter of the block
	 */
	public final Parameter<RPIstring> getRobot() {
		return this.paramRobot;
	}

	/**
	 * Sets a parameter of the block: Robot device
	 *
	 * @param value new value of the parameter
	 */
	public final void setRobot(RPIstring value) {
		this.paramRobot.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Robot device
	 *
	 * @param value new value of the parameter
	 */
	public final void setRobot(String value) {
		this.setRobot(new RPIstring(value));
	}

}
