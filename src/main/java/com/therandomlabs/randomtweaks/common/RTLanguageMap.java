package com.therandomlabs.randomtweaks.common;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.text.translation.LanguageMap;

public class RTLanguageMap extends LanguageMap {
	public static final RTLanguageMap INSTANCE = new RTLanguageMap();
	private static final String[] ROMAN_NUMERALS = {
			"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"
	};
	private static final int[] ROMAN_NUMERAL_VALUES = {
			1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1
	};
	private static final Map<Integer, String> cachedRomanNumerals = new HashMap<>();
	private static final Map<Field, LanguageMap> languageMaps = new HashMap<>(3);

	@Override
	public synchronized String translateKey(String key) {
		boolean isLevel = false;
		int level = 0;

		if(key.startsWith("enchantment.level.")) {
			final String number = key.substring("enchantment.level.".length());
			try {
				level = Integer.parseInt(number);
				isLevel = true;
			} catch(NumberFormatException ex) {}
		} else if(key.startsWith("potion.potency.")) {
			final String number = key.substring("potion.potency.".length());
			try {
				level = Integer.parseInt(number) + 1;
				isLevel = true;
			} catch(NumberFormatException ex) {}
		}

		return isLevel ? getRomanNumeral(level) : super.translateKey(key);
	}

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

	public static void replaceLanguageMaps() throws Exception {
		if(languageMaps.isEmpty()) {
			final List<Field> fields = new ArrayList<>(3);
			fields.addAll(RandomTweaks.findFields(I18n.class, LanguageMap.class));
			fields.add(RandomTweaks.findField(LanguageMap.class, LanguageMap.class));

			for(Field field : fields) {
				RandomTweaks.makeNotFinal(field);
				languageMaps.put(field, (LanguageMap) field.get(null));
				field.set(null, INSTANCE);
			}
		}

		for(Field field : languageMaps.keySet()) {
			field.set(null, INSTANCE);
		}
	}

	public static void unreplaceLanguageMaps() throws Exception {
		if(!languageMaps.isEmpty()) {
			for(Field field : languageMaps.keySet()) {
				field.set(null, languageMaps.get(field));
			}
		}
	}
}
