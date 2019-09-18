package org.roboticsapi.facet.runtime.rpi.world.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIbool;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIstring;
import org.roboticsapi.facet.runtime.rpi.world.types.RPITwist;

/**
 * Communication module to read value from net
 */
public class TwistNetcommOut extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::TwistNetcommOut";

	/**  */
	private final InPort inValue = new InPort("inValue");

	/** Key (unique name) of the property */
	private final Parameter<RPIstring> paramKey = new Parameter<RPIstring>("Key", new RPIstring(""));

	/** Report value to application (set to false for pure InterNetcomm) */
	private final Parameter<RPIbool> paramReport = new Parameter<RPIbool>("Report", new RPIbool("true"));

	/** Initial value to read from net */
	private final Parameter<RPITwist> paramValue = new Parameter<RPITwist>("Value",
			new RPITwist("{vel:{x:0,y:0,z:0},rot:{x:0,y:0,z:0}}"));

	public TwistNetcommOut() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inValue);

		// Add all parameters
		add(paramKey);
		add(paramReport);
		add(paramValue);
	}

	/**
	 * Creates communication module to read value from net
	 *
	 * @param key    Key (unique name) of the property
	 * @param report Report value to application (set to false for pure
	 *               InterNetcomm)
	 */
	public TwistNetcommOut(RPIstring paramKey, RPIbool paramReport) {
		this();

		// Set the parameters
		setKey(paramKey);
		setReport(paramReport);
	}

	/**
	 * Creates communication module to read value from net
	 *
	 * @param key    Key (unique name) of the property
	 * @param report Report value to application (set to false for pure
	 *               InterNetcomm)
	 */
	public TwistNetcommOut(String paramKey, Boolean paramReport) {
		this(new RPIstring(paramKey), new RPIbool(paramReport));
	}

	/**
	 * Creates communication module to read value from net
	 *
	 * @param paramKey    Key (unique name) of the property
	 * @param paramReport Report value to application (set to false for pure
	 *                    InterNetcomm)
	 * @param paramValue  Initial value to read from net
	 */
	public TwistNetcommOut(RPIstring paramKey, RPIbool paramReport, RPITwist paramValue) {
		this();

		// Set the parameters
		setKey(paramKey);
		setReport(paramReport);
		setValue(paramValue);
	}

	/**
	 * Creates communication module to read value from net
	 *
	 * @param paramKey    Key (unique name) of the property
	 * @param paramReport Report value to application (set to false for pure
	 *                    InterNetcomm)
	 * @param paramValue  Initial value to read from net
	 */
	public TwistNetcommOut(String paramKey, Boolean paramReport, String paramValue) {
		this(new RPIstring(paramKey), new RPIbool(paramReport), new RPITwist(paramValue));
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
	public final Parameter<RPIstring> getKey() {
		return this.paramKey;
	}

	/**
	 * Sets a parameter of the block: Key (unique name) of the property
	 *
	 * @param value new value of the parameter
	 */
	public final void setKey(RPIstring value) {
		this.paramKey.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Key (unique name) of the property
	 *
	 * @param value new value of the parameter
	 */
	public final void setKey(String value) {
		this.setKey(new RPIstring(value));
	}

	/**
	 * Report value to application (set to false for pure InterNetcomm)
	 *
	 * @return the parameter of the block
	 */
	public final Parameter<RPIbool> getReport() {
		return this.paramReport;
	}

	/**
	 * Sets a parameter of the block: Report value to application (set to false for
	 * pure InterNetcomm)
	 *
	 * @param value new value of the parameter
	 */
	public final void setReport(RPIbool value) {
		this.paramReport.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Report value to application (set to false for
	 * pure InterNetcomm)
	 *
	 * @param value new value of the parameter
	 */
	public final void setReport(Boolean value) {
		this.setReport(new RPIbool(value));
	}

	/**
	 * Initial value to read from net
	 *
	 * @return the parameter of the block
	 */
	public final Parameter<RPITwist> getValue() {
		return this.paramValue;
	}

	/**
	 * Sets a parameter of the block: Initial value to read from net
	 *
	 * @param value new value of the parameter
	 */
	public final void setValue(RPITwist value) {
		this.paramValue.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Initial value to read from net
	 *
	 * @param value new value of the parameter
	 */
	public final void setValue(String value) {
		this.setValue(new RPITwist(value));
	}

}
