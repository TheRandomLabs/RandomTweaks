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

	@Override
	public synchronized String translateKey(String key) {
		boolean isLevel = false;
		int level = 0;

		//Someone might have defined a value for a key like enchantment.level.37
		if(!super.translateKey(key).equals(key)) {
			return super.translateKey(key);
		}

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

		return isLevel ? RomanNumeralHandler.getRomanNumeral(level) : super.translateKey(key);
	}

	public static void replaceLanguageMaps() {
		I18n.localizedName = INSTANCE;
		I18n.fallbackTranslator = INSTANCE;
		LanguageMap.instance = INSTANCE;
	}
}
