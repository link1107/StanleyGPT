package com.igorlink.stanleygpt.client;

import com.igorlink.stanleygpt.client.commentmaker.CommentMaker;
import com.igorlink.stanleygpt.client.eventhandlers.EventHandlerManager;
import com.igorlink.stanleygpt.client.exceptions.FailedStateException;
import com.igorlink.stanleygpt.client.gpt.openai.OpenAiApiClient;
import com.igorlink.stanleygpt.client.gui.buttons.ButtonHandler;
import com.igorlink.stanleygpt.client.settings.ModSettings;
import com.igorlink.stanleygpt.client.tts.silero.SileroApiClient;
import net.fabricmc.api.ClientModInitializer;

/**
 * The main class of the client.
 */
public class StanleyGptClient implements ClientModInitializer {

    /**
     * The instance of the mod.
     */
    public static StanleyGptClient INSTANCE;

    /**
     * The settings of the client.
     */
    public static final ModSettings MOD_SETTINGS = new ModSettings();;

    /**
     * The TTS client.
     */
    public static final SileroApiClient TTS_CLIENT = new SileroApiClient();

    /**
     * The GPT client.
     */
    public static final OpenAiApiClient GPT_CLIENT = new OpenAiApiClient();

    /**
     * The comment maker.
     */
    public static final CommentMaker COMMENT_MAKER = new CommentMaker();


    /**
     * Initializes the client.
     */
    @Override
    public void onInitializeClient() {
        // Initialize the client
        INSTANCE = this;

        // Initialize the settings
        try {
            GPT_CLIENT.init(MOD_SETTINGS.getOpenaiApiKey());
            TTS_CLIENT.init(MOD_SETTINGS.getSileroEndpoint(), MOD_SETTINGS.getSileroApiKey());
        } catch (FailedStateException e) {
            new OnLaunchWarning(OnLaunchWarning.WarningType.FAILED_STATE_ERROR);
        } catch (Exception e) {
            new OnLaunchWarning(OnLaunchWarning.WarningType.UNKNOWN_ERROR);
        }

        // Initialize the event handlers
        new EventHandlerManager();

        // Initialize the comment maker
        new ButtonHandler(COMMENT_MAKER);
    }


    /**
     * Returns whether the client is enabled.
     *
     * @return whether the client is enabled
     */
    public boolean isEnabled() {
        return TTS_CLIENT.isEnabled() && GPT_CLIENT.isEnabled();
    }
}
