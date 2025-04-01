package com.beanbeanjuice.heartsplus;

import com.beanbeanjuice.heartsplus.config.HeartsPlusConfig;
import com.beanbeanjuice.heartsplus.events.hug.HugClientEvent;
import com.beanbeanjuice.heartsplus.events.kiss.KissClientEvent;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;

public class HeartsPlusClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AutoConfig.register(HeartsPlusConfig.class, JanksonConfigSerializer::new);

        KissClientEvent.registerClient();
        HugClientEvent.registerClient();
    }

}
