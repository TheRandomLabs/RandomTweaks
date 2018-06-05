package com.therandomlabs.randomtweaks.common;

import java.lang.reflect.Field;
import java.util.IllegalFormatException;
import java.util.Map;
import com.therandomlabs.randomtweaks.util.RomanNumerals;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class RTLanguageMap extends LanguageMap {
	public static final Field LANGUAGE_LIST =
			ReflectionHelper.findField(LanguageMap.class, "languageList", "field_74816_c");

	public final Map<String, String> languageList;

	@SuppressWarnings("unchecked")
	public RTLanguageMap(LanguageMap languageMap) throws Exception {
		languageList = (Map<String, String>) LANGUAGE_LIST.get(languageMap);
		LANGUAGE_LIST.set(this, languageList);
	}

	@Override
	public synchronized String translateKey(String key) {
		if(languageList.get(key) != null) {
			return languageList.get(key);
		}

		boolean isLevel = false;
		int level = 0;

		try {
			if(key.startsWith("enchantment.level.")) {
				final String number = key.substring("enchantment.level.".length());
				level = Integer.parseInt(number);
				isLevel = true;
			} else if(key.startsWith("potion.potency.")) {
				final String number = key.substring("potion.potency.".length());
				level = Integer.parseInt(number) + 1;
				isLevel = true;
			}
		} catch(NumberFormatException ignored) {}

		return isLevel ? RomanNumerals.get(level) : key;
	}

	@Override
	public synchronized String translateKeyFormat(String key, Object... args) {
		try {
			return String.format(translateKey(key), args);
		} catch(IllegalFormatException ex) {
			return "Format error: " + key;
		}
	}

	@Override
	public synchronized boolean isKeyTranslated(String key) {
		return languageList.containsKey(key);
	}

	@SuppressWarnings("deprecation")
	public static void replaceLanguageMaps() throws Exception {
		RTLanguageMap instance = new RTLanguageMap(I18n.localizedName);
		I18n.localizedName = instance;
		LanguageMap.instance = instance;
	}
}
