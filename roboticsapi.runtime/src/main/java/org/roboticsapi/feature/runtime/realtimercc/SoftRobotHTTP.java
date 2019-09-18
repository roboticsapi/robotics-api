/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.runtime.realtimercc;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Helper class for SoftRobot RCC HTTP access
 */
public class SoftRobotHTTP {

	public static String encode(final String url) {
		try {
			return URLEncoder.encode(url, "UTF-8");
		} catch (final UnsupportedEncodingException e) {
			return url;
		}
	}

	/** executes an HTTP GET */
	public static HTTPResponse get(final String uri) {
		return exec("GET", uri, null);
	}

	/**
	 * Follows redirects and returns the final URL
	 * 
	 * @param uri          URL to watch
	 * @param maxRedirects maximum number of redirects to follow
	 * @return final URL, or null if an error occurs or the maximum number of
	 *         redirects is exceeded
	 */
	public static String getFinalUri(String uri, int maxRedirects) {
		for (int i = 0; i < maxRedirects; i++) {
			HTTPResponse resp = SoftRobotHTTP.get(uri);
			if (resp == null) {
				return null;
			}
			String newUri = resp.getNewURL();
			if (newUri != null) {
				uri = newUri;
			} else {
				return uri;
			}
		}
		return null;
	}

	public static Document getXML(final String uri) {
		Document result = parseXML(get(uri));
		if (result != null && !uri.contains("?")) {
			xmlCache.put(uri, result);
		}
		return result;
	}

	private static Hashtable<String, Document> xmlCache = new Hashtable<String, Document>();

	public static Document getCachedXML(final String uri) {
		if (xmlCache.containsKey(uri)) {
			return xmlCache.get(uri);
		}
		Document result = parseXML(get(uri));
		if (result != null) {
			xmlCache.put(uri, result);
		}
		return result;
	}

	/** executes an HTTP PUT */
	public static HTTPResponse put(final String uri, final String data) {
		return exec("PUT", uri, data);
	}

	/** executes an HTTP POST */
	public static HTTPResponse post(final String uri, final String data) {
		return exec("POST", uri, data);
	}

	public static Document postXML(final String uri, final String data) {
		return parseXML(post(uri, data));
	}

	public static Document parseXML(final HTTPResponse xml) {
		try {
			if (xml == null || xml.responseCode != 200) {
				return null;
			}

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			ByteArrayInputStream bais = new ByteArrayInputStream(xml.getContent().getBytes());
			return builder.parse(bais);

		} catch (ParserConfigurationException e) {
			return null;
		} catch (SAXException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}

	/** executes an HTTP operation */
	private static HTTPResponse exec(final String method, final String uri, final String data) {
		try {
			// connect
			final URL url = new URL(uri);
			final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setInstanceFollowRedirects(false);
			conn.setAllowUserInteraction(false);
			conn.setDefaultUseCaches(false);
			// set method
			conn.setRequestMethod(method);
			// post data
			if (data != null) {
				conn.setDoOutput(true);
				final OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
				osw.write(data);
				osw.close();
			}
			// handle response
			return new HTTPResponse(conn);
		} catch (final IOException ex) {
			// ex.printStackTrace();
			return null;
		}
	}

	/**
	 * A HTTP response
	 */
	public static class HTTPResponse {

		/** response content */
		private final String content;

		/** response code */
		private final int responseCode;

		/** redirect (location header) URL */
		private String newURL;

		/**
		 * Creates a new HTTP response
		 * 
		 * @param conn HTTP connection
		 * @throws IOException if an error occurs
		 */
		public HTTPResponse(final HttpURLConnection conn) throws IOException {
			this.responseCode = conn.getResponseCode();
			if (conn.getHeaderField("Location") != null) {
				this.newURL = new URL(conn.getURL(), conn.getHeaderField("Location")).toString();
			}
			StringBuilder cnt = new StringBuilder();
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(new DoneHandlerInputStream(conn.getInputStream())));
				String line;
				while ((line = br.readLine()) != null) {
					cnt.append(line);
				}
			} catch (IOException e) {
				// ignore in case of HTTP 4XX responses
			} finally {
				if (br != null) {
					br.close();
				}
			}
			content = cnt.toString();
			conn.disconnect();
		}

		/** retrieves the content */
		public String getContent() {
			return content;
		}

		/** retrieves the response code */
		public int getResponseCode() {
			return responseCode;
		}

		/** retrieves the redirect (location header) URL */
		public String getNewURL() {
			return newURL;
		}
	}

	/**
	 * This input stream won't read() after the underlying stream is exhausted.
	 * http://code.google.com/p/android/issues/detail?id=14562
	 */
	private static final class DoneHandlerInputStream extends FilterInputStream {
		private boolean done;

		public DoneHandlerInputStream(InputStream stream) {
			super(stream);
		}

		@Override
		public int read(byte[] bytes, int offset, int count) throws IOException {
			if (!done) {
				int result = super.read(bytes, offset, count);
				if (result != -1) {
					return result;
				}
			}
			done = true;
			return -1;
		}
	}
}
