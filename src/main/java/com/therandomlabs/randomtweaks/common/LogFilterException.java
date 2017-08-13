package com.therandomlabs.randomtweaks.common;

@SuppressWarnings("serial")
public class LogFilterException extends Exception {
	public LogFilterException(String message, Object... args) {
		super(String.format(message, args));
	}
}
