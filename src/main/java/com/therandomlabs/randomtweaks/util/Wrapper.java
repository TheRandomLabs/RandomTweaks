package com.therandomlabs.randomtweaks.util;

public class Wrapper<T> {
	private T value;

	public void set(T value) {
		this.value = value;
	}

	public T get() {
		return value;
	}
}
