package org.roboticsapi.framework.gnss.runtime.rpi.primitives;

import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIstring;

/**
 * Monitor for Global Navigation Satellite System
 */
public class SatelliteMonitor extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "GNSS::SatelliteMonitor";

	/** Longitude [rad] */
	private final OutPort outLongitude = new OutPort("outLongitude");

	/** Latitude [rad] */
	private final OutPort outLatitude = new OutPort("outLatitude");

	/** Altitude [m] */
	private final OutPort outAlitutde = new OutPort("outAlitutde");

	/** Horizontal Dilution of Precision [m] */
	private final OutPort outHDOP = new OutPort("outHDOP");

	/** Vertical Dilution of Precision [m] */
	private final OutPort outVDOP = new OutPort("outVDOP");

	/** Number of satellites visible */
	private final OutPort outNumSatellites = new OutPort("outNumSatellites");

	/** Device name */
	private final Parameter<RPIstring> paramDevice = new Parameter<RPIstring>("Device", new RPIstring(""));

	public SatelliteMonitor() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(outLongitude);
		add(outLatitude);
		add(outAlitutde);
		add(outHDOP);
		add(outVDOP);
		add(outNumSatellites);

		// Add all parameters
		add(paramDevice);
	}

	/**
	 * Creates monitor for Global Navigation Satellite System
	 *
	 * @param device Device name
	 */
	public SatelliteMonitor(RPIstring paramDevice) {
		this();

		// Set the parameters
		setDevice(paramDevice);
	}

	/**
	 * Creates monitor for Global Navigation Satellite System
	 *
	 * @param device Device name
	 */
	public SatelliteMonitor(String paramDevice) {
		this(new RPIstring(paramDevice));
	}

	/**
	 * Longitude [rad]
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutLongitude() {
		return this.outLongitude;
	}

	/**
	 * Latitude [rad]
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutLatitude() {
		return this.outLatitude;
	}

	/**
	 * Altitude [m]
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutAlitutde() {
		return this.outAlitutde;
	}

	/**
	 * Horizontal Dilution of Precision [no unit]
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutHDOP() {
		return this.outHDOP;
	}

	/**
	 * Vertical Dilution of Precision [no unit]
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutVDOP() {
		return this.outVDOP;
	}

	/**
	 * Number of satellites visible
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutNumSatellites() {
		return this.outNumSatellites;
	}

	/**
	 * Device name
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<RPIstring> getDevice() {
		return this.paramDevice;
	}

	/**
	 * Sets a parameter of the block: Device name
	 * 
	 * @param value new value of the parameter
	 */
	public final void setDevice(RPIstring value) {
		this.paramDevice.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Device name
	 * 
	 * @param value new value of the parameter
	 */
	public final void setDevice(String value) {
		this.setDevice(new RPIstring(value));
	}

}
