package com.therandomlabs.randomtweaks.util;

import java.util.HashMap;
import java.util.Map;

public final class RomanNumerals {
	private static final String[] ROMAN_NUMERALS = {
			"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"
	};
	private static final int[] ROMAN_NUMERAL_VALUES = {
			1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1
	};
	private static final Map<Integer, String> cachedRomanNumerals = new HashMap<>();

	private RomanNumerals() {}

	public static String get(int number) {
		if(number == 0) {
			return "Nulla";
		}

		if(cachedRomanNumerals.containsKey(number)) {
			return cachedRomanNumerals.get(number);
		}

		final boolean negative = number < 0;
		if(negative) {
			number = -number;
		}

		final StringBuilder romanNumeral = new StringBuilder();
		for(int i = 0; i < ROMAN_NUMERALS.length; i++) {
			while(number >= ROMAN_NUMERAL_VALUES[i]) {
				//The largest value that can be represented with Roman numerals without special
				//characters
				if(number > 3999) {
					int digits = String.valueOf(number).length();
					final int nested = Integer.parseInt(
							String.valueOf(number).substring(0, 1 + (digits - 4)));

					//In Roman numerals, brackets mean "multiply by 1000"
					romanNumeral.append('(').append(get(nested)).append(')');
					number -= nested * 1000;
				} else {
					romanNumeral.append(ROMAN_NUMERALS[i]);
					number -= ROMAN_NUMERAL_VALUES[i];
				}
			}
		}

		String romanNumeralString = romanNumeral.toString();
		if(negative) {
			romanNumeralString = "-" + romanNumeralString;
		}
		cachedRomanNumerals.put(number, romanNumeralString);

		return romanNumeralString;
	}
}
