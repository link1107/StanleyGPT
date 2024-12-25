package com.igorlink.stanleygpt.client.eventhandlers.handlers;

import com.igorlink.stanleygpt.client.StanleyGptClient;
import com.igorlink.stanleygpt.client.eventhandlers.handlers.service.FrequencyAndPriority;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.CustomPayload;

/**
 * Abstract class for handling events.
 */
@Slf4j
public abstract class AbstractEventHandler<T extends CustomPayload> {

    public AbstractEventHandler(CustomPayload.Id<T> id, boolean needScreenShot) {
        ClientPlayNetworking.registerGlobalReceiver(id, (payload, context) -> {
            context.client().execute(() -> {
                FrequencyAndPriority pnq = getPrioirty(payload);

                if (pnq.frequency() < 0 || pnq.frequency() > 100) {
                    throw new IllegalArgumentException("Frequency must be between 0 and 100! Passed value: " +
                            pnq.frequency());
                }

                double commentIntensity = ((double) StanleyGptClient.MOD_SETTINGS.getCommentIntensity() /
                        StanleyGptClient.MOD_SETTINGS.MAX_COMMENTS_INTENSITY);

                if (Math.random() * 100 <= pnq.frequency() * commentIntensity) {
                    StanleyGptClient.COMMENT_MAKER.makeNewComment(pnq.priority(),
                            handleEvent(context.client(), payload),
                            needScreenShot);
                }
            });
        });
    }


    /**
     * Returns the frequency and priority of the event.
     *
     * @param payload the payload of the event
     * @return the frequency and priority of the event
     */
    protected abstract FrequencyAndPriority getPrioirty(T payload);


    /**
     * Handles the event.
     *
     * @param client  the client
     * @param payload the payload of the event
     * @return the comment
     */
    protected abstract String handleEvent(MinecraftClient client, T payload);


}
