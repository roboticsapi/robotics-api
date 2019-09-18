package org.roboticsapi.framework.robot.runtime.rpi.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIint;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIstring;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIVector;

/**
 * Module for configuring the tool of a robot
 */
public class ToolParameters extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "RobotArm::ToolParameters";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** Tool's center of mass */
	private final InPort inCOM = new InPort("inCOM");

	/** Tool's moment of inertia */
	private final InPort inMOI = new InPort("inMOI");

	/** Tool's mass */
	private final InPort inMass = new InPort("inMass");

	/** true, when the switching action has completed */
	private final OutPort outCompleted = new OutPort("outCompleted");

	/** Result (nonzero means jointError) */
	private final OutPort outError = new OutPort("outError");

	/** Name of the Robot */
	private final Parameter<RPIstring> paramRobot = new Parameter<RPIstring>("Robot", new RPIstring(""));

	/** Axis to set payload for (0 based) */
	private final Parameter<RPIint> paramAxis = new Parameter<RPIint>("Axis", new RPIint("0"));

	/** Payload mass */
	private final Parameter<RPIdouble> paramMass = new Parameter<RPIdouble>("Mass", new RPIdouble("0"));

	/** Center of mass */
	private final Parameter<RPIVector> paramCOM = new Parameter<RPIVector>("COM", new RPIVector("{x:0,y:0,z:0}"));

	/** Moment of inertia */
	private final Parameter<RPIVector> paramMOI = new Parameter<RPIVector>("MOI", new RPIVector("{x:0,y:0,z:0}"));

	public ToolParameters() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inCOM);
		add(inMOI);
		add(inMass);
		add(outCompleted);
		add(outError);

		// Add all parameters
		add(paramRobot);
		add(paramAxis);
		add(paramMass);
		add(paramCOM);
		add(paramMOI);
	}

	/**
	 * Creates module for configuring the tool of a robot
	 * 
	 * @param robot Name of the Robot
	 * @param axis  Axis to set payload for (0 based)
	 */
	public ToolParameters(RPIstring paramRobot, RPIint paramAxis) {
		this();

		// Set the parameters
		setRobot(paramRobot);
		setAxis(paramAxis);
	}

	/**
	 * Creates module for configuring the tool of a robot
	 * 
	 * @param robot Name of the Robot
	 * @param axis  Axis to set payload for (0 based)
	 */
	public ToolParameters(String paramRobot, Integer paramAxis) {
		this(new RPIstring(paramRobot), new RPIint(paramAxis));
	}

	/**
	 * Creates module for configuring the tool of a robot
	 * 
	 * @param paramRobot Name of the Robot
	 * @param paramAxis  Axis to set payload for (0 based)
	 * @param paramMass  Payload mass
	 * @param paramCOM   Center of mass
	 * @param paramMOI   Moment of inertia
	 */
	public ToolParameters(RPIstring paramRobot, RPIint paramAxis, RPIdouble paramMass, RPIVector paramCOM,
			RPIVector paramMOI) {
		this();

		// Set the parameters
		setRobot(paramRobot);
		setAxis(paramAxis);
		setMass(paramMass);
		setCOM(paramCOM);
		setMOI(paramMOI);
	}

	/**
	 * Creates module for configuring the tool of a robot
	 * 
	 * @param paramRobot Name of the Robot
	 * @param paramAxis  Axis to set payload for (0 based)
	 * @param paramMass  Payload mass
	 * @param paramCOM   Center of mass
	 * @param paramMOI   Moment of inertia
	 */
	public ToolParameters(String paramRobot, Integer paramAxis, Double paramMass, String paramCOM, String paramMOI) {
		this(new RPIstring(paramRobot), new RPIint(paramAxis), new RPIdouble(paramMass), new RPIVector(paramCOM),
				new RPIVector(paramMOI));
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
	 * Tool's center of mass
	 * 
	 * @return the input port of the block
	 */
	public final InPort getInCOM() {
		return this.inCOM;
	}

	/**
	 * Tool's moment of inertia
	 * 
	 * @return the input port of the block
	 */
	public final InPort getInMOI() {
		return this.inMOI;
	}

	/**
	 * Tool's mass
	 * 
	 * @return the input port of the block
	 */
	public final InPort getInMass() {
		return this.inMass;
	}

	/**
	 * true, when the switching action has completed
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutCompleted() {
		return this.outCompleted;
	}

	/**
	 * Result (nonzero means jointError)
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutError() {
		return this.outError;
	}

	/**
	 * Name of the Robot
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<RPIstring> getRobot() {
		return this.paramRobot;
	}

	/**
	 * Sets a parameter of the block: Name of the Robot
	 * 
	 * @param value new value of the parameter
	 */
	public final void setRobot(RPIstring value) {
		this.paramRobot.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Name of the Robot
	 * 
	 * @param value new value of the parameter
	 */
	public final void setRobot(String value) {
		this.setRobot(new RPIstring(value));
	}

	/**
	 * Axis to set payload for (0 based)
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<RPIint> getAxis() {
		return this.paramAxis;
	}

	/**
	 * Sets a parameter of the block: Axis to set payload for (0 based)
	 * 
	 * @param value new value of the parameter
	 */
	public final void setAxis(RPIint value) {
		this.paramAxis.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Axis to set payload for (0 based)
	 * 
	 * @param value new value of the parameter
	 */
	public final void setAxis(Integer value) {
		this.setAxis(new RPIint(value));
	}

	/**
	 * Payload mass
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<RPIdouble> getMass() {
		return this.paramMass;
	}

	/**
	 * Sets a parameter of the block: Payload mass
	 * 
	 * @param value new value of the parameter
	 */
	public final void setMass(RPIdouble value) {
		this.paramMass.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Payload mass
	 * 
	 * @param value new value of the parameter
	 */
	public final void setMass(Double value) {
		this.setMass(new RPIdouble(value));
	}

	/**
	 * Center of mass
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<RPIVector> getCOM() {
		return this.paramCOM;
	}

	/**
	 * Sets a parameter of the block: Center of mass
	 * 
	 * @param value new value of the parameter
	 */
	public final void setCOM(RPIVector value) {
		this.paramCOM.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Center of mass
	 * 
	 * @param value new value of the parameter
	 */
	public final void setCOM(String value) {
		this.setCOM(new RPIVector(value));
	}

	/**
	 * Moment of inertia
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<RPIVector> getMOI() {
		return this.paramMOI;
	}

	/**
	 * Sets a parameter of the block: Moment of inertia
	 * 
	 * @param value new value of the parameter
	 */
	public final void setMOI(RPIVector value) {
		this.paramMOI.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Moment of inertia
	 * 
	 * @param value new value of the parameter
	 */
	public final void setMOI(String value) {
		this.setMOI(new RPIVector(value));
	}

}
