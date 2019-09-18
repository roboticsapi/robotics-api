package org.roboticsapi.framework.platform.runtime.rpi.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * Module to calculate a desired velocity from current position and goal.
 */
public class OmnidirectionalBaseController extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "RobotBase::OmnidirectionalBaseController";

	/** Target position */
	private final InPort inDest = new InPort("inDest");

	/** Current position */
	private final InPort inPos = new InPort("inPos");

	/** Current velocity */
	private final InPort inVel = new InPort("inVel");

	/** Result (completion) */
	private final OutPort outCompleted = new OutPort("outCompleted");

	/** Target velocity */
	private final OutPort outVel = new OutPort("outVel");

	/** Maximum X velocity (m/s) */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramVelX = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"velX", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("0.25"));

	/** Maximum Y velocity (m/s) */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramVelY = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"velY", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("0.25"));

	/** Maximum Rotation velocity (rad/s) */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramVelYaw = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"velYaw", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("0.3"));

	/** Proportional factor in X direction */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramPX = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"pX", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("3"));

	/** Proportional factor in Y direction */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramPY = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"pY", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("3"));

	/** Proportional factor in yaw direction */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramPYaw = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"pYaw", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("3"));

	public OmnidirectionalBaseController() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inDest);
		add(inPos);
		add(inVel);
		add(outCompleted);
		add(outVel);

		// Add all parameters
		add(paramVelX);
		add(paramVelY);
		add(paramVelYaw);
		add(paramPX);
		add(paramPY);
		add(paramPYaw);
	}

	/**
	 * Creates module to calculate a desired velocity from current position and
	 * goal.
	 *
	 * @param velX   Maximum X velocity (m/s)
	 * @param velY   Maximum Y velocity (m/s)
	 * @param velYaw Maximum Rotation velocity (rad/s)
	 * @param pX     Proportional factor in X direction
	 * @param pY     Proportional factor in Y direction
	 * @param pYaw   Proportional factor in yaw direction
	 */
	public OmnidirectionalBaseController(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramVelX,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramVelY,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramVelYaw,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramPX,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramPY,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramPYaw) {
		this();

		// Set the parameters
		setvelX(paramVelX);
		setvelY(paramVelY);
		setvelYaw(paramVelYaw);
		setpX(paramPX);
		setpY(paramPY);
		setpYaw(paramPYaw);
	}

	/**
	 * Creates module to calculate a desired velocity from current position and
	 * goal.
	 *
	 * @param velX   Maximum X velocity (m/s)
	 * @param velY   Maximum Y velocity (m/s)
	 * @param velYaw Maximum Rotation velocity (rad/s)
	 * @param pX     Proportional factor in X direction
	 * @param pY     Proportional factor in Y direction
	 * @param pYaw   Proportional factor in yaw direction
	 */
	public OmnidirectionalBaseController(Double paramVelX, Double paramVelY, Double paramVelYaw, Double paramPX,
			Double paramPY, Double paramPYaw) {
		this(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramVelX),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramVelY),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramVelYaw),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramPX),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramPY),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramPYaw));
	}

	/**
	 * Target position
	 *
	 * @return the input port of the block
	 */
	public final InPort getInDest() {
		return this.inDest;
	}

	/**
	 * Current position
	 *
	 * @return the input port of the block
	 */
	public final InPort getInPos() {
		return this.inPos;
	}

	/**
	 * Current velocity
	 *
	 * @return the input port of the block
	 */
	public final InPort getInVel() {
		return this.inVel;
	}

	/**
	 * Result (completion)
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutCompleted() {
		return this.outCompleted;
	}

	/**
	 * Target velocity
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutVel() {
		return this.outVel;
	}

	/**
	 * Maximum X velocity (m/s)
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getvelX() {
		return this.paramVelX;
	}

	/**
	 * Sets a parameter of the block: Maximum X velocity (m/s)
	 * 
	 * @param value new value of the parameter
	 */
	public final void setvelX(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramVelX.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Maximum X velocity (m/s)
	 * 
	 * @param value new value of the parameter
	 */
	public final void setvelX(Double value) {
		this.setvelX(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

	/**
	 * Maximum Y velocity (m/s)
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getvelY() {
		return this.paramVelY;
	}

	/**
	 * Sets a parameter of the block: Maximum Y velocity (m/s)
	 * 
	 * @param value new value of the parameter
	 */
	public final void setvelY(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramVelY.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Maximum Y velocity (m/s)
	 * 
	 * @param value new value of the parameter
	 */
	public final void setvelY(Double value) {
		this.setvelY(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

	/**
	 * Maximum Rotation velocity (rad/s)
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getvelYaw() {
		return this.paramVelYaw;
	}

	/**
	 * Sets a parameter of the block: Maximum Rotation velocity (rad/s)
	 * 
	 * @param value new value of the parameter
	 */
	public final void setvelYaw(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramVelYaw.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Maximum Rotation velocity (rad/s)
	 * 
	 * @param value new value of the parameter
	 */
	public final void setvelYaw(Double value) {
		this.setvelYaw(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

	/**
	 * Proportional factor in X direction
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getpX() {
		return this.paramPX;
	}

	/**
	 * Sets a parameter of the block: Proportional factor in X direction
	 * 
	 * @param value new value of the parameter
	 */
	public final void setpX(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramPX.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Proportional factor in X direction
	 * 
	 * @param value new value of the parameter
	 */
	public final void setpX(Double value) {
		this.setpX(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

	/**
	 * Proportional factor in Y direction
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getpY() {
		return this.paramPY;
	}

	/**
	 * Sets a parameter of the block: Proportional factor in Y direction
	 * 
	 * @param value new value of the parameter
	 */
	public final void setpY(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramPY.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Proportional factor in Y direction
	 * 
	 * @param value new value of the parameter
	 */
	public final void setpY(Double value) {
		this.setpY(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

	/**
	 * Proportional factor in yaw direction
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getpYaw() {
		return this.paramPYaw;
	}

	/**
	 * Sets a parameter of the block: Proportional factor in yaw direction
	 * 
	 * @param value new value of the parameter
	 */
	public final void setpYaw(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramPYaw.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Proportional factor in yaw direction
	 * 
	 * @param value new value of the parameter
	 */
	public final void setpYaw(Double value) {
		this.setpYaw(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

}
