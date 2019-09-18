package org.roboticsapi.facet.runtime.rpi.world.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIbool;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIstring;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIFrame;

/**
 * Communication module to read value from net
 */
public class FrameNetcommOut extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::FrameNetcommOut";

	/**  */
	private final InPort inValue = new InPort("inValue");

	/** Key (unique name) of the property */
	private final Parameter<RPIstring> paramKey = new Parameter<RPIstring>("Key", new RPIstring(""));

	/** Report value to application (set to false for pure InterNetcomm) */
	private final Parameter<RPIbool> paramReport = new Parameter<RPIbool>("Report", new RPIbool("true"));

	/** Initial value to read from net */
	private final Parameter<RPIFrame> paramValue = new Parameter<RPIFrame>("Value",
			new RPIFrame("{pos:{x:0,y:0,z:0},rot:{a:0,b:-0,c:0}}"));

	public FrameNetcommOut() {
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
	public FrameNetcommOut(RPIstring paramKey, RPIbool paramReport) {
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
	public FrameNetcommOut(String paramKey, Boolean paramReport) {
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
	public FrameNetcommOut(RPIstring paramKey, RPIbool paramReport, RPIFrame paramValue) {
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
	public FrameNetcommOut(String paramKey, Boolean paramReport, String paramValue) {
		this(new RPIstring(paramKey), new RPIbool(paramReport), new RPIFrame(paramValue));
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
	public final Parameter<RPIFrame> getValue() {
		return this.paramValue;
	}

	/**
	 * Sets a parameter of the block: Initial value to read from net
	 *
	 * @param value new value of the parameter
	 */
	public final void setValue(RPIFrame value) {
		this.paramValue.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Initial value to read from net
	 *
	 * @param value new value of the parameter
	 */
	public final void setValue(String value) {
		this.setValue(new RPIFrame(value));
	}

}
