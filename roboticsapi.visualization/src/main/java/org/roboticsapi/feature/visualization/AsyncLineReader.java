/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.visualization;

import java.io.EOFException;
import java.io.InputStream;
import java.util.concurrent.TimeoutException;

public class AsyncLineReader {

	private final Thread t;
	private boolean alive = true;
	private final Object monitor = new Object();
	private String nextLine = null;
	private Exception exception = null;

	private static final char linefeed = '\n';
	private static final char carriagereturn = '\r';

	public AsyncLineReader(InputStream inputStream) {
		t = new Thread(() -> run(inputStream));
		t.setName("Stream reader");
		t.setDaemon(true);
		t.start();
	}

	private void run(InputStream inputStream) {
		try {
			boolean terminatedByCarriageReturn = false;
			while (alive) {
				synchronized (monitor) {
					while (nextLine != null) {
						if (!alive)
							return;
						try {
							monitor.wait();
						} catch (InterruptedException e) {
						}
					}
				}

				StringBuilder buffer = new StringBuilder();
				while (true) {
					if (!alive)
						return;
					int chr = inputStream.read();
					if (chr == -1)
						return;
					if (terminatedByCarriageReturn && chr == linefeed) {
						terminatedByCarriageReturn = false;
						continue;
					}
					if (chr == carriagereturn || chr == linefeed) {
						terminatedByCarriageReturn = chr == carriagereturn;
						break;
					}
					terminatedByCarriageReturn = false;
					buffer.append((char) chr);
				}
				synchronized (monitor) {
					nextLine = buffer.toString();
					buffer = new StringBuilder();
					monitor.notifyAll();
				}
			}
		} catch (Exception e) {
			exception = e;
		} finally {
			alive = false;
			try {
				inputStream.close();
			} catch (Exception e) {
			}
			synchronized (monitor) {
				monitor.notifyAll();
			}
		}
	}

	/**
	 * Reads a line of text. A line is considered to be terminated by any one of a
	 * line feed ('\n'), a carriage return ('\r'), or a carriage return followed
	 * immediately by a line feed. This method is blocking until a line of data is
	 * available, timeout is reached or an error occurs (connection close or error).
	 *
	 * @param timeout the timeout in milliseconds. If there is nothing to read in
	 *                this time period, d {@link TimeoutException} is thrown
	 * @return A String containing the contents of the line, not including any
	 *         line-termination characters
	 *
	 * @exception EOFException If stream is closed (planned or by error)
	 *                         TimeoutException If specified timeout has run out
	 */
	public String readLine(long timeout) throws TimeoutException, EOFException {
		long start = System.currentTimeMillis();
		synchronized (monitor) {
			checkTermination();
			while (nextLine == null) {
				try {
					long maxWait = timeout - (System.currentTimeMillis() - start);
					if (maxWait <= 0)
						throw new TimeoutException();
					monitor.wait(maxWait);
				} catch (InterruptedException e) {
				}
				checkTermination();
			}
			String result = nextLine;
			nextLine = null;
			monitor.notifyAll();
			return result;
		}
	}

	/**
	 * Reads a line of text. A line is considered to be terminated by any one of a
	 * line feed ('\n'), a carriage return ('\r'), or a carriage return followed
	 * immediately by a linefeed. This method is blocking until a line of data is
	 * available or an error occurs (connection close or error).
	 *
	 * @return A String containing the contents of the line, not including any
	 *         line-termination characters
	 *
	 * @exception EOFException If stream is closed (planned or by error)
	 */
	public String readLine() throws EOFException {
		synchronized (monitor) {
			checkTermination();
			while (nextLine == null) {
				try {
					monitor.wait();
				} catch (InterruptedException e) {
				}
				checkTermination();
			}
			String result = nextLine;
			nextLine = null;
			monitor.notifyAll();
			return result;
		}
	}

	/**
	 * Checks whether a new line of data is available for read. If this mehtod
	 * returns true, a successive call of readLine() is guaranteed to be
	 * non-blocking.
	 * 
	 * @return true, if a new line of data is available
	 * @throws EOFException
	 */
	public boolean ready() throws EOFException {
		synchronized (monitor) {
			checkTermination();
			return (nextLine != null);
		}
	}

	private void checkTermination() throws EOFException {
		if (exception != null) {
			if (exception instanceof EOFException)
				throw (EOFException) exception;
			else
				throw new EOFException(exception.getMessage());
		}
		if (!alive)
			throw new EOFException();
	}

	public void close() {
		alive = false;
		t.interrupt();
		synchronized (monitor) {
			monitor.notifyAll();
		}
		while (true) {
			try {
				t.join();
				break;
			} catch (InterruptedException e) {
			}
		}
	}

}
