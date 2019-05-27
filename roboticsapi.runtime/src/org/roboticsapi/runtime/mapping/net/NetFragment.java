/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.net;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.roboticsapi.runtime.mapping.LinkBuilder;
import org.roboticsapi.runtime.mapping.LinkBuilderResult;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.rpi.Fragment;
import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Primitive;
import org.roboticsapi.runtime.rpi.RPIException;

/**
 * A net fragment used for mapping
 */
public class NetFragment {

	// Former NetFragment
	private final String name;

	private NetFragment parentFragment;

	public NetFragment getParentFragment() {
		return parentFragment;
	}

	void setParentFragment(NetFragment parentFragment) {
		this.parentFragment = parentFragment;
	}

	public NetFragment(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	// public abstract void addLinkBuilder(LinkBuilder builder);
	//
	// public abstract List<LinkBuilder> getLinkBuilders();
	//
	// public abstract List<DataflowInPort> getInPorts();
	//
	// public abstract List<DataflowOutPort> getOutPorts();
	//
	// public abstract List<DataflowInPort> getFreeInPorts();
	//
	// public abstract List<DataflowOutPort> getFreeOutPorts();
	//
	// public abstract boolean buildLinks(List<LinkBuilder> builders) throws
	// MappingException;
	//
	// public abstract void addToNet(Net net) throws MappingException;

	/**
	 * Retrieves compatible in ports for a given datatflow type
	 *
	 * @param source type to search compatible in ports for
	 * @return in ports compatible to the given type
	 */
	public List<DataflowInPort> getCompatibleInPorts(final DataflowType source) {
		final List<DataflowInPort> ports = getFreeInPorts();
		final List<DataflowInPort> ret = new ArrayList<DataflowInPort>();
		for (final DataflowInPort port : ports) {
			if (source.providesValueFor(port.getType())) {
				ret.add(port);
			}
		}
		return ret;
	}

	/**
	 * Retrieves compatible out ports for a given data flow type
	 *
	 * @param dest type to search compatible out ports for
	 * @return out ports compatible to the given type
	 */
	public List<DataflowOutPort> getCompatibleOutPorts(final DataflowType dest) {
		final List<DataflowOutPort> ports = getOutPorts();
		final List<DataflowOutPort> ret = new ArrayList<DataflowOutPort>();
		for (final DataflowOutPort port : ports) {
			if (port.getType().providesValueFor(dest)) {
				ret.add(port);
			}
		}
		return ret;
	}

	/**
	 * Resolves smart links in the net
	 *
	 * @return true if all links were resolved
	 * @throws MappingException
	 */
	public boolean buildLinks() throws MappingException {
		return buildLinks(getLinkBuilders());
	}

	@Override
	public String toString() {
		return name + (getParentFragment() != null ? " < " + getParentFragment() : "");
	}

	// Former SimpleNetFragment

	/** primitives collected in this net fragment */
	protected List<Primitive> primitives = new ArrayList<Primitive>();

	/** in ports of this net fragment */
	protected List<DataflowInPort> inPorts = new ArrayList<DataflowInPort>();

	/** out ports of this net fragment */
	protected List<DataflowOutPort> outPorts = new ArrayList<DataflowOutPort>();

	protected List<LinkBuilder> linkBuilders = new ArrayList<LinkBuilder>();

	public List<Primitive> getPrimitives() {
		return primitives;
	}

	/**
	 * Adds the net fragment to an RPI net
	 *
	 * @param fragment RPI net to add fragment to
	 * @throws MappingException
	 */
	public void addToFragment(final Fragment fragment) throws MappingException {

		if (links.size() > 0) {
			throw new MappingException("Unresolvable link", links.get(0).getIncompatibleTypeException());
		}

		for (final Primitive primitive : primitives) {
			fragment.add(primitive);
		}

		for (final NetFragment nf : fragments) {
			nf.addToFragment(fragment);
		}

		for (final DataflowInPort port : inPorts) {
			port.connectInNet();
		}
	}

	/**
	 * Retrieves the input ports
	 *
	 * @return input ports of this net fragment
	 */
	public List<DataflowInPort> getInPorts() {
		final List<DataflowInPort> ports = new ArrayList<DataflowInPort>(inPorts);
		for (final NetFragment fragment : fragments) {
			ports.addAll(fragment.getInPorts());
		}
		ports.removeAll(hiddenInPorts);
		return ports;
	}

	/**
	 * Retrieves the output ports
	 *
	 * @return output ports of this net fragment
	 */
	public List<DataflowOutPort> getOutPorts() {
		final List<DataflowOutPort> ports = new ArrayList<DataflowOutPort>(wrappedOutPorts);
		ports.addAll(outPorts);
		for (final NetFragment fragment : fragments) {
			ports.addAll(fragment.getOutPorts());
		}
		ports.removeAll(hiddenOutPorts);
		return ports;
	}

	/**
	 * Adds an RPI primitive to the net fragment
	 *
	 * @param primitive primitive to add
	 */
	public <T extends Primitive> T addPrimitive(final T primitive) {
		return add(primitive);
	}

	/**
	 * Adds an in port to the net fragment
	 *
	 * @param inPort in port to add
	 */
	public <T extends DataflowInPort> T addInPort(final T inPort) {
		inPorts.add(inPort);
		inPort.setParentFragment(this);

		return inPort;
	}

	/**
	 * Adds an out port to the net fragment
	 *
	 * @param outPort out port to add
	 */
	public <T extends DataflowOutPort> T addOutPort(final T outPort) {
		outPorts.add(outPort);
		outPort.setParentFragment(this);

		return outPort;
	}

	/**
	 * Adds a new out port to this net fragment
	 *
	 * @param type data flow type
	 * @param outs list of corresponding RPI ports
	 * @return the created out port
	 */
	public DataflowOutPort addOutPort(final DataflowType type, final boolean required, final List<OutPort> outs) {
		return addOutPort(type, required, outs.toArray(new OutPort[outs.size()]));
	}

	/**
	 * Adds a new out port to this net fragment
	 *
	 * @param type data flow type
	 * @param outs list of corresponding RPI ports
	 * @return the created out port
	 */
	public DataflowOutPort addOutPort(final DataflowType type, final boolean required, final OutPort... outs) {
		final DataflowOutPort ret = new DataflowOutPort(required, type, outs);
		outPorts.add(ret);
		ret.setParentFragment(this);
		return ret;
	}

	/**
	 * Adds a new in port to this net fragment
	 *
	 * @param type     data flow type
	 * @param required data flow is required for operation of the primitive
	 * @param ins      list of corresponding RPI ports
	 * @return the created in port
	 */
	public DataflowInPort addInPort(final DataflowType type, final boolean required, final InPort... ins) {
		final DataflowInPort ret = new DataflowInPort(required, type, ins);
		inPorts.add(ret);
		ret.setParentFragment(this);
		return ret;
	}

	public DataflowThroughOutPort addThroughPort(final boolean required, final DataflowType type) {
		final DataflowThroughInPort in = new DataflowThroughInPort(required, type);
		addInPort(in);
		in.setParentFragment(this);
		final DataflowThroughOutPort out = new DataflowThroughOutPort(required, in);
		addOutPort(out);
		out.setParentFragment(this);
		return out;
	}

	/**
	 * Retrieves the link builders available in the net fragment
	 *
	 * @return list of link builders
	 */
	public List<LinkBuilder> getLinkBuilders() {
		final ArrayList<LinkBuilder> ret = new ArrayList<LinkBuilder>(linkBuilders);
		for (final NetFragment child : fragments) {
			ret.addAll(child.getLinkBuilders());
		}
		return ret;
	}

	public void addLinkBuilder(final LinkBuilder builder) {
		linkBuilders.add(builder);
	}

	// ComposedNetFragment

	/** child net fragments */
	private final List<NetFragment> fragments = new ArrayList<NetFragment>();

	private final List<DataflowInPort> hiddenInPorts = new ArrayList<DataflowInPort>();
	private final List<DataflowOutPort> hiddenOutPorts = new ArrayList<DataflowOutPort>();

	private final List<DataflowOutPort> wrappedOutPorts = new ArrayList<DataflowOutPort>();

	/** list of smart links */
	private final List<DataflowLink> links = new Vector<DataflowLink>();

	/**
	 * Adds a new net fragment
	 *
	 * @param fragment net fragment to add
	 */
	public <T extends NetFragment> T add(final T fragment) {
		if (fragments.contains(fragment)) {
			return null;
		}
		fragments.add(fragment);
		fragment.setParentFragment(this);

		return fragment;
	}

	public <T extends Primitive> T add(final T primitive) {
		primitives.add(primitive);
		return primitive;
	}

	public DataflowInPort reinterpret(DataflowInPort port, DataflowType type) throws MappingException {

		NetFragment reinterpreter = new NetFragment("Reinterpret");

		add(reinterpreter);

		DataflowThroughInPort inPort = reinterpreter.addInPort(new DataflowThroughInPort(type));

		DataflowThroughOutPort outPort = reinterpreter
				.addOutPort(new DataflowThroughOutPort(true, inPort, port.getType()));

		port.connectTo(outPort);

		return inPort;
	}

	public DataflowOutPort reinterpret(DataflowOutPort port, DataflowType type) throws MappingException {

		NetFragment reinterpreter = new NetFragment("Reinterpret");

		add(reinterpreter);

		DataflowThroughInPort inPort = reinterpreter.addInPort(new DataflowThroughInPort(port.getType()));

		DataflowThroughOutPort outPort = reinterpreter.addOutPort(new DataflowThroughOutPort(true, inPort, type));

		inPort.connectTo(port);

		return outPort;
	}

	public List<NetFragment> getFragments() {
		return fragments;
	}

	public void hidePort(final DataflowInPort port) {
		hiddenInPorts.add(port);
	}

	public void hidePort(final DataflowOutPort port) {
		hiddenOutPorts.add(port);
	}

	public void hideAllInPorts(final List<DataflowInPort> ports) {
		hiddenInPorts.addAll(ports);
	}

	public void hideAllOutPorts(final List<DataflowOutPort> ports) {
		hiddenOutPorts.addAll(ports);
	}

	/**
	 * Retrieves the input ports not yet connected to an output
	 *
	 * @return free input ports
	 */
	public List<DataflowInPort> getFreeInPorts() {
		final List<DataflowInPort> ports = new ArrayList<DataflowInPort>();
		for (DataflowInPort port : inPorts) {
			if (!port.isConnected()) {
				ports.add(port);
			}
		}

		for (final NetFragment fragment : fragments) {
			for (final DataflowInPort port : fragment.getFreeInPorts()) {
				ports.add(port);
			}
		}

		for (final DataflowLink link : links) {
			ports.remove(link.getTo());
		}
		return ports;
	}

	public DataflowOutPort addConverterLink(final DataflowOutPort port, final DataflowType type)
			throws MappingException {
		final NetFragment converter = new NetFragment(port.getType() + " -> " + type);
		final DataflowThroughInPort in = new DataflowThroughInPort(type);
		final DataflowThroughOutPort out = new DataflowThroughOutPort(true, in);

		converter.addInPort(in);
		converter.addOutPort(out);
		connect(port, in);
		add(converter);
		return out;
	}

	public DataflowInPort addConverterLink(final DataflowInPort port, final DataflowType type) throws MappingException {
		final NetFragment converter = new NetFragment(type + " -> " + port.getType());
		final DataflowThroughInPort in = new DataflowThroughInPort(type);
		final DataflowThroughOutPort out = new DataflowThroughOutPort(true, in);
		converter.addInPort(in);
		converter.addOutPort(out);
		connect(out, port);
		add(converter);
		return in;
	}

	/**
	 * Connects two data flow ports
	 *
	 * @param from source data flow port
	 * @param to   destination data flow port
	 * @throws MappingException
	 */
	public void connect(final DataflowOutPort from, final DataflowInPort to) throws MappingException {
		// if the type isn't compatible, add a smart link
		if (from == null || to == null || !from.getType().providesValueFor(to.getType())) {
			links.add(new DataflowLink(from, to));
		} else {
			to.connectTo(from);
		}
	}

	public void connect(final DataflowOutPort from, final InPort to, final DataflowType type) throws MappingException {
		connect(from, addInPort(type, false, to));
	}

	public void connect(final OutPort from, final DataflowType type, final DataflowInPort to) throws MappingException {
		connect(addOutPort(type, false, from), to);
	}

	public void connect(final OutPort from, final InPort to) throws MappingException {
		try {
			to.connectTo(from);
		} catch (RPIException e) {
			throw new MappingException(e);
		}
	}

	public boolean buildLinks(final List<LinkBuilder> builders) throws MappingException {

		// try to resolve each link
		boolean ret = true;
		for (int i = 0; i < links.size(); i++) {
			final DataflowLink link = links.get(i);

			if (link.getTo().isConnected()) {
				// this link is already connected
				links.remove(i--);
				continue;
			}

			boolean found = false;
			// try each link builder to create a linking net
			for (final LinkBuilder builder : builders) {
				final DataflowType fromType = link.getFrom() == null ? null : link.getFrom().getType(),
						toType = link.getTo() == null ? null : link.getTo().getType();

				final LinkBuilderResult linkNet = builder.buildLink(fromType, toType);

				if (linkNet != null) {
					for (final LinkBuilder b : builders) {
						linkNet.getNetFragment().addLinkBuilder(b);
					}
					if (linkNet.getNetFragment().buildLinks()) {
						// add the created net
						add(linkNet.getNetFragment());
						// link inputs and outputs
						if (linkNet.getInputPort() != null) {
							connect(link.getFrom(), linkNet.getInputPort());
						}
						if (linkNet.getResultPort() != null) {
							connect(linkNet.getResultPort(), link.getTo());
						}

						// this link has been resolved
						links.remove(i--);
						found = true;
						break;
					}
				}
			}

			ret &= found;
		}

		// propagate to child net fragments
		for (final NetFragment fragment : fragments) {
			ret &= fragment.buildLinks(builders);
		}
		return ret;
	}

}
