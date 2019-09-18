package org.roboticsapi.facet.runtime.rpi.core.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * A edge detection module (detects rising [Direction=true] or falling
 * [Direction=false] edges)
 */
public class EdgeDetection extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::EdgeDetection";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** Reset port */
	private final InPort inReset = new InPort("inReset");

	/** Activation trigger */
	private final InPort inValue = new InPort("inValue");

	/** Deactivation trigger */
	private final OutPort outValue = new OutPort("outValue");

	/** Direction of edge */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIbool> paramDirection = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIbool>(
			"Direction", new org.roboticsapi.facet.runtime.rpi.core.types.RPIbool("true"));

	public EdgeDetection() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inReset);
		add(inValue);
		add(outValue);

		// Add all parameters
		add(paramDirection);
	}

	/**
	 * Creates a edge detection module (detects rising [Direction=true] or falling
	 * [Direction=false] edges)
	 *
	 * @param direction Direction of edge
	 */
	public EdgeDetection(org.roboticsapi.facet.runtime.rpi.core.types.RPIbool paramDirection) {
		this();

		// Set the parameters
		setDirection(paramDirection);
	}

	/**
	 * Creates a edge detection module (detects rising [Direction=true] or falling
	 * [Direction=false] edges)
	 *
	 * @param direction Direction of edge
	 */
	public EdgeDetection(Boolean paramDirection) {
		this(new org.roboticsapi.facet.runtime.rpi.core.types.RPIbool(paramDirection));
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
	 * Reset port
	 *
	 * @return the input port of the block
	 */
	public final InPort getInReset() {
		return this.inReset;
	}

	/**
	 * Activation trigger
	 *
	 * @return the input port of the block
	 */
	public final InPort getInValue() {
		return this.inValue;
	}

	/**
	 * Deactivation trigger
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

	/**
	 * Direction of edge
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIbool> getDirection() {
		return this.paramDirection;
	}

	/**
	 * Sets a parameter of the block: Direction of edge
	 * 
	 * @param value new value of the parameter
	 */
	public final void setDirection(org.roboticsapi.facet.runtime.rpi.core.types.RPIbool value) {
		this.paramDirection.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Direction of edge
	 * 
	 * @param value new value of the parameter
	 */
	public final void setDirection(Boolean value) {
		this.setDirection(new org.roboticsapi.facet.runtime.rpi.core.types.RPIbool(value));
	}

}
