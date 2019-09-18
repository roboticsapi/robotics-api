/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.runtime.realtimercc.websocket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

import org.roboticsapi.core.util.RAPILogger;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.feature.runtime.realtimercc.dioprotocol.DIOCommand;
import org.roboticsapi.feature.runtime.realtimercc.dioprotocol.DIOProtocol;
import org.roboticsapi.feature.runtime.realtimercc.dioprotocol.DIOString;

public class WebsocketProtocol extends DIOProtocol {
	private final int CONNECTIONTIMEOUT = 3000;
	private final InputStream reader;
	private final OutputStream writer;
	private Thread readThread;

	Socket socket;

	public WebsocketProtocol(String uri, ProtocolCallback callback, String name) throws RpiException {
		super(callback, name);
		try {
			url = new URI(uri);
			socket = new Socket();
			socket.connect(new InetSocketAddress(url.getHost(), url.getPort()), CONNECTIONTIMEOUT);
			this.writer = new BufferedOutputStream(socket.getOutputStream());
			this.reader = new BufferedInputStream(socket.getInputStream());
			initWebsocket(url.getHost(), "/DirectIO/");
			init();
		} catch (URISyntaxException e) {
			throw new RpiException(e);
		} catch (IOException e) {
			throw new RpiException(e);
		}
	}

	private void initWebsocket(String host, String uri) throws IOException {
		OutputStreamWriter osw = new OutputStreamWriter(writer);
		osw.write("GET " + uri + " HTTP/1.1\r\n" + "Host: " + host + "\r\n" + "Upgrade: websocket\r\n"
				+ "Connection: Upgrade\r\n" + "Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==\r\n" + "\r\n");
		osw.flush();
		BufferedReader br = new BufferedReader(new InputStreamReader(reader));
		boolean accepted = false;
		String line;
		while (true) {
			line = br.readLine();
			if (line == null || line.equals("")) {
				break;
			}
			if (line.startsWith("Sec-WebSocket-Accept: s3pPLMBiTxaQ9kYGzzhZRbK+xOo=")) {
				accepted = true;
			}
		}
		if (!accepted) {
			throw new IOException("Failed to setup websocket connection. ");
		}
	}

	public boolean init() {
		try {
			DIOCommand ver = new DIOCommand("ver");
			ver.addParameter(new DIOString(DIO_VERSION));
			String tag = writeCommand(ver);
			readOne();
			if (!checkTagStatus(tag)) {
				return false;
			}

			ready = true;
			readThread = new Thread(new Runnable() {
				@Override
				public void run() {
					while (ready) {
						readOne();
					}
				}
			});
			readThread.setDaemon(true);
			readThread.setName("RPI-RAPI-Bridge [" + name + "]");
			readThread.start();
			return true;
		} catch (IOException e) {
			// e.printStackTrace();
			ready = false;
			return false;
		}
	}

	public void quit() {
		try {
			ready = false;
			writeWebsocket(8, "BYE");
			reader.close();
			writer.close();
			readThread.join();
		} catch (IOException e) {
		} catch (InterruptedException e) {
		}
	}

	private int cmdNr = 0;
	private URI url;

	private synchronized int nextCmdNr() {
		return cmdNr++;
	}

	@Override
	protected String writeCommand(DIOCommand command) throws IOException {
		String tag = "c" + nextCmdNr();

		writeWebsocket(1, tag + "=" + command.toString());
		return tag;
	}

	private synchronized void writeWebsocket(int opcode, String data) throws IOException {
		RAPILogger.getLogger(this).finest(System.currentTimeMillis() + " " + name + ">>" + opcode + ":" + data);
		writer.write(((1 << 7) + opcode));
		if (data.length() > 65535) {
			writer.write(127);
			writer.write(0);
			writer.write(0);
			writer.write(0);
			writer.write(0);
			writer.write((data.length() >> 24) & 0xff);
			writer.write((data.length() >> 16) & 0xff);
			writer.write((data.length() >> 8) & 0xff);
			writer.write(data.length() & 0xff);
			writer.write(data.getBytes());
		} else if (data.length() > 125) {
			writer.write(126);
			writer.write((data.length() >> 8) & 0xff);
			writer.write(data.length() & 0xff);
			writer.write(data.getBytes());
		} else {
			writer.write(data.length());
			writer.write(data.getBytes());
		}
		writer.flush();
	}

	protected void readOne() {

		String request = "";
		try {
			while (true) {
				byte[] msg = new byte[2];
				if (!readBlocking(msg)) {
					break;
				}
				boolean fin = ((msg[0] >> 7) & 1) != 0;
				boolean rsv1 = ((msg[0] >> 6) & 1) != 0;
				boolean rsv2 = ((msg[0] >> 5) & 1) != 0;
				boolean rsv3 = ((msg[0] >> 4) & 1) != 0;
				if (rsv1 || rsv2 || rsv3) {
					// TODO: illegal command - close connection
				}
				int opcode = (msg[0] & 0x0F);
				boolean mask = ((msg[1] >> 7) & 1) != 0;
				int len = msg[1] & 0x7F;
				if (len == 126) {
					byte[] len2 = new byte[2];
					if (!readBlocking(len2)) {
						break;
					}
					len = ((len2[0] & 0xff) << 8) + (len2[1] & 0xff);
				} else if (len == 127) {
					byte[] len8 = new byte[8];
					if (!readBlocking(len8)) {
						break;
					}
					len = ((((((len8[4] & 0xff) << 8) + (len8[5] & 0xff)) << 8) + (len8[6] & 0xff)) << 8)
							+ (len8[7] & 0xff);
				}
				byte[] maskkey = new byte[4];
				if (mask) {
					if (!readBlocking(maskkey)) {
						break;
					}
				}
				if (len > 8 * 1024 * 1024) {
					return;
				}
				byte[] data = new byte[len];
				if (!readBlocking(data)) {
					break;
				}
				if (mask) {
					for (int i = 0; i < len; i++) {
						data[i] = (byte) (data[i] ^ maskkey[i % 4]);
					}
				}

				RAPILogger.getLogger(this)
						.finest(System.currentTimeMillis() + " " + name + "<<" + opcode + ":" + new String(data));

				switch (opcode) {
				case 0: // Continuation
				case 1: // Text
				case 2: // Binary
					request += new String(data);
					if (fin) {
						netcommsChanged = false;
						parseDirectIO(request);
						if (netcommsChanged) {
							callback.notifyNetcommUpdated();
						}
						request = "";
						return;
					}
					break;
				case 8: // CLOSE
					writeWebsocket(8, new String(data));
					ready = false;
					return;
				case 9: // PING
					writeWebsocket(10, new String(data));
					return;
				case 10: // PONG
					setTagStatus(new String(data), true, "");
					return;
				default:
					break;
				}

				request += new String(data);
			}

		} catch (IOException e) {
			if (ready) {
				RAPILogger.getLogger(this).warning("Connection to " + url + " lost.");
				ready = false;
			}
		}
	}

	public void ping() throws IOException {
		String tag = "p" + cmdNr++;
		writeWebsocket(9, tag);
		if (!checkTagStatus(tag, 10)) {
			throw new IOException("Connection lost.");
		}
	}

	private boolean readBlocking(byte[] msg) throws IOException {
		int len = msg.length, start = 0;
		while (start < len) {
			int cnt = reader.read(msg, start, len - start);
			start += cnt;
			if (cnt < 0) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isReaderThread() {
		return Thread.currentThread() == readThread;
	}

}
