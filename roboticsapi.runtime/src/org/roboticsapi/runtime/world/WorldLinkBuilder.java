/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.runtime.mapping.AbstractMapperRuntime;
import org.roboticsapi.runtime.mapping.LinkBuilder;
import org.roboticsapi.runtime.mapping.LinkBuilderResult;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowInPort;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DataflowThroughOutPort;
import org.roboticsapi.runtime.mapping.net.DataflowType;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.world.dataflow.RelationDataflow;
import org.roboticsapi.runtime.world.dataflow.VelocityDataflow;
import org.roboticsapi.runtime.world.fragment.AdaptVelocityOrientationFragment;
import org.roboticsapi.runtime.world.fragment.AdaptVelocityPivotPointFragment;
import org.roboticsapi.runtime.world.fragment.AddVelocitiesFragment;
import org.roboticsapi.runtime.world.fragment.InvertVelocityFragment;
import org.roboticsapi.runtime.world.primitives.FrameAddTwist;
import org.roboticsapi.runtime.world.primitives.FrameFromPosRot;
import org.roboticsapi.runtime.world.primitives.FrameInvert;
import org.roboticsapi.runtime.world.primitives.FrameIsNull;
import org.roboticsapi.runtime.world.primitives.FramePre;
import org.roboticsapi.runtime.world.primitives.FrameTransform;
import org.roboticsapi.runtime.world.primitives.TwistFromFrames;
import org.roboticsapi.runtime.world.primitives.TwistSetNull;
import org.roboticsapi.world.DynamicConnection;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Placement;
import org.roboticsapi.world.Relation;
import org.roboticsapi.world.StaticConnection;
import org.roboticsapi.world.TemporaryRelation;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.Twist;
import org.roboticsapi.world.Vector;
import org.roboticsapi.world.sensor.RelationSensor;
import org.roboticsapi.world.sensor.VelocitySensor;

public class WorldLinkBuilder implements LinkBuilder {

	private final AbstractMapperRuntime runtime;

	public WorldLinkBuilder(AbstractMapperRuntime runtime) {
		this.runtime = runtime;
	}

