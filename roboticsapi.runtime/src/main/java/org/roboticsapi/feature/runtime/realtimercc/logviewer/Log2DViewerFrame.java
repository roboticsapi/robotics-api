/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.runtime.realtimercc.logviewer;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JFrame;

import org.roboticsapi.feature.runtime.realtimercc.logviewer.LogViewerFrame.Entry;

public class Log2DViewerFrame extends JFrame {
	private static final long serialVersionUID = -7326171854209402261L;
	private final Log2DPanel logPanel;

	public Log2DViewerFrame() {
		setTitle("Log file");

		logPanel = new Log2DPanel();
		add(logPanel);
		pack();
		setVisible(true);
		setState(Frame.MAXIMIZED_BOTH);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				logPanel.clearGraphs();
			}
		});
	}

	public void addGraph(String nameX, String nameY, List<Entry> x, List<Entry> y) {
		logPanel.addGraph(nameX, nameY, x, y);
	}

}
