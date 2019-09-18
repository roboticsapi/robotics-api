/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.runtime.realtimercc.logviewer;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class LogViewerFrame extends JFrame {
	private static final long serialVersionUID = -7326171854209402261L;

	class Entry {
		final long time;
		final double value;

		public Entry(long time, double value) {
			this.time = time;
			this.value = value;
		}

		public long getTime() {
			return time;
		}

		public double getValue() {
			return value;
		}
	}

	Map<String, List<Entry>> data = new LinkedHashMap<String, List<Entry>>();
	private final JList<Object> list;
	private final LogPanel logPanel;
	Log2DViewerFrame log2d;

	public LogViewerFrame() {
		setTitle("Log file");

		JSplitPane splitPane = new JSplitPane();
		getContentPane().add(splitPane, BorderLayout.CENTER);

		JScrollPane scrollPane = new JScrollPane();
		splitPane.setLeftComponent(scrollPane);

		list = new JList<Object>();
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				logPanel.clearGraphs();
				for (int i : list.getSelectedIndices()) {
					Object item = list.getModel().getElementAt(i);
					logPanel.addGraph(item.toString(), data.get(item));
				}
			}
		});
		scrollPane.setViewportView(list);

		JPanel pnlButtons = new JPanel();
		scrollPane.setColumnHeaderView(pnlButtons);

		JButton btnDerive = new JButton("Derive");
		btnDerive.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i : list.getSelectedIndices()) {
					Object item = list.getModel().getElementAt(i);
					List<Entry> raw = data.get(item);
					List<Entry> der = new ArrayList<LogViewerFrame.Entry>();
					for (int j = 1; j < raw.size(); j++) {
						Entry e1 = raw.get(j - 1);
						Entry e2 = raw.get(j);
						double nv = (e2.getValue() - e1.getValue()) * 1e9 / (e2.getTime() - e1.getTime());
						der.add(new Entry(e2.getTime(), nv));
					}
					data.put(item + " d/dt", der);
				}
				fillList();
			}
		});

		pnlButtons.add(btnDerive);

		JButton btn2D = new JButton("2D");
		pnlButtons.add(btn2D);
		btn2D.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] selectedIndices = list.getSelectedIndices();
				if (selectedIndices.length != 2) {
					return;
				}

				if (log2d == null) {
					log2d = new Log2DViewerFrame();
				}
				log2d.setVisible(true);
				Object x = list.getModel().getElementAt(selectedIndices[0]);
				Object y = list.getModel().getElementAt(selectedIndices[1]);
				List<Entry> rx = data.get(x);
				List<Entry> ry = data.get(y);

				log2d.addGraph(x.toString(), y.toString(), rx, ry);
			}
		});

		JButton btnSaveToCSV = new JButton("Save to CSV");
		btnSaveToCSV.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				saveCSV();
			}
		});
		pnlButtons.add(btnSaveToCSV);

		logPanel = new LogPanel();
		splitPane.setRightComponent(logPanel);
		pack();
		setVisible(true);
		setState(Frame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public void saveCSV() {
		FileNameExtensionFilter fnef = new FileNameExtensionFilter("CSV Filed", "csv");

		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(fnef);
		if (fc.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File selFile = new File(fc.getSelectedFile().getAbsolutePath() + ".csv");

		try {
			Writer osw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(selFile)));

			Map<Long, Map<String, Double>> sortedList = new TreeMap<Long, Map<String, Double>>();
			for (Map.Entry<String, List<Entry>> dataentry : data.entrySet()) {

				for (Entry entry : dataentry.getValue()) {
					if (!sortedList.containsKey(entry.time)) {
						sortedList.put(entry.time, new HashMap<String, Double>());
					}

					sortedList.get(entry.time).put(dataentry.getKey(), entry.value);
				}
			}

			// write header
			ArrayList<String> entryKeys = new ArrayList<String>(data.keySet());
			Collections.sort(entryKeys);

			osw.write("time;");

			for (String datakey : entryKeys) {
				osw.write(datakey);
				osw.write(";");
			}
			osw.write("\n");

			NumberFormat numberFormat = NumberFormat.getInstance();
			numberFormat.setMinimumFractionDigits(10);

			for (Map.Entry<Long, Map<String, Double>> sortedEntry : sortedList.entrySet()) {
				osw.write(sortedEntry.getKey().toString());
				osw.write(";");
				for (String datakey : entryKeys) {
					Double val = sortedEntry.getValue().get(datakey);
					if (val != null) {

						osw.write(numberFormat.format(val));
					}
					osw.write(";");
				}

				osw.write("\n");
			}

			osw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {

		}

	}

	public void openFile(File log) throws IOException {
		loadFile(log);
		fillList();
		setTitle(log.getName());
	}

	private void fillList() {
		DefaultListModel<Object> model = new DefaultListModel<Object>();
		List<String> keys = new ArrayList<String>(data.keySet());
		Collections.sort(keys);
		for (String key : keys) {
			model.addElement(key);
		}
		list.setModel(model);
	}

	private void loadFile(File log) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(log));
		String firstLine = br.readLine();
		br.close();
		br = new BufferedReader(new FileReader(log));
		if (firstLine.contains("\t")) {
			loadDebugLog(br);
		} else {
			loadCrashDump(br);
		}
		br.close();
	}

	private void loadDebugLog(BufferedReader br) throws IOException {
		String header = br.readLine();
		String[] headers = header.split("\t");
		while (true) {
			String line = br.readLine();
			if (line == null || !line.contains("\t")) {
				break;
			}
			String[] parts = line.split("\t");
			long time = (long) (Double.parseDouble(parts[0]) * 1000000);
			for (int i = 1; i < parts.length; i++) {
				consume(time, headers[i], new ByteArrayInputStream(parts[i].getBytes()));
			}
		}
	}

	private void consume(long time, String name, InputStream stream) throws IOException {
		int read = stream.read();
		switch (read) {
		case '[':
			for (int i = 0; true; i++) {
				consume(time, name + "[" + i + "]", stream);
				read = stream.read();
				if (read == ',') {
					continue;
				} else if (read == ']') {
					break;
				} else if (read == -1) {
					throw new IOException("Unexpected EOF in array");
				} else {
					throw new IOException("Unexpected character " + ((char) read) + " in array");
				}
			}
			break;
		case '{':
			StringBuffer key = new StringBuffer();
			while (true) {
				read = stream.read();
				if (read == -1) {
					throw new IOException("Unexpected EOF in object");
				} else if (read == ':') {
					consume(time, name + "." + key, stream);
					key = new StringBuffer();
					read = stream.read();
					if (read == -1) {
						throw new IOException("Unexpected EOF in object");
					} else if (read == ',') {
						continue;
					} else if (read == '}') {
						break;
					} else {
						throw new IOException("Unexpected character " + ((char) read) + " in object");
					}
				} else {
					key.append((char) read);
				}
			}
			break;
		case -1:
			break;
		default:
			StringBuffer val = new StringBuffer();
			val.append((char) read);
			while (true) {
				stream.mark(1);
				read = stream.read();
				if (read == -1 || read == ',' || read == '}' || read == ']') {
					putRaw(name, time, val.toString());
					stream.reset();
					break;
				} else {
					val.append((char) read);
				}
			}
			break;
		}
	}

	private void putRaw(String name, long time, String part) {
		if (!data.containsKey(name)) {
			data.put(name, new ArrayList<LogViewerFrame.Entry>());
		}
		if (part.equals("n/a")) {
			// do nothing
		} else if (part.equals("null")) {
			// do nothing
		} else if (part.equals("true")) {
			data.get(name).add(new Entry((time * 1000000), 1));
		} else if (part.equals("false")) {
			data.get(name).add(new Entry((time * 1000000), 0));
		} else {
			double value = Double.parseDouble(part);
			data.get(name).add(new Entry((time * 1000000), value));
		}
	}

	private void loadCrashDump(BufferedReader br) throws IOException {
		while (true) {
			String header = br.readLine();
			if (header == null) {
				break;
			}
			List<Entry> entries = new ArrayList<Entry>();
			long lastTime = 0;
			while (true) {
				String line = br.readLine();
				if (line == null) {
					break;
				}
				String[] parts = line.split("\t");
				if (parts.length != 2) {
					break;
				}
				long time = Long.parseLong(parts[0]);
				if (time < lastTime) {
					entries.clear();
				}
				consume(time, header, new ByteArrayInputStream(parts[1].getBytes()));
				// entries.add(new Entry(time, Double.parseDouble(parts[1])));
				lastTime = time;
			}
			// data.put(header, entries);
		}
	}
}
