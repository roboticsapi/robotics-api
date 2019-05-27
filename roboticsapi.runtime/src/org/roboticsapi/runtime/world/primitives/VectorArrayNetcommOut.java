package org.roboticsapi.runtime.world.primitives;

import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.Parameter;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * Communication module to read value from net
 */
public class VectorArrayNetcommOut extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::VectorArrayNetcommOut";

	/**  */
	private final InPort inValue = new InPort("inValue");

	/** Key (unique name) of the property */
	private final Parameter<org.roboticsapi.runtime.core.types.RPIstring> paramKey = new Parameter<org.roboticsapi.runtime.core.types.RPIstring>(
			"Key", new org.roboticsapi.runtime.core.types.RPIstring(""));

	/** Initial value to read from net */
	private final Parameter<org.roboticsapi.runtime.world.types.RPIVectorArray> paramValue = new Parameter<org.roboticsapi.runtime.world.types.RPIVectorArray>(
			"Value", new org.roboticsapi.runtime.world.types.RPIVectorArray("[]"));

	public VectorArrayNetcommOut() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inValue);

		// Add all parameters
		add(paramKey);
		add(paramValue);
	}

	/**
	 * Creates communication module to read value from net
	 *
	 * @param key Key (unique name) of the property
	 */
	public VectorArrayNetcommOut(org.roboticsapi.runtime.core.types.RPIstring paramKey) {
		this();

		// Set the parameters
		setKey(paramKey);
	}

	/**
	 * Creates communication module to read value from net
	 *
	 * @param key Key (unique name) of the property
	 */
	public VectorArrayNetcommOut(String paramKey) {
		this(new org.roboticsapi.runtime.core.types.RPIstring(paramKey));
	}

	/**
	 * Creates communication module to read value from net
	 *
	 * @param paramKey   Key (unique name) of the property
	 * @param paramValue Initial value to read from net
	 */
	public VectorArrayNetcommOut(org.roboticsapi.runtime.core.types.RPIstring paramKey,
			org.roboticsapi.runtime.world.types.RPIVectorArray paramValue) {
		this();

		// Set the parameters
		setKey(paramKey);
		setValue(paramValue);
	}

	/**
	 * Creates communication module to read value from net
	 *
	 * @param paramKey   Key (unique name) of the property
	 * @param paramValue Initial value to read from net
	 */
	public VectorArrayNetcommOut(String paramKey, String paramValue) {
		this(new org.roboticsapi.runtime.core.types.RPIstring(paramKey),
				new org.roboticsapi.runtime.world.types.RPIVectorArray(paramValue));
	}

	/**
	 * 
	 *
	 * @return the input port of the block
	 */
	public final InPort getInValue() {
		return this.inValue;
	}

	/**
	 * Key (unique name) of the property
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.core.types.RPIstring> getKey() {
		return this.paramKey;
	}

	/**
	 * Sets a parameter of the block: Key (unique name) of the property
	 * 
	 * @param value new value of the parameter
	 */
	public final void setKey(org.roboticsapi.runtime.core.types.RPIstring value) {
		this.paramKey.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Key (unique name) of the property
	 * 
	 * @param value new value of the parameter
	 */
	public final void setKey(String value) {
		this.setKey(new org.roboticsapi.runtime.core.types.RPIstring(value));
	}

	/**
	 * Initial value to read from net
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.world.types.RPIVectorArray> getValue() {
		return this.paramValue;
	}

	/**
	 * Sets a parameter of the block: Initial value to read from net
	 * 
	 * @param value new value of the parameter
	 */
	public final void setValue(org.roboticsapi.runtime.world.types.RPIVectorArray value) {
		this.paramValue.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Initial value to read from net
	 * 
	 * @param value new value of the parameter
	 */
	public final void setValue(String value) {
		this.setValue(new org.roboticsapi.runtime.world.types.RPIVectorArray(value));
	}

}
