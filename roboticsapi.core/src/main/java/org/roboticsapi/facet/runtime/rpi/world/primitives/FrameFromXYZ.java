package org.roboticsapi.facet.runtime.rpi.world.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * A frame from XYZ module (combines values for X, Y, Z and rotation into a
 * frame datatype) [deprecated]
 */
@Deprecated
public class FrameFromXYZ extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::FrameFromXYZ";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** Rotation */
	private final InPort inRot = new InPort("inRot");

	/** X position */
	private final InPort inX = new InPort("inX");

	/** Y position */
	private final InPort inY = new InPort("inY");

	/** Z position */
	private final InPort inZ = new InPort("inZ");

	/** Combined frame */
	private final OutPort outValue = new OutPort("outValue");

	/** X position */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramX = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"X", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("0"));

	/** Y position */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramY = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"Y", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("0"));

	/** Z position */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramZ = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"Z", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("0"));

	/** A rotation */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramA = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"A", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("0"));

	/** B rotation */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramB = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"B", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("0"));

	/** C rotation */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramC = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"C", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("0"));

	public FrameFromXYZ() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inRot);
		add(inX);
		add(inY);
		add(inZ);
		add(outValue);

		// Add all parameters
		add(paramX);
		add(paramY);
		add(paramZ);
		add(paramA);
		add(paramB);
		add(paramC);
	}

	/**
	 * Creates a frame from XYZ module (combines values for X, Y, Z and rotation
	 * into a frame datatype) [deprecated]
	 *
	 * @param a A rotation
	 * @param b B rotation
	 * @param c C rotation
	 */
	public FrameFromXYZ(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramA,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramB,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramC) {
		this();

		// Set the parameters
		setA(paramA);
		setB(paramB);
		setC(paramC);
	}

	/**
	 * Creates a frame from XYZ module (combines values for X, Y, Z and rotation
	 * into a frame datatype) [deprecated]
	 *
	 * @param a A rotation
	 * @param b B rotation
	 * @param c C rotation
	 */
	public FrameFromXYZ(Double paramA, Double paramB, Double paramC) {
		this(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramA),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramB),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramC));
	}

	/**
	 * Creates a frame from XYZ module (combines values for X, Y, Z and rotation
	 * into a frame datatype) [deprecated]
	 *
	 * @param paramX X position
	 * @param paramY Y position
	 * @param paramZ Z position
	 * @param paramA A rotation
	 * @param paramB B rotation
	 * @param paramC C rotation
	 */
	public FrameFromXYZ(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramX,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramY,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramZ,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramA,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramB,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramC) {
		this();

		// Set the parameters
		setX(paramX);
		setY(paramY);
		setZ(paramZ);
		setA(paramA);
		setB(paramB);
		setC(paramC);
	}

	/**
	 * Creates a frame from XYZ module (combines values for X, Y, Z and rotation
	 * into a frame datatype) [deprecated]
	 *
	 * @param paramX X position
	 * @param paramY Y position
	 * @param paramZ Z position
	 * @param paramA A rotation
	 * @param paramB B rotation
	 * @param paramC C rotation
	 */
	public FrameFromXYZ(Double paramX, Double paramY, Double paramZ, Double paramA, Double paramB, Double paramC) {
		this(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramX),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramY),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramZ),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramA),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramB),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramC));
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
	 * Rotation
	 *
	 * @return the input port of the block
	 */
	public final InPort getInRot() {
		return this.inRot;
	}

	/**
	 * X position
	 *
	 * @return the input port of the block
	 */
	public final InPort getInX() {
		return this.inX;
	}

	/**
	 * Y position
	 *
	 * @return the input port of the block
	 */
	public final InPort getInY() {
		return this.inY;
	}

	/**
	 * Z position
	 *
	 * @return the input port of the block
	 */
	public final InPort getInZ() {
		return this.inZ;
	}

	/**
	 * Combined frame
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

	/**
	 * X position
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getX() {
		return this.paramX;
	}

	/**
	 * Sets a parameter of the block: X position
	 * 
	 * @param value new value of the parameter
	 */
	public final void setX(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramX.setValue(value);
	}

	/**
	 * Sets a parameter of the block: X position
	 * 
	 * @param value new value of the parameter
	 */
	public final void setX(Double value) {
		this.setX(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

	/**
	 * Y position
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getY() {
		return this.paramY;
	}

	/**
	 * Sets a parameter of the block: Y position
	 * 
	 * @param value new value of the parameter
	 */
	public final void setY(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramY.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Y position
	 * 
	 * @param value new value of the parameter
	 */
	public final void setY(Double value) {
		this.setY(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

	/**
	 * Z position
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getZ() {
		return this.paramZ;
	}

	/**
	 * Sets a parameter of the block: Z position
	 * 
	 * @param value new value of the parameter
	 */
	public final void setZ(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramZ.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Z position
	 * 
	 * @param value new value of the parameter
	 */
	public final void setZ(Double value) {
		this.setZ(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
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