	@Override
	public LinkBuilderResult buildLink(final DataflowType from, final DataflowType to) throws MappingException {

		if (from == null && to instanceof RelationDataflow) {
			final RelationDataflow t = (RelationDataflow) to;

			// System.out.println("World link builder is building null -> " +
			// t);

			final NetFragment ret = new NetFragment("Transformation");
			final NetFragment compose = new NetFragment("Compose");
			ret.add(compose);

			if (t.getFrom() == t.getTo()) {
				final FrameFromPosRot fc = new FrameFromPosRot(0d, 0d, 0d, 0d, 0d, 0d);
				compose.add(fc);
				DataflowOutPort result = compose.addOutPort(new RelationDataflow(t.getFrom(), t.getTo()), true,
						fc.getOutValue());
				return new LinkBuilderResult(compose, null, result);
			}

			List<Relation> rels = t.getFrom().getRelationsTo(t.getTo());
			if (rels == null) {
				System.out.println("[WorldLinkBuilder] Link building didn't work: null -> " + t);

				return null;
			}

			List<DataflowOutPort> transformations = new ArrayList<DataflowOutPort>();
			for (int i = 0; i < rels.size(); i++) {
				LinkBuilderResult frameFragment = addFrameFragment(rels.get(i));
				transformations.add(frameFragment.getResultPort());
				ret.add(frameFragment.getNetFragment());
			}

			DataflowOutPort last = combineTransformations(ret, transformations, t.getFrom());

			// hide all outports of internal partial transformations to
			// avoid ambiguities
			for (DataflowOutPort p : transformations) {
				if (p != last) {
					ret.hidePort(p);
				}

			}
			return new LinkBuilderResult(ret, null, last);
		}

		// world acts as a link builder for frame dataflows
		if (from instanceof RelationDataflow && to instanceof RelationDataflow) {
			final RelationDataflow f = (RelationDataflow) from, t = (RelationDataflow) to;

			// System.out.println("World link builder is building " + f + " -> "
			// + t);
			final NetFragment ret = new NetFragment("Transformation");
			final NetFragment compose = new NetFragment("Compose");
			ret.add(compose);

			Frame tFrom = t.getFrom();
			Frame fFrom = f.getFrom();
			if (tFrom == null) {
				tFrom = fFrom;
			}
			if (fFrom == null) {
				fFrom = tFrom;
			}
			Frame fTo = f.getTo();
			Frame tTo = t.getTo();
			if (fTo == null) {
				fTo = tTo;
			}
			if (tTo == null) {
				tTo = fTo;
			}
			List<Relation> first = tFrom.getRelationsTo(fFrom);
			List<Relation> second = fTo.getRelationsTo(tTo,
					first == null ? new Relation[0] : first.toArray(new Relation[first.size()]));

			if (first == null || second == null) {
				first = tFrom.getRelationsTo(fTo);
				second = fFrom.getRelationsTo(tTo, first.toArray(new Relation[first.size()]));
			}

			if (first != null && second != null) {
				List<DataflowOutPort> transformations = new ArrayList<DataflowOutPort>();

				for (int i = 0; i < first.size(); i++) {
					LinkBuilderResult frameFragment = addFrameFragment(first.get(i));
					transformations.add(frameFragment.getResultPort());
					ret.add(frameFragment.getNetFragment());
				}
				DataflowThroughOutPort input = compose.addThroughPort(true, from);
				transformations.add(input);
				for (int i = 0; i < second.size(); i++) {
					LinkBuilderResult frameFragment = addFrameFragment(second.get(i));
					transformations.add(frameFragment.getResultPort());
					ret.add(frameFragment.getNetFragment());
				}

				DataflowOutPort last = combineTransformations(ret, transformations, tFrom);

				// hide all outports of internal partial transformations to
				// avoid ambiguities
				for (DataflowOutPort p : transformations) {
					if (p != last) {
						ret.hidePort(p);
					}

				}

				return new LinkBuilderResult(ret, input.getInPort(), last);
			}

			System.out.println("[WorldLinkBuilder] Link building didn't work");

		}

		if (from instanceof RelationDataflow && to instanceof VelocityDataflow) {
			VelocityDataflow toV = (VelocityDataflow) to;

			final NetFragment ret = new NetFragment("Calculate Twist");
			DataflowThroughOutPort inFrom = ret.addThroughPort(true, from);
			RelationDataflow relationType = new RelationDataflow(toV.getReferenceFrame(), toV.getMovingFrame());
			DataflowOutPort vFrom = ret.addConverterLink(inFrom, relationType);

			FramePre pre = ret.add(new FramePre());
			ret.connect(vFrom, pre.getInValue(), relationType);
			FrameIsNull isNull = ret.add(new FrameIsNull());
			ret.connect(pre.getOutValue(), isNull.getInValue());

			// FrameConditional ifNull = ret.add(new FrameConditional());
			// ret.connect(null, ifNull.getInTrue(), relationType);
			// ret.connect(pre.getOutValue(), ifNull.getInFalse());
			// ret.connect(isNull.getOutValue(), ifNull.getInCondition());

			TwistFromFrames twist = ret.add(new TwistFromFrames());
			ret.connect(pre.getOutValue(), twist.getInPrevFrame());
			ret.connect(vFrom, twist.getInFrame(), relationType);

			TwistSetNull twistOrNull = ret.add(new TwistSetNull());
			ret.connect(twist.getOutValue(), twistOrNull.getInValue());
			ret.connect(isNull.getOutValue(), twistOrNull.getInNull());

			DataflowOutPort twistOut = ret
					.addOutPort(
							new VelocityDataflow(toV.getMovingFrame(), toV.getReferenceFrame(),
									toV.getMovingFrame().getPoint(), toV.getReferenceFrame().getOrientation()),
							false, twistOrNull.getOutValue());

			DataflowOutPort result = ret.addConverterLink(twistOut, toV);

			return new LinkBuilderResult(ret, inFrom.getInPort(), result);
		}

		if (from == null && to instanceof VelocityDataflow) {
			VelocityDataflow toV = (VelocityDataflow) to;

			VelocitySensor velSensor;
			velSensor = toV.getReferenceFrame().getVelocitySensorOf(toV.getMovingFrame(), toV.getPivotPoint(),
					toV.getOrientation());
			if (velSensor == null) {
				throw new MappingException("No velocity found.");
			}
			SensorMapperResult<Twist> mappedSensor = runtime.getMapperRegistry().mapSensor(runtime, velSensor, null,
					null);

			return new LinkBuilderResult(mappedSensor.getNetFragment(), null, mappedSensor.getSensorPort());
		}

		if (from instanceof VelocityDataflow && to instanceof VelocityDataflow) {
			VelocityDataflow fromV = (VelocityDataflow) from;
			VelocityDataflow toV = (VelocityDataflow) to;

			boolean refEquals = fromV.getReferenceFrame() == null || toV.getReferenceFrame() == null
					|| fromV.getReferenceFrame().equals(toV.getReferenceFrame());

			boolean movEquals = fromV.getMovingFrame() == null || toV.getMovingFrame() == null
					|| fromV.getMovingFrame().equals(toV.getMovingFrame());

			boolean pivEquals = fromV.getPivotPoint() == null || toV.getPivotPoint() == null
					|| fromV.getPivotPoint().equals(toV.getPivotPoint());

			boolean oriEquals = fromV.getOrientation() == null || toV.getOrientation() == null
					|| fromV.getOrientation().equals(toV.getOrientation());

			if (!pivEquals) {
				return equalizePivotPoint(fromV, toV);
			}

			if (!oriEquals) {
				return equalizeOrientation(fromV, toV);
			}

			if (!refEquals && !movEquals && fromV.getMovingFrame().equals(toV.getReferenceFrame())
					&& toV.getMovingFrame().equals(fromV.getReferenceFrame())) {
				return invertVelocity(fromV);
			}

			if (!refEquals || !movEquals) {
				return adaptVelocityFrames(fromV, toV);

			}
			System.out.println("[WorldLinkBuilder] VelocityDataflow link building didn't work");
		}

		if (from instanceof VelocityDataflow && to instanceof RelationDataflow) {
			NetFragment fragment = new NetFragment("Velocity->Frame");
			NetFragment integrator = fragment.add(new NetFragment("Frame integration"));
			FrameAddTwist add = integrator.add(new FrameAddTwist());

			RelationDataflow toF = (RelationDataflow) to;

			VelocityDataflow toV = new VelocityDataflow(toF.getTo(), toF.getFrom(), toF.getTo().getPoint(),
					toF.getFrom().getOrientation());

			DataflowInPort addVelIn = integrator.addInPort(toV, true, add.getInTwist());

			DataflowInPort fromVelPort = fragment.addConverterLink(addVelIn, toV);

			fragment.addInPort(fromVelPort);
			// fragment.connect(null, fromVelPort);

			DataflowInPort frameIn = fragment.addInPort(to, true, add.getInFrame());
			fragment.connect(null, frameIn);

			// DataflowOutPort originalFramePort = frameIn.getSource();
			// originalFramePort.getParentFragment().hidePort(originalFramePort);

			DataflowOutPort toFramePort = fragment.addOutPort(to, true, add.getOutValue());

			return new LinkBuilderResult(fragment, fromVelPort, toFramePort);

		}
		return null;
	}

