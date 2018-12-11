package com.therandomlabs.randomtweaks.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class RomanNumerals {
	private static final String[] ROMAN_NUMERALS = {
			"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"
	};

	private static final int[] ROMAN_NUMERAL_VALUES = {
			1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1
	};

	private static final Map<Integer, String> cache = new ConcurrentHashMap<>();

	private RomanNumerals() {}

	public static String get(int number) {
		if(number == 0) {
			return "Nulla";
		}

		final String cached = cache.get(number);

		if(cached != null) {
			return cached;
		}

		final boolean negative = number < 0;

		if(negative) {
			number = -number;
		}

		final StringBuilder builder = new StringBuilder();

		for(int i = 0; i < ROMAN_NUMERALS.length; i++) {
			while(number >= ROMAN_NUMERAL_VALUES[i]) {
				//The largest value that can be represented with Roman numerals without special
				//characters
				if(number > 3999) {
					final String string = String.valueOf(number);
					final int nested =
							Integer.parseInt(string.substring(0, 1 + (string.length() - 4)));

					//In Roman numerals, brackets mean "multiply by 1000"
					builder.append('(').append(get(nested)).append(')');
					number -= nested * 1000;
				} else {
					builder.append(ROMAN_NUMERALS[i]);
					number -= ROMAN_NUMERAL_VALUES[i];
				}
			}
		}

		String romanNumeral = builder.toString();

		if(negative) {
			romanNumeral = "-" + romanNumeral;
		}

		cache.put(number, romanNumeral);
		return romanNumeral;
	}
}
