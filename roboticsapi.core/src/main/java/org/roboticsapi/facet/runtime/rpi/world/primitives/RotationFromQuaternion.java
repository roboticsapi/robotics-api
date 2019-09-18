package org.roboticsapi.facet.runtime.rpi.world.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * A rotation combiner module (creates a rotation data flow from a quaternion)
 */
public class RotationFromQuaternion extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::RotationFromQuaternion";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** Quaternion W value */
	private final InPort inW = new InPort("inW");

	/** Quaternion X value */
	private final InPort inX = new InPort("inX");

	/** Quaternion Y value */
	private final InPort inY = new InPort("inY");

	/** Quaternion Z value */
	private final InPort inZ = new InPort("inZ");

	/** Combined rotation */
	private final OutPort outValue = new OutPort("outValue");

	/** Quaternion X value */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramX = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"X", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("1"));

	/** Quaternion Y value */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramY = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"Y", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("0"));

	/** Quaternion Z value */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramZ = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"Z", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("0"));

	/** Quaternion W value */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramW = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"W", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("0"));

	public RotationFromQuaternion() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inW);
		add(inX);
		add(inY);
		add(inZ);
		add(outValue);

		// Add all parameters
		add(paramX);
		add(paramY);
		add(paramZ);
		add(paramW);
	}

	/**
	 * Creates a rotation combiner module (creates a rotation data flow from a
	 * quaternion)
	 *
	 * @param paramX Quaternion X value
	 * @param paramY Quaternion Y value
	 * @param paramZ Quaternion Z value
	 * @param paramW Quaternion W value
	 */
	public RotationFromQuaternion(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramX,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramY,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramZ,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramW) {
		this();

		// Set the parameters
		setX(paramX);
		setY(paramY);
		setZ(paramZ);
		setW(paramW);
	}

	/**
	 * Creates a rotation combiner module (creates a rotation data flow from a
	 * quaternion)
	 *
	 * @param paramX Quaternion X value
	 * @param paramY Quaternion Y value
	 * @param paramZ Quaternion Z value
	 * @param paramW Quaternion W value
	 */
	public RotationFromQuaternion(Double paramX, Double paramY, Double paramZ, Double paramW) {
		this(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramX),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramY),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramZ),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramW));
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
	 * Quaternion W value
	 *
	 * @return the input port of the block
	 */
	public final InPort getInW() {
		return this.inW;
	}

	/**
	 * Quaternion X value
	 *
	 * @return the input port of the block
	 */
	public final InPort getInX() {
		return this.inX;
	}

	/**
	 * Quaternion Y value
	 *
	 * @return the input port of the block
	 */
	public final InPort getInY() {
		return this.inY;
	}

	/**
	 * Quaternion Z value
	 *
	 * @return the input port of the block
	 */
	public final InPort getInZ() {
		return this.inZ;
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
	 * Quaternion X value
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getX() {
		return this.paramX;
	}

	/**
	 * Sets a parameter of the block: Quaternion X value
	 * 
	 * @param value new value of the parameter
	 */
	public final void setX(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramX.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Quaternion X value
	 * 
	 * @param value new value of the parameter
	 */
	public final void setX(Double value) {
		this.setX(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

	/**
	 * Quaternion Y value
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getY() {
		return this.paramY;
	}

	/**
	 * Sets a parameter of the block: Quaternion Y value
	 * 
	 * @param value new value of the parameter
	 */
	public final void setY(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramY.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Quaternion Y value
	 * 
	 * @param value new value of the parameter
	 */
	public final void setY(Double value) {
		this.setY(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

	/**
	 * Quaternion Z value
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getZ() {
		return this.paramZ;
	}

	/**
	 * Sets a parameter of the block: Quaternion Z value
	 * 
	 * @param value new value of the parameter
	 */
	public final void setZ(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramZ.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Quaternion Z value
	 * 
	 * @param value new value of the parameter
	 */
	public final void setZ(Double value) {
		this.setZ(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

	/**
	 * Quaternion W value
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getW() {
		return this.paramW;
	}

	/**
	 * Sets a parameter of the block: Quaternion W value
	 * 
	 * @param value new value of the parameter
	 */
	public final void setW(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramW.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Quaternion W value
	 * 
	 * @param value new value of the parameter
	 */
	public final void setW(Double value) {
		this.setW(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

}