	private DataflowOutPort combineTransformations(final NetFragment netFragment, List<DataflowOutPort> transformations,
			Frame start) throws MappingException {
		DataflowOutPort last = null;
		NetFragment combiner = new NetFragment("Combiner");
		netFragment.add(combiner);
		for (DataflowOutPort out : transformations) {
			RelationDataflow type = (RelationDataflow) out.getType();
			if (start == type.getFrom()) {
				start = type.getTo();
			} else if (start == type.getTo()) {
				FrameInvert inv = combiner.add(new FrameInvert());
				combiner.connect(out, combiner.addInPort(new RelationDataflow(type.getFrom(), type.getTo()), false,
						inv.getInValue()));
				out = combiner.addOutPort(new RelationDataflow(type.getTo(), type.getFrom()), false, inv.getOutValue());
				if (transformations.size() > 1) {
					netFragment.hidePort(out);
				}
				start = type.getFrom();
			} else {
				throw new MappingException("Invalid relation in transformation graph.");
			}
			if (last == null) {
				last = out;
			} else {
				FrameTransform ft = combiner.add(new FrameTransform());
				combiner.connect(last, combiner.addInPort(last.getType(), true, ft.getInFirst()));
				combiner.connect(out, combiner.addInPort(out.getType(), true, ft.getInSecond()));

				netFragment.hidePort(last);

				last = combiner.addOutPort(new RelationDataflow(((RelationDataflow) last.getType()).getFrom(),
						((RelationDataflow) out.getType()).getTo()), true, ft.getOutValue());
			}
		}

		return last;
	}

