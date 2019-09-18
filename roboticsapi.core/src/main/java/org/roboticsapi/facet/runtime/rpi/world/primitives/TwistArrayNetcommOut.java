package org.roboticsapi.facet.runtime.rpi.world.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * Communication module to read value from net
 */
public class TwistArrayNetcommOut extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::TwistArrayNetcommOut";

	/**  */
	private final InPort inValue = new InPort("inValue");

	/** Key (unique name) of the property */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIstring> paramKey = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIstring>(
			"Key", new org.roboticsapi.facet.runtime.rpi.core.types.RPIstring(""));

	/** Initial value to read from net */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.world.types.RPITwistArray> paramValue = new Parameter<org.roboticsapi.facet.runtime.rpi.world.types.RPITwistArray>(
			"Value", new org.roboticsapi.facet.runtime.rpi.world.types.RPITwistArray("[]"));

	public TwistArrayNetcommOut() {
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
	public TwistArrayNetcommOut(org.roboticsapi.facet.runtime.rpi.core.types.RPIstring paramKey) {
		this();

		// Set the parameters
		setKey(paramKey);
	}

	/**
	 * Creates communication module to read value from net
	 *
	 * @param key Key (unique name) of the property
	 */
	public TwistArrayNetcommOut(String paramKey) {
		this(new org.roboticsapi.facet.runtime.rpi.core.types.RPIstring(paramKey));
	}

	/**
	 * Creates communication module to read value from net
	 *
	 * @param paramKey   Key (unique name) of the property
	 * @param paramValue Initial value to read from net
	 */
	public TwistArrayNetcommOut(org.roboticsapi.facet.runtime.rpi.core.types.RPIstring paramKey,
			org.roboticsapi.facet.runtime.rpi.world.types.RPITwistArray paramValue) {
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
	public TwistArrayNetcommOut(String paramKey, String paramValue) {
		this(new org.roboticsapi.facet.runtime.rpi.core.types.RPIstring(paramKey),
				new org.roboticsapi.facet.runtime.rpi.world.types.RPITwistArray(paramValue));
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
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIstring> getKey() {
		return this.paramKey;
	}

	/**
	 * Sets a parameter of the block: Key (unique name) of the property
	 * 
	 * @param value new value of the parameter
	 */
	public final void setKey(org.roboticsapi.facet.runtime.rpi.core.types.RPIstring value) {
		this.paramKey.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Key (unique name) of the property
	 * 
	 * @param value new value of the parameter
	 */
	public final void setKey(String value) {
		this.setKey(new org.roboticsapi.facet.runtime.rpi.core.types.RPIstring(value));
	}

	/**
	 * Initial value to read from net
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.world.types.RPITwistArray> getValue() {
		return this.paramValue;
	}

	/**
	 * Sets a parameter of the block: Initial value to read from net
	 * 
	 * @param value new value of the parameter
	 */
	public final void setValue(org.roboticsapi.facet.runtime.rpi.world.types.RPITwistArray value) {
		this.paramValue.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Initial value to read from net
	 * 
	 * @param value new value of the parameter
	 */
	public final void setValue(String value) {
		this.setValue(new org.roboticsapi.facet.runtime.rpi.world.types.RPITwistArray(value));
	}

}
