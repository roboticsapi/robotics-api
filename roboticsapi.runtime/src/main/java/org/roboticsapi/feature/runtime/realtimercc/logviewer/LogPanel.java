/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.runtime.realtimercc.logviewer;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;

import org.roboticsapi.feature.runtime.realtimercc.logviewer.LogViewerFrame.Entry;

public class LogPanel extends JPanel {
	private static final long serialVersionUID = -3273772172554930156L;
	double scale = 1;
	List<List<Entry>> graphs = new ArrayList<List<Entry>>();
	List<String> graphNames = new ArrayList<String>();
	long minTime, maxTime;
	double minValue, maxValue;
	private final Color[] cols = new Color[] { new Color(128, 0, 0), new Color(0, 128, 0), new Color(0, 0, 128),
			new Color(128, 128, 0), new Color(128, 0, 128), new Color(0, 128, 128), new Color(128, 128, 128) };
	int selX = -1;
	private final JScrollBar scroll;

	public LogPanel() {
		setLayout(new BorderLayout(0, 0));

		scroll = new JScrollBar();
		scroll.setMaximum(1);
		scroll.setOrientation(Adjustable.HORIZONTAL);
		add(scroll, BorderLayout.SOUTH);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				double pos = (scroll.getValue() + selX / scale) / scale;
				if (SwingUtilities.isLeftMouseButton(e)) {
					scale *= 2;
				} else if (SwingUtilities.isRightMouseButton(e)) {
					scale /= 2;
				}
				if (scale < 1) {
					scale = 1;
				}
				scroll.setMaximum((int) (scale * getWidth()));
				scroll.setVisibleAmount(getWidth());
				scroll.setValue((int) (pos * scale - selX / scale));
			}
		});
		scroll.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				repaint();
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				selX = e.getX();
				repaint();
			}
		});
	}

	public void addGraph(String name, List<Entry> data) {
		graphs.add(data);
		graphNames.add(name);
		calculateMinMax();
		repaint();
	}

	private void calculateMinMax() {
		minTime = Long.MAX_VALUE;
		maxTime = Long.MIN_VALUE;
		minValue = Double.MAX_VALUE;
		maxValue = -Double.MAX_VALUE;
		for (List<Entry> graph : graphs) {
			for (Entry e : graph) {
				if (e.getTime() > maxTime) {
					maxTime = e.getTime();
				}
				if (e.getTime() < minTime) {
					minTime = e.getTime();
				}
				if (e.getValue() > maxValue) {
					maxValue = e.getValue();
				}
				if (e.getValue() < minValue) {
					minValue = e.getValue();
				}
			}
		}

	}

	public void clearGraphs() {
		graphs.clear();
		graphNames.clear();
		repaint();
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension((int) (scale * 600), 400);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D ig = (Graphics2D) g;
		ig.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());

		g.setColor(Color.gray);
		ig.draw(new Line2D.Double(0, scaleValue(0), getWidth(), scaleValue(0)));
		String[] vals = new String[graphs.size()];
		int col = 0;
		Rectangle2D textBounds = new Rectangle2D.Double(), valueBounds = new Rectangle2D.Double();
		for (List<Entry> graph : graphs) {
			vals[col] = "";
			g.setColor(cols[col % cols.length]);
			for (int i = 1; i < graph.size(); i++) {
				Entry last = graph.get(i - 1);
				Entry cur = graph.get(i);
				double sx = scaleTime(last.getTime());
				double sy = scaleValue(last.getValue());
				double ex = scaleTime(cur.getTime());
				double ey = scaleValue(cur.getValue());
				ig.draw(new Line2D.Double(sx, sy, ex, ey));

				if (sx < selX && ex >= selX) {
					vals[col] = String.format("%+1.5f", cur.getValue());
					ig.draw(new Ellipse2D.Double(scaleTime(cur.getTime()) - 2, scaleValue(cur.getValue()) - 2, 4, 4));
				}
			}
			Rectangle2D.union(textBounds, ig.getFontMetrics().getStringBounds(graphNames.get(col), ig), textBounds);

			Rectangle2D.union(valueBounds, ig.getFontMetrics().getStringBounds(vals[col], ig), valueBounds);

			col++;

		}

		if (graphs.size() > 0) {
			int boxWidth = (int) (textBounds.getWidth() + valueBounds.getWidth() + 20);
			int xpos = selX > getWidth() / 2 ? 5 : getWidth() - boxWidth - 5;
			ig.setColor(new Color(220, 220, 220, 200));
			ig.fill(new Rectangle2D.Double(xpos, 5, boxWidth, graphs.size() * 20));
			ig.setColor(new Color(128, 128, 128));
			ig.draw(new Rectangle2D.Double(xpos, 5, boxWidth, graphs.size() * 20));

			for (col = 0; col < graphs.size(); col++) {
				g.setColor(cols[col % cols.length]);
				g.drawString(graphNames.get(col), xpos + 5, 20 * col + 20);
				g.drawString(vals[col], (int) (xpos + 15 + textBounds.getWidth()), 20 * col + 20);
			}
		}

	}

	private double scaleValue(double value) {
		return getHeight() - scroll.getHeight()
				- ((value - minValue) / (maxValue - minValue) * (getHeight() - 20 - scroll.getHeight())) - 10;

	}

	private double scaleTime(long time) {
		return ((double) (time - minTime) / (maxTime - minTime) * getWidth() * scale) - scroll.getValue();
	}
}
