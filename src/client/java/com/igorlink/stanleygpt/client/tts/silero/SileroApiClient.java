package com.igorlink.stanleygpt.client.tts.silero;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.igorlink.stanleygpt.client.exceptions.TtsClientInitException;
import com.igorlink.stanleygpt.client.tts.silero.dto.SileroDto;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.Base64;

@Slf4j
public class SileroApiClient {

    // Create an OkHttpClient
    private final OkHttpClient client = new OkHttpClient();

    // API key
    private String apiKey;

    private SileroEndpoint endpoint;

    @Getter
    private boolean enabled = false;

    private final ObjectMapper mapper = new ObjectMapper();


    /**
     * Create a new Silero API client.
     *
     * @param apiKey API key
     */
    public void init(SileroEndpoint endpoint, String apiKey) {
        updateApiKeyAndEndpoint(endpoint, apiKey);
    }


    /**
     * Update the API key.
     *
     * @param apiKey new API key
     * @throws TtsClientInitException if the API key is invalid
     */
    public void updateApiKeyAndEndpoint(SileroEndpoint endpoint, String apiKey) throws TtsClientInitException {
        // Check if the API key is empty
        if (apiKey.isBlank()) {
            throw new TtsClientInitException("API key is empty!");
        }

        // Test the API connection
        try {
            testApiConnection(endpoint, apiKey);
            this.apiKey = apiKey;
            this.endpoint = endpoint;
            this.enabled = true;
        } catch (Exception e) {
            log.error("Failed to connect to SileroTTS: {}", e.getLocalizedMessage(), e);

            throw new TtsClientInitException(e.getLocalizedMessage());
        }
    }


    /**
     * Get the Ogg audio data for the specified text.
     *
     * @param text    text to convert to speech
     * @param speaker speaker
     * @return OGG audio data in bytes array
     */
    public byte[] getOggForText(String text, SileroSpeaker speaker) {
        return getOggForText(text, speaker, this.endpoint, this.apiKey);
    }


    /**
     * Get the Ogg audio data for the specified text.
     *
     * @param text    text to convert to speech
     * @param speaker speaker
     * @param apiKey  API key
     * @return OGG audio data in bytes array
     * @throws RuntimeException if an error occurs
     */
    private byte[] getOggForText(String text,
                                 SileroSpeaker speaker,
                                 SileroEndpoint endpoint,
                                 String apiKey) throws RuntimeException {
        SileroDto sileroDto = new SileroDto(apiKey,
                text,
                48000,
                speaker.name().toLowerCase(),
                "ru",
                "ogg",
                false,
                false);

        // Create a request body
        RequestBody requestBody;
        try {
            requestBody = RequestBody.create(
                    mapper.writeValueAsString(sileroDto),
                    MediaType.parse("application/json; charset=utf-8")
            );
        } catch (IOException e) {
            log.error("Failed to create request body: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }

        // Create a request
        Request request = new Request.Builder()
                .url(endpoint.getEndpoint())
                .post(requestBody)
                .build();

        // Initialize the audio data array
        byte[] audioData;

        // Send the request and handle the response
        try (Response response = client.newCall(request).execute()) {

            // Check if the response is successful
            if (response.isSuccessful() && response.body() != null) {

                // Get the response body
                String jsonResponse;
                try {
                    jsonResponse = response.body().string();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                // Parse the response body to get the audio data
                audioData = parseAudioFromResponse(jsonResponse);
            } else {
                // Handle the error response
                String errorResponse = response.body() == null ? "No response body" : handleErrorResponse(response.body().string());
                throw new RuntimeException(errorResponse);
            }
        } catch (IOException e) {
            log.error("IOException request error: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }

        log.info("Voice-file received! File size: {}", audioData.length);

        return audioData;
    }


    /**
     * Parse the audio data from the JSON response.
     *
     * @param response JSON response
     * @return audio data
     */
    private byte[] parseAudioFromResponse(String response) {
        // Parse the JSON response
        JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();

        // Extract the audio data from the response
        JsonArray results = jsonResponse.getAsJsonArray("results");
        if (!results.isEmpty()) {
            String audioBase64 = results.get(0).getAsJsonObject().get("audio").getAsString();

            // Decode the base64 audio data
            return Base64.getDecoder().decode(audioBase64);
        } else {
            throw new IllegalStateException("Audio data not found in the response!");
        }
    }


    /**
     * Handle the error response.
     *
     * @param errorResponse error response
     * @return error message
     */
    private String handleErrorResponse(String errorResponse) {
        // Parse the JSON error response
        try {
            JsonObject errorJson = JsonParser.parseString(errorResponse).getAsJsonObject();
            JsonArray details = errorJson.getAsJsonArray("detail");

            String errorMessage = "";
            int i = 1;
            // Extract the error details
            for (JsonElement detail : details) {
                JsonObject detailObject = detail.getAsJsonObject();
                errorMessage = "{Error #" + i + ": loc: " + detailObject.get("loc") + ", msg: " + detailObject.get("msg").getAsString() + ", type: " + detailObject.get("type").getAsString() + "}";
                i++;
            }

            // Check if the error message is empty
            if (errorMessage.isBlank()) {
                throw new RuntimeException("Unknown error!");
            }

            return errorMessage;
        } catch (Exception e) {
            log.error("Error while parsing error response: {}", errorResponse, e);
            return "Unknown error!";
        }
    }


    /**
     * Test the API connection. If no exception is thrown, the connection is successful.
     *
     * @param testApiKey test API key
     * @throws RuntimeException if an error occurs
     */
    private void testApiConnection(SileroEndpoint endpoint, String testApiKey) throws RuntimeException {
        getOggForText("тест", SileroSpeaker.GMAN, endpoint, testApiKey);
    }


}
