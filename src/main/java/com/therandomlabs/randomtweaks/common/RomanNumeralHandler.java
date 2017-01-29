package com.therandomlabs.randomtweaks.common;

import java.util.HashMap;
import java.util.Map;

public final class RomanNumeralHandler {
	private static final String[] ROMAN_NUMERALS = {
			"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"
	};
	private static final int[] ROMAN_NUMERAL_VALUES = {
			1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1
	};
	private static final Map<Integer, String> cachedRomanNumerals = new HashMap<>();

	private RomanNumeralHandler() {}

	//Taken from https://github.com/TheRandomLabs/Roman-Numeral-Generator
	public static String getRomanNumeral(int number) {
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

		String romanNumeral = "";
		for(int i = 0; i < ROMAN_NUMERALS.length; i++) {
			while(number >= ROMAN_NUMERAL_VALUES[i]) {
				//The largest value that can be represented with Roman Numerals without special
				//characters
				if(number > 3999) {
					int digits = String.valueOf(number).length();
					final int nested = Integer.parseInt(
							String.valueOf(number).substring(0, 1 + (digits - 4)));

					//In Roman Numerals, brackets mean "multiply by 1000"
					romanNumeral += "(" + getRomanNumeral(nested) + ")";
					number -= nested * 1000;
				} else {
					romanNumeral += ROMAN_NUMERALS[i];
					number -= ROMAN_NUMERAL_VALUES[i];
				}
			}
		}

		if(negative) {
			romanNumeral = "-" + romanNumeral;
		}

		cachedRomanNumerals.put(number, romanNumeral);

		return romanNumeral;
	}
}