	private LinkBuilderResult addFrameFragment(Relation relation) throws MappingException {
		if (relation instanceof StaticConnection || relation instanceof Placement
				|| relation instanceof TemporaryRelation) {
			NetFragment ret = new NetFragment(relation.getFrom().getName() + " -> " + relation.getTo().getName());
			Transformation trans;
			try {
				trans = relation.getTransformation();
			} catch (RoboticsException e) {
				throw new MappingException(e);
			}

			final FrameFromPosRot fc = new FrameFromPosRot(trans.getTranslation().getX(), trans.getTranslation().getY(),
					trans.getTranslation().getZ(), trans.getRotation().getA(), trans.getRotation().getB(),
					trans.getRotation().getC());
			ret.add(fc);
			DataflowOutPort result = ret.addOutPort(new RelationDataflow(relation.getFrom(), relation.getTo()), true,
					fc.getOutValue());
			return new LinkBuilderResult(ret, null, result);
		} else if (relation instanceof DynamicConnection) {
			RelationSensor trans = ((DynamicConnection) relation).getRelationSensor();
			SensorMapperResult<?> result = runtime.getMapperRegistry().mapSensor(runtime, trans, null, null);
			return new LinkBuilderResult(result.getNetFragment(), null, result.getSensorPort());
		} else {
			throw new MappingException("Unknown relation type: " + relation);
		}
	}

	private LinkBuilderResult adaptVelocityFrames(VelocityDataflow fromV, VelocityDataflow toV)
			throws MappingException {
		NetFragment ret = new NetFragment("WorldLinkBuilder<ConvertVelocity>");

		List<Relation> first = toV.getReferenceFrame().getRelationsTo(fromV.getReferenceFrame());
		List<Relation> second = fromV.getMovingFrame().getRelationsTo(toV.getMovingFrame(),
				first == null ? new Relation[0] : first.toArray(new Relation[first.size()]));

		boolean invert = false;

		if (first == null || second == null) {
			first = toV.getReferenceFrame().getRelationsTo(fromV.getMovingFrame());
			second = fromV.getReferenceFrame().getRelationsTo(toV.getMovingFrame(),
					first.toArray(new Relation[first.size()]));

			invert = true;
		}

		if (first != null && second != null) {

			Frame refFirst = toV.getReferenceFrame();
			Frame movFirst = invert ? fromV.getMovingFrame() : fromV.getReferenceFrame();

			DataflowThroughOutPort firstThrough = ret.addThroughPort(true,
					new VelocityDataflow(movFirst, refFirst, fromV.getPivotPoint(), fromV.getOrientation()));

			DataflowThroughOutPort input = ret.addThroughPort(true, fromV);

			Frame inputRef = invert ? fromV.getMovingFrame() : fromV.getReferenceFrame();
			Frame inputMov = invert ? fromV.getReferenceFrame() : fromV.getMovingFrame();

			DataflowThroughOutPort inputThrough = ret.addThroughPort(true,
					new VelocityDataflow(inputMov, inputRef, fromV.getPivotPoint(), fromV.getOrientation()));

			Frame refSecond = invert ? fromV.getReferenceFrame() : fromV.getMovingFrame();
			Frame movSecond = toV.getMovingFrame();

			DataflowThroughOutPort secondThrough = ret.addThroughPort(true,
					new VelocityDataflow(movSecond, refSecond, fromV.getPivotPoint(), fromV.getOrientation()));

			DataflowOutPort firstPlusInput;
			if (refFirst.equals(movFirst)) {
				firstPlusInput = inputThrough;
			} else {
				AddVelocitiesFragment add = ret.add(new AddVelocitiesFragment("", firstThrough, inputThrough));

				firstPlusInput = add.getAddedVelocitiesPort();
			}

			DataflowOutPort secondPlusFirstPlusInput;
			if (refSecond.equals(movSecond)) {
				secondPlusFirstPlusInput = firstPlusInput;
			} else {
				secondPlusFirstPlusInput = ret.add(new AddVelocitiesFragment("", firstPlusInput, secondThrough))
						.getAddedVelocitiesPort();
			}

			ret.connect(null, firstThrough.getInPort());
			ret.connect(null, secondThrough.getInPort());
			ret.connect(input, inputThrough.getInPort());

			return new LinkBuilderResult(ret, input.getInPort(), secondPlusFirstPlusInput);
		}

		return null;
	}

