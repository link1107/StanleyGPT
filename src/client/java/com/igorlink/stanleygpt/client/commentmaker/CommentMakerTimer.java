package com.igorlink.stanleygpt.client.commentmaker;

import com.igorlink.stanleygpt.client.StanleyGptClient;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Timer for making comments. Launches a scheduler that makes comments at a certain frequency.
 */
@Slf4j
public class CommentMakerTimer {
    // Previous state
    private boolean wasInWorld = false;
    private boolean wasEnabled = false;

    // Initial delay for the scheduler after the timer is started
    private final int INITIAL_DELAY = 2;
    // Default delay for the scheduler
    private final int DEFAULT_DELAY = 40;

    // Scheduler for making comments
    private ScheduledExecutorService makeCommentScheduler;

    public CommentMakerTimer(ICommentMaker commentMaker) {

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            // Current state
            boolean inWorldNow = (MinecraftClient.getInstance().world != null);
            boolean enabledNow = StanleyGptClient.INSTANCE.isEnabled();

            //Check if the state has changed
            boolean wasActive = wasInWorld && wasEnabled;
            boolean isActive = inWorldNow && enabledNow;

            // If the state has changed from inactive to active, start the scheduler
            if (isActive && !wasActive) {
                makeCommentScheduler = Executors.newScheduledThreadPool(1);
                makeCommentScheduler.scheduleAtFixedRate(
                        () -> commentMaker.makeNewComment(0, null, true),
                        INITIAL_DELAY,         // initial delay
                        getAmplifiedDelay(),        // period
                        TimeUnit.SECONDS
                );
            }

            // Otherwise, if the state has changed from active to inactive, stop the scheduler
            else if (!isActive && wasActive) {
                if (makeCommentScheduler != null && !makeCommentScheduler.isShutdown()) {
                    makeCommentScheduler.shutdownNow();
                }
            }

            // Refresh the previous state
            wasInWorld = inWorldNow;
            wasEnabled = enabledNow;
        });
    }

    void reset() {
        log.info("Resetting CommentMakerTimer");

        if (makeCommentScheduler != null && !makeCommentScheduler.isShutdown()) {
            makeCommentScheduler.shutdownNow();
        }

        boolean inWorldNow = (MinecraftClient.getInstance().world != null);
        boolean enabledNow = StanleyGptClient.INSTANCE.isEnabled();

        if (inWorldNow && enabledNow) {
            makeCommentScheduler = Executors.newScheduledThreadPool(1);
            makeCommentScheduler.scheduleAtFixedRate(
                    () -> StanleyGptClient.COMMENT_MAKER.makeNewComment(0, null, true),
                    getAmplifiedDelay(),         // initial delay
                    getAmplifiedDelay(),        // period
                    TimeUnit.SECONDS
            );
        }
    }

    private long getAmplifiedDelay() {
        double commentIntensity = (double) StanleyGptClient.MOD_SETTINGS.getCommentIntensity() /
                StanleyGptClient.MOD_SETTINGS.MAX_COMMENTS_INTENSITY;
        return DEFAULT_DELAY + (DEFAULT_DELAY - (long) commentIntensity * DEFAULT_DELAY);
    }

}
