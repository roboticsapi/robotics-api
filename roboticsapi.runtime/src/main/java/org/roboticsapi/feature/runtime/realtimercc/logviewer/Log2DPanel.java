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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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

public class Log2DPanel extends JPanel {
	private static final long serialVersionUID = -3273772172554930156L;
	double scale = 1;
	List<List<Entry>> graphX = new ArrayList<List<Entry>>();
	List<List<Entry>> graphY = new ArrayList<List<Entry>>();
	List<String> graphNamesX = new ArrayList<String>();
	List<String> graphNamesY = new ArrayList<String>();
	double minX, maxX, minY, maxY;
	private final Color[] cols = new Color[] { new Color(128, 0, 0), new Color(0, 128, 0), new Color(0, 0, 128),
			new Color(128, 128, 0), new Color(128, 0, 128), new Color(0, 128, 128), new Color(128, 128, 128) };
	int sel = -1;
	private final JScrollBar scrollX, scrollY;
	protected boolean includeOrigin = false;

	public Log2DPanel() {
		setLayout(new BorderLayout(0, 0));

		setFocusable(true);
		scrollX = new JScrollBar();
		scrollX.setMaximum(1);
		scrollX.setOrientation(Adjustable.HORIZONTAL);
		add(scrollX, BorderLayout.SOUTH);
		scrollX.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				repaint();
			}
		});

		scrollY = new JScrollBar();
		scrollY.setMaximum(1);
		scrollY.setOrientation(Adjustable.VERTICAL);
		add(scrollY, BorderLayout.EAST);
		scrollY.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				repaint();
			}
		});

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					includeOrigin = !includeOrigin;
					calculateMinMax();
					repaint();
				}
			}
		});

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				double posX = (scrollX.getValue() + sel / scale) / scale;
				double posY = (scrollY.getValue() + sel / scale) / scale;
				if (SwingUtilities.isLeftMouseButton(e)) {
					scale *= 2;
				} else if (SwingUtilities.isRightMouseButton(e)) {
					scale /= 2;
				}
				if (scale < 0.1) {
					scale = 0.1;
				}
				scrollX.setMaximum((int) (scale * getWidth()));
				scrollX.setVisibleAmount(getWidth());
				scrollX.setValue((int) (posX * scale - sel / scale));

				scrollY.setMaximum((int) (scale * getHeight()));
				scrollY.setVisibleAmount(getHeight());
				scrollY.setValue((int) (posY * scale - sel / scale));
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				sel = e.getX();
				repaint();
			}
		});

	}

	public void addGraph(String nameX, String nameY, List<Entry> x, List<Entry> y) {
		graphX.add(x);
		graphY.add(y);
		graphNamesX.add(nameX);
		graphNamesY.add(nameY);
		calculateMinMax();
		repaint();
	}

	private void calculateMinMax() {
		minX = Double.MAX_VALUE;
		maxX = -Double.MAX_VALUE;
		minY = Double.MAX_VALUE;
		maxY = -Double.MAX_VALUE;
		if (includeOrigin) {
			minX = maxX = minY = maxY = 0;
		}
		for (List<Entry> graph : graphX) {
			for (Entry e : graph) {
				if (e.getValue() > maxX) {
					maxX = e.getValue();
				}
				if (e.getValue() < minX) {
					minX = e.getValue();
				}
			}
		}

		for (List<Entry> graph : graphY) {
			for (Entry e : graph) {
				if (e.getValue() > maxY) {
					maxY = e.getValue();
				}
				if (e.getValue() < minY) {
					minY = e.getValue();
				}
			}
		}
	}

	public void clearGraphs() {
		graphX.clear();
		graphY.clear();
		graphNamesX.clear();
		graphNamesY.clear();
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
		ig.draw(new Line2D.Double(0, scaleY(0), getWidth(), scaleY(0)));
		ig.draw(new Line2D.Double(scaleX(0), 0, scaleX(0), getHeight()));

		String[] valX = new String[graphX.size()];
		String[] valY = new String[graphX.size()];
		int col = 0;
		Rectangle2D textBounds = new Rectangle2D.Double(), valueBounds = new Rectangle2D.Double();
		for (int gnr = 0; gnr < graphNamesX.size(); gnr++) {
			List<Entry> gX = graphX.get(gnr);
			List<Entry> gY = graphY.get(gnr);
			valX[col] = "";
			valY[col] = "";
			g.setColor(cols[col % cols.length]);
			for (int i = 1; i < gX.size() && i < gY.size(); i++) {
				Entry lastX = gX.get(i - 1);
				Entry curX = gX.get(i);
				Entry lastY = gY.get(i - 1);
				Entry curY = gY.get(i);
				double sx = scaleX(lastX.getValue());
				double sy = scaleY(lastY.getValue());
				double ex = scaleX(curX.getValue());
				double ey = scaleY(curY.getValue());
				ig.draw(new Line2D.Double(sx, sy, ex, ey));

				if (i * getWidth() / gX.size() < sel && (i + 1) * getWidth() / gX.size() >= sel) {
					valX[col] = String.format("%+1.5f", curX.getValue());
					valY[col] = String.format("%+1.5f", curY.getValue());
					ig.draw(new Ellipse2D.Double(ex - 2, ey - 2, 4, 4));
				}
			}
			Rectangle2D.union(textBounds, ig.getFontMetrics().getStringBounds(graphNamesX.get(col), ig), textBounds);

			Rectangle2D.union(textBounds, ig.getFontMetrics().getStringBounds(graphNamesY.get(col), ig), textBounds);

			Rectangle2D.union(valueBounds, ig.getFontMetrics().getStringBounds(valX[col], ig), valueBounds);

			Rectangle2D.union(valueBounds, ig.getFontMetrics().getStringBounds(valY[col], ig), valueBounds);

			col++;

		}

		if (graphNamesX.size() > 0) {
			int boxWidth = (int) (textBounds.getWidth() + valueBounds.getWidth() + 20);
			int xpos = sel > getWidth() / 2 ? 5 : getWidth() - scrollY.getWidth() - boxWidth - 5;
			ig.setColor(new Color(220, 220, 220, 200));
			ig.fill(new Rectangle2D.Double(xpos, 5, boxWidth, graphNamesX.size() * 40));
			ig.setColor(new Color(128, 128, 128));
			ig.draw(new Rectangle2D.Double(xpos, 5, boxWidth, graphNamesX.size() * 40));

			for (col = 0; col < graphNamesX.size(); col++) {
				g.setColor(cols[col % cols.length]);
				g.drawString(graphNamesX.get(col), xpos + 5, 40 * col + 20);
				g.drawString(graphNamesY.get(col), xpos + 5, 40 * col + 40);
				g.drawString(valX[col], (int) (xpos + 15 + textBounds.getWidth()), 40 * col + 20);
				g.drawString(valY[col], (int) (xpos + 15 + textBounds.getWidth()), 40 * col + 40);
			}
		}
	}

	private double scaleY(double value) {
		return ((value - minY) / (maxY - minY) * (getHeight() - 20 - scrollX.getHeight())) * scale + 10
				- scrollY.getValue();

	}

	private double scaleX(double value) {
		return ((value - minX) / (maxX - minX) * (getWidth() - 20 - scrollY.getWidth())) * scale + 10
				- scrollX.getValue();

	}
}
