/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.runtime.realtimercc.logviewer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class RealtimeRccLogViewer {

	public static void main(String[] args) throws IOException, Exception {

		Object[] options = new Object[] { "Local file", "Remote RCC Net", "Remote RCC Crash Dump" };
		Object choice = JOptionPane.showInputDialog(null, "What do you want to view today?", "LogViewer",
				JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

		if (choice == options[0]) {
			JFileChooser fc = new JFileChooser();
			fc.setFileFilter(new FileFilter() {

				@Override
				public String getDescription() {
					return "Log files (*.log)";
				}

				@Override
				public boolean accept(File f) {
					return f.isDirectory() || f.getName().toLowerCase().endsWith(".log");
				}
			});
			if (fc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
				return;
			}

			LogViewerFrame lf = new LogViewerFrame();
			lf.openFile(fc.getSelectedFile());

		} else if (choice == options[1]) {
			String host = JOptionPane.showInputDialog(null, "Please enter the host name", "127.0.0.1");
			String uri = "http://" + host + ":8080/nets/";
			InputStream document = new URL(uri).openStream();

			Document xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(document);
			NodeList net = xml.getElementsByTagName("net");
			List<String> nets = new ArrayList<String>();
			for (int i = 0; i < net.getLength(); i++) {
				nets.add(((Element) net.item(i)).getAttribute("uri"));
			}
			options = nets.toArray();
			choice = JOptionPane.showInputDialog(null, "Please select the requested net.", "LogViewer",
					JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
			uri = "http://" + host + ":8080/" + choice + "/debug";
			openRemoteFile(uri);

		} else if (choice == options[2]) {
			String host = JOptionPane.showInputDialog(null, "Please enter the host name", "127.0.0.1");
			String uri = "http://" + host + ":8080/crash/";
			InputStream document = new URL(uri).openStream();
			Scanner scanner = new Scanner(document, "UTF-8");
			String line = scanner.useDelimiter("\\A").next();
			scanner.close();

			Matcher matcher = Pattern.compile("<a href=\"/crash/(crash.*)\">").matcher(line);
			List<String> lines = new ArrayList<>();
			while (matcher.find()) {
				lines.add(matcher.group(1));
			}
			options = lines.toArray();
			choice = JOptionPane.showInputDialog(null, "Please select the requested log.", "LogViewer",
					JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

			uri = "http://" + host + ":8080/crash/" + choice;
			openRemoteFile(uri);
		}
	}

	private static void openRemoteFile(String uri) throws IOException, MalformedURLException, FileNotFoundException {
		InputStream document;
		document = new URL(uri).openStream();
		File file = File.createTempFile("log", ".log");
		byte[] block = new byte[4096];
		int len;
		OutputStream out = new FileOutputStream(file);
		while ((len = document.read(block)) != -1) {
			out.write(block, 0, len);
		}
		out.close();
		LogViewerFrame lf = new LogViewerFrame();
		lf.openFile(file);
	}

}
