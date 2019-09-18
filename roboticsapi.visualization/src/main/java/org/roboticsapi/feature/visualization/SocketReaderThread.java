/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.visualization;

import java.io.EOFException;
import java.io.InputStream;
import java.net.Socket;

import org.roboticsapi.core.util.RAPILogger;

public class SocketReaderThread {

	private final Thread t;
	private boolean alive = true;

	private static final char linefeed = '\n';
	private static final char carriagereturn = '\r';

	public static interface SocketReaderCallback {
		public void messageReceived(String message);

		public void connectionClosed(Exception e);
	}

	public SocketReaderThread(Socket socket, SocketReaderCallback callback) {
		t = new Thread(() -> run(socket, callback));
		t.setName("Socket reader");
		t.setDaemon(true);
		t.start();
	}

	private void run(Socket socket, SocketReaderCallback callback) {
		Exception exception = null;
		try {
			InputStream inputStream = socket.getInputStream();
			boolean terminatedByCarriageReturn = false;
			StringBuilder buffer = new StringBuilder();
			while (alive) {
				int chr = inputStream.read();
				if (chr == -1)
					throw new EOFException();
				if (terminatedByCarriageReturn && chr == linefeed) {
					terminatedByCarriageReturn = false;
				} else if (chr == carriagereturn || chr == linefeed) {
					terminatedByCarriageReturn = chr == carriagereturn;
					try {
						callback.messageReceived(buffer.toString());
					} catch (Exception e) {
						RAPILogger.logException(getClass(), e);
					}
					buffer = new StringBuilder();
				} else {
					terminatedByCarriageReturn = false;
					buffer.append((char) chr);
				}
			}
		} catch (Exception e) {
			exception = e;
		} finally {
			alive = false;
			try {
				socket.close();
			} catch (Exception e) {
			}
			try {
				callback.connectionClosed(exception);
			} catch (Exception e) {
				RAPILogger.logException(getClass(), e);
			}
		}
	}

	public void close() {
		alive = false;
		t.interrupt();
		while (true) {
			try {
				t.join();
				break;
			} catch (InterruptedException e) {
			}
		}
	}

}
