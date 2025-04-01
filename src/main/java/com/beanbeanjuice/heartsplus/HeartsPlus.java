package com.beanbeanjuice.heartsplus;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartsPlus implements ModInitializer {

	public static final String MOD_ID = "heartsplus";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello, world!");
	}

}
