package com.igorlink.stanleygpt;

import com.igorlink.stanleygpt.payloads.*;
import com.igorlink.stanleygpt.service.ItemStackDto;
import com.igorlink.stanleygpt.service.PacketRegistrar;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

@Slf4j
public class StanleyGpt implements ModInitializer {

    @Override
    public void onInitialize() {
        PacketRegistrar.registerPackets();
        log.info("StanleyGpt server-part initialized");
    }
}
