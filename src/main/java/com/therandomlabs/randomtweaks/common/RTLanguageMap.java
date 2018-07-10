package com.therandomlabs.randomtweaks.common;

import com.therandomlabs.randomtweaks.RTConfig;
import com.therandomlabs.randomtweaks.util.RomanNumerals;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.text.translation.LanguageMap;

public class RTLanguageMap extends LanguageMap {
	@SuppressWarnings("deprecation")
	public static final RTLanguageMap INSTANCE = new RTLanguageMap(I18n.localizedName);

	private RTLanguageMap(LanguageMap languageMap) {
		this.languageList = languageMap.languageList;
	}

	@Override
	public synchronized String translateKey(String key) {
		if(languageList.get(key) != null) {
			return languageList.get(key);
		}

		if(!RTConfig.misc.moreRomanNumerals) {
			return key;
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
		I18n.localizedName = INSTANCE;
		LanguageMap.instance = INSTANCE;
	}
}
