package com.therandomlabs.randomtweaks.common;

import java.lang.reflect.Field;
import java.util.Map;
import com.therandomlabs.randomtweaks.util.RomanNumeralHandler;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class RTLanguageMap extends LanguageMap {
	private static RTLanguageMap instance;
	public final Map<String, String> languageList;

	public RTLanguageMap(LanguageMap languageMap)
			throws IllegalArgumentException, IllegalAccessException {
		final Field languageListField =
				ReflectionHelper.findField(LanguageMap.class, "languageList", "field_74816_c");
		languageListField.setAccessible(true);
		languageList = (Map<String, String>) languageListField.get(languageMap);
	}

	private RTLanguageMap(boolean failed) {
		languageList = null;
	}

	@Override
	public synchronized String translateKey(String key) {
		if(languageList.get(key) != null) {
			return languageList.get(key);
		}

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

		return isLevel ? RomanNumeralHandler.getRomanNumeral(level) : key;
	}

	public static void replaceLanguageMaps() throws Exception {
		instance = new RTLanguageMap(I18n.localizedName);
		I18n.localizedName = instance;
		I18n.fallbackTranslator = instance;
		LanguageMap.instance = instance;
	}
}
