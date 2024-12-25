package com.igorlink.stanleygpt.client.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.igorlink.stanleygpt.client.Utils;
import com.igorlink.stanleygpt.client.gpt.openai.service.OpenAiModelName;
import com.igorlink.stanleygpt.client.tts.silero.SileroEndpoint;
import com.igorlink.stanleygpt.client.tts.silero.SileroSpeaker;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class ModSettings {
    // Constants
    public final int MAX_COMMENTS_INTENSITY = 10;

    // Jackson object mapper
    private final ObjectMapper mapper = new ObjectMapper();

    // Settings instance
    private ModSettingsStorage settings;

    // Path to the mod folder
    private final String MOD_CONFIG_PATH = getConfigPath();
    // Path to the config file
    private final String MOD_CONFIG_FILE = MOD_CONFIG_PATH + "config.json";


    /**
     * Create a new ModSettings instance.
     */
    public ModSettings() {
        // Enable pretty printing
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            // Load settings
            loadSettings();
        } catch (Exception e) {
            log.error("Failed to load settings", e);
            // Reset settings to default if loading failed
            resetSettingsToDefault();
        }
    }


    /**
     * Get the path to the mod folder
     *
     * @return path to the mod folder
     */
    public String getConfigPath() {
        try {
            // Get the path to the mod jar file
            File file = new File(Utils.class.getProtectionDomain().getCodeSource().getLocation().toURI());

            // Retrieve the file separator for the current OS
            String delimiter = FileSystems.getDefault().getSeparator();

            // Get the path to the game directory
            String gameDirectory = new File(file.getParent()).getParent();

            // Add the delimiter to the end of the path if it is missing
            if (!gameDirectory.endsWith(delimiter)) {
                gameDirectory += delimiter;
            }

            // Return the path to the mod folder
            return gameDirectory + "config" + delimiter + "stanley-gpt" + delimiter;
        } catch (URISyntaxException e) {
            log.error("Failed to get the path to the mod folder: {}", e.getLocalizedMessage(), e);
            throw new RuntimeException(e);
        }
    }


    /**
     * Load settings from the file
     */
    public void loadSettings() {
        // Save default settings if the file does not exist
        saveDefaultSettings();

        // Load settings from the file
        File file = new File(MOD_CONFIG_FILE);
        try {
            this.settings = mapper.readValue(file, ModSettingsStorage.class);
        } catch (IOException e) {
            log.error("Failed to load settings: {}", e.getLocalizedMessage(), e);
            throw new RuntimeException(e);
        }
    }


    /**
     * Save settings to the file
     */
    public void saveSettings() {
        // Create the file
        File file = new File(MOD_CONFIG_FILE);

        // Create the directory if it does not exist and write the settings to the file
        try {
            Files.createDirectories(Path.of(MOD_CONFIG_PATH));
            mapper.writeValue(file, this.settings);
        } catch (IOException e) {
            log.error("Failed to save settings: {}", e.getLocalizedMessage(), e);
            throw new RuntimeException(e);
        }
    }


    /**
     * Reset settings to default
     */
    public void resetSettingsToDefault() {
        // Create a new settings instance and set default values
        this.settings = new ModSettingsStorage();
        saveSettings();
    }


    /**
     * Save default settings to the file
     */
    public void saveDefaultSettings() {
        // Create the file if it does not exist and save the default settings
        if (Files.notExists(Path.of(MOD_CONFIG_FILE))) {
            resetSettingsToDefault();
        }
    }


    public OpenAiModelName getSelectedScreenshotModel() {
        return settings.selectedScreenshotModel;
    }

    public void setSelectedScreenshotModel(OpenAiModelName selectedScreenshotModel) {
        settings.selectedScreenshotModel = selectedScreenshotModel;
    }

    public OpenAiModelName getSelectedCommentModel() {
        return settings.selectedCommentModel;
    }

    public void setSelectedCommentModel(OpenAiModelName selectedCommentModel) {
        settings.selectedCommentModel = selectedCommentModel;
    }

    public SileroSpeaker getSelectedVoice() {
        return settings.selectedVoice;
    }

    public void setSelectedVoice(SileroSpeaker selectedVoice) {
        settings.selectedVoice = selectedVoice;
    }

    public String getOpenaiApiKey() {
        return settings.openAiApiKey;
    }

    public void setOpenaiApiKey(String openaiApiKey) {
        settings.openAiApiKey = openaiApiKey;
    }

    public String getSileroApiKey() {
        return settings.sileroApiKey;
    }

    public void setSileroApiKey(String sileroApiKey) {
        settings.sileroApiKey = sileroApiKey;
    }

    public int getCommentIntensity() {
        return settings.commentIntensity;
    }

    public void setCommentIntensity(int commentIntensity) {
        settings.commentIntensity = commentIntensity;
    }

    public SileroEndpoint getSileroEndpoint() { return settings.sileroEndpoint; }

    public void setSileroEndpoint(SileroEndpoint sileroEndpoint) { settings.sileroEndpoint = sileroEndpoint; }

    /**
     * Represents the settings storage with default values.
     */
    @Data
    private static class ModSettingsStorage {
        @JsonProperty("selected_screenshot_model")
        private OpenAiModelName selectedScreenshotModel = OpenAiModelName.GPT_4O_MINI;

        @JsonProperty("selected_comment_model")
        private OpenAiModelName selectedCommentModel = OpenAiModelName.GPT_4O_MINI;

        @JsonProperty("selected_voice")
        private SileroSpeaker selectedVoice = SileroSpeaker.SPONGEBOB;

        @JsonProperty("comment_intensity")
        private int commentIntensity = 10;

        @JsonProperty("openai_api_key")
        private String openAiApiKey = "";
        @JsonProperty("silero_api_key")
        private String sileroApiKey = "";
        @JsonProperty("silero_endpoint")
        private SileroEndpoint sileroEndpoint = SileroEndpoint.IGOR_LINK_PREMIUM;
    }


}
