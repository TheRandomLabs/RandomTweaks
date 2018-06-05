package com.therandomlabs.randomtweaks.common;

import com.therandomlabs.randomtweaks.util.RomanNumerals;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.text.translation.LanguageMap;

public class RTLanguageMap extends LanguageMap {
	public RTLanguageMap(LanguageMap languageMap) {
		this.languageList = languageMap.languageList;
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

	@SuppressWarnings("deprecation")
	public static void replaceLanguageMaps() {
		final RTLanguageMap instance = new RTLanguageMap(I18n.localizedName);
		I18n.localizedName = instance;
		LanguageMap.instance = instance;
	}
}
