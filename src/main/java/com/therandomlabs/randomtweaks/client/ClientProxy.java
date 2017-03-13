package com.therandomlabs.randomtweaks.client;

import java.lang.reflect.Field;
import org.lwjgl.input.Keyboard;
import com.therandomlabs.randomtweaks.common.CommonProxy;
import com.therandomlabs.randomtweaks.common.ConfigurationHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLModDisabledEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public final class ClientProxy extends CommonProxy {
	@Override
	public void preInit(FMLPreInitializationEvent event) throws Exception {
		super.preInit(event);

		if(ConfigurationHandler.moveBucketCreativeTab) {
			Items.BUCKET.setCreativeTab(CreativeTabs.TOOLS);
		}

		if(ConfigurationHandler.contributorCapes) {
			CapeHandler.downloadPlayers();
		}
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);

		if(ConfigurationHandler.reloadSoundSystemKeyBind) {
			SoundSystemReloadHandler.registerKeyBinding();
		}
	}
}
