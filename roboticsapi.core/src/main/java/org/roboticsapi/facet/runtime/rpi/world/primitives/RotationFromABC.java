package org.roboticsapi.facet.runtime.rpi.world.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * A rotation combiner (creates a rotation data flow from A, B, C angles)
 */
public class RotationFromABC extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::RotationFromABC";

	/** A rotation (around Z) */
	private final InPort inA = new InPort("inA");

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** B rotation (around Y) */
	private final InPort inB = new InPort("inB");

	/** C rotation (around X) */
	private final InPort inC = new InPort("inC");

	/** Combined rotation */
	private final OutPort outValue = new OutPort("outValue");

	/** A rotation */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramA = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"A", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("0"));

	/** B rotation */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramB = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"B", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("0"));

	/** C rotation */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramC = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"C", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("0"));

	public RotationFromABC() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inA);
		add(inActive);
		add(inB);
		add(inC);
		add(outValue);

		// Add all parameters
		add(paramA);
		add(paramB);
		add(paramC);
	}

	/**
	 * Creates a rotation combiner (creates a rotation data flow from A, B, C
	 * angles)
	 *
	 * @param paramA A rotation
	 * @param paramB B rotation
	 * @param paramC C rotation
	 */
	public RotationFromABC(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramA,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramB,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramC) {
		this();

		// Set the parameters
		setA(paramA);
		setB(paramB);
		setC(paramC);
	}

	/**
	 * Creates a rotation combiner (creates a rotation data flow from A, B, C
	 * angles)
	 *
	 * @param paramA A rotation
	 * @param paramB B rotation
	 * @param paramC C rotation
	 */
	public RotationFromABC(Double paramA, Double paramB, Double paramC) {
		this(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramA),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramB),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramC));
	}

	/**
	 * A rotation (around Z)
	 *
	 * @return the input port of the block
	 */
	public final InPort getInA() {
		return this.inA;
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
	 * B rotation (around Y)
	 *
	 * @return the input port of the block
	 */
	public final InPort getInB() {
		return this.inB;
	}

	/**
	 * C rotation (around X)
	 *
	 * @return the input port of the block
	 */
	public final InPort getInC() {
		return this.inC;
	}

	/**
	 * Combined rotation
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

	/**
	 * A rotation
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getA() {
		return this.paramA;
	}

	/**
	 * Sets a parameter of the block: A rotation
	 * 
	 * @param value new value of the parameter
	 */
	public final void setA(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramA.setValue(value);
	}

	/**
	 * Sets a parameter of the block: A rotation
	 * 
	 * @param value new value of the parameter
	 */
	public final void setA(Double value) {
		this.setA(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

	/**
	 * B rotation
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getB() {
		return this.paramB;
	}

	/**
	 * Sets a parameter of the block: B rotation
	 * 
	 * @param value new value of the parameter
	 */
	public final void setB(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramB.setValue(value);
	}

	/**
	 * Sets a parameter of the block: B rotation
	 * 
	 * @param value new value of the parameter
	 */
	public final void setB(Double value) {
		this.setB(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

	/**
	 * C rotation
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getC() {
		return this.paramC;
	}

	/**
	 * Sets a parameter of the block: C rotation
	 * 
	 * @param value new value of the parameter
	 */
	public final void setC(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramC.setValue(value);
	}

	/**
	 * Sets a parameter of the block: C rotation
	 * 
	 * @param value new value of the parameter
	 */
	public final void setC(Double value) {
		this.setC(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

}