	private LinkBuilderResult invertVelocity(VelocityDataflow fromV) throws MappingException {
		NetFragment ret = new NetFragment("WorldLinkBuilder<InvertVelocity>");

		DataflowThroughOutPort velFromThrough = ret.addThroughPort(true, fromV);

		InvertVelocityFragment invert = ret.add(new InvertVelocityFragment("", velFromThrough));

		return new LinkBuilderResult(ret, velFromThrough.getInPort(), invert.getInvertedVelocityPort());
	}

	private LinkBuilderResult equalizeOrientation(VelocityDataflow fromV, VelocityDataflow toV)
			throws MappingException {
		NetFragment ret = new NetFragment("WorldLinkBuilder<ChangeOrientation>");

		DataflowThroughOutPort velFromThrough = ret.addThroughPort(true, fromV);

		Frame newOrientationFrame = toV.getOrientation().getReferenceFrame().plus(new Vector(),
				toV.getOrientation().getRotation());
		Frame oldOrientationFrame = fromV.getOrientation().getReferenceFrame().plus(new Vector(),
				fromV.getOrientation().getRotation());

		DataflowThroughOutPort relativePosePort = ret.addThroughPort(true,
				new RelationDataflow(newOrientationFrame, oldOrientationFrame));
		ret.connect(null, relativePosePort.getInPort());

		AdaptVelocityOrientationFragment adapt = new AdaptVelocityOrientationFragment("", velFromThrough,
				relativePosePort, toV.getOrientation());

		ret.add(adapt);

		return new LinkBuilderResult(ret, velFromThrough.getInPort(), adapt.getOrientationAdaptedVelocityPort());
	}

	private LinkBuilderResult equalizePivotPoint(VelocityDataflow fromV, VelocityDataflow toV) throws MappingException {
		NetFragment ret = new NetFragment("WorldLinkBuilder<ChangePivotPoint>");

		DataflowThroughOutPort velFromThrough = ret.addThroughPort(true, fromV);

		Frame newPivotFrame = toV.getPivotPoint().getReferenceFrame().plus(toV.getPivotPoint().getVector());
		Frame oldPivotFrame = fromV.getPivotPoint().getReferenceFrame().plus(fromV.getPivotPoint().getVector());

		Frame oriFrame = fromV.getOrientation().getReferenceFrame().plus(new Vector(),
				fromV.getOrientation().getRotation());

		DataflowThroughOutPort relativePosePort = ret.addThroughPort(true,
				new RelationDataflow(oldPivotFrame, newPivotFrame));
		DataflowThroughOutPort relativeOriPort = ret.addThroughPort(true,
				new RelationDataflow(oriFrame, oldPivotFrame));

		ret.connect(null, relativePosePort.getInPort());
		ret.connect(null, relativeOriPort.getInPort());

		AdaptVelocityPivotPointFragment adapt = new AdaptVelocityPivotPointFragment("", velFromThrough,
				relativePosePort, relativeOriPort, toV.getPivotPoint());

		ret.add(adapt);

		return new LinkBuilderResult(ret, velFromThrough.getInPort(), adapt.getPivotAdaptedVelocityPort());
	}

}
