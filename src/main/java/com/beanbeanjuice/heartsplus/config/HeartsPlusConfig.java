package com.beanbeanjuice.heartsplus.config;

import com.beanbeanjuice.heartsplus.HeartsPlus;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = HeartsPlus.MOD_ID)
public class HeartsPlusConfig implements ConfigData {

    public float kissVolume = 1.0f;
    public float hugVolume = 1.0f;

}
