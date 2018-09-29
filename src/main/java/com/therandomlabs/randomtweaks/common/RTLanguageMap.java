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

		if(key.startsWith("enchantment.level.")) {
			try {
				level = Integer.parseInt(key.substring(18));
				isLevel = true;
			} catch(NumberFormatException ignored) {}
		} else if(key.startsWith("potion.potency.")) {
			try {
				level = Integer.parseInt(key.substring(15));
				isLevel = true;
			} catch(NumberFormatException ignored) {}
		}

		return isLevel ? RomanNumerals.get(level) : key;
	}

	@SuppressWarnings("deprecation")
	public static void replaceLanguageMaps() {
		I18n.localizedName = INSTANCE;
		LanguageMap.instance = INSTANCE;
	}
}
