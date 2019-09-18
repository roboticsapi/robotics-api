/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.visualization;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RapiInfo {

	public static void main(String[] args) {
		String message = RapiInfo.serialize(
				new RapiInfo[] { new RapiInfo("rmi", 111, "Rmi-Server"), new RapiInfo("tcp", 222, "Tcp-Server") });
		System.out.println("Nachricht: " + message);
		System.out.println();

		for (RapiInfo s : RapiInfo.fromText(message)) {
			System.out.println("Typ:       " + s.type);
			System.out.println("Port:      " + s.port);
			System.out.println("Name:      " + s.name);
			System.out.println("Age:       " + s.age);
			System.out.println();
		}
	}

	public final String type;
	public final int port;
	public final String name;
	public final long age;

	public RapiInfo(String type, int port, String name) {
		this(type, port, name, -1);
	}

	public RapiInfo(String type, int port, String name, long age) {
		this.type = type;
		this.port = port;
		this.name = name;
		this.age = age;
	}

	public RapiInfo withNewAge(long age) {
		return new RapiInfo(type, port, name, age);
	}

	private String serialize() {
		StringBuilder sb2 = new StringBuilder();
		sb2.append(mask(type)).append(":").append(mask("" + port)).append(":").append(mask(name)).append(":")
				.append(mask("" + age));
		return sb2.toString();
	}

	public static String serialize(RapiInfo[] rapiServer) {
		StringBuilder result = new StringBuilder();
		boolean first = true;
		for (RapiInfo server : rapiServer) {
			result.append(first ? "" : ":");
			first = false;
			result.append(mask(server.serialize()));
		}
		return result.toString();
	}

	private static String mask(String text) {
		return text.replaceAll("\\\\", "\\\\\\\\").replaceAll(":", "\\\\:");
	}

	public static final RapiInfo[] fromText(String message) {
		Set<RapiInfo> result = new HashSet<>();
		for (String entry : parse(message)) {
			String[] split = parse(entry);
			if (split.length != 4)
				throw new IllegalArgumentException();
			result.add(new RapiInfo(split[0], Integer.valueOf(split[1]), split[2], Long.valueOf(split[3])));
		}
		return result.toArray(new RapiInfo[result.size()]);
	}

	private static final String[] parse(String message) {
		List<String> result = new ArrayList<>();
		String last = "";
		boolean mask = false;
		for (char c : message.toCharArray()) {
			if (mask) {
				if (c == '\\')
					last = last + c;
				else if (c == ':')
					last = last + c;
				else
					throw new IllegalArgumentException();
				mask = false;
			} else {
				if (c == ':') {
					result.add(last);
					last = "";
				} else if (c == '\\')
					mask = true;
				else
					last = last + c;
			}
		}
		if (result.size() == 0 && last.equals(""))
			return new String[0];
		result.add(last);
		return result.toArray(new String[result.size()]);
	}

	public boolean describesSameServer(RapiInfo other) {
		return type.equals(other.type) && port == other.port;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof RapiInfo))
			return false;
		RapiInfo other = (RapiInfo) obj;
		return describesSameServer(other) && name.equals(other.name) && age == other.age;
	}

	@Override
	public String toString() {
		return "Server [type:" + type + ", name:" + name + ", port:" + port + ", age:" + age + "]";
	}

}
