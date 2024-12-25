package com.igorlink.stanleygpt.client.gpt.openai;

import com.igorlink.stanleygpt.client.gpt.openai.service.OpenAiModelName;
import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.EncodingType;

/**
 * Class for GPT model.
 */
public class OpenAiModel {
    /**
     * Represents the name of a GPT model.
     */
    public final OpenAiModelName MODEL_NAME;

    /**
     * The id of the model for the API.
     */
    public final String MODEL_ID;


    // Price per million input tokens
    private final double PRICE_PER_MILLION_INPUT_TOKENS_USD;
    // Price per million output tokens
    private final double PRICE_PER_MILLION_OUTPUT_TOKENS_USD;
    // Maximum context length in tokens
    private final int MAX_CONTEXT_LENGTH_IN_TOKENS;

    // Vision properties
    private final OpenAiVisionProperties visionProperties;

    // Encoding for the token encoding
    private final Encoding encoding;

    /**
     * Constructor for GptModel.
     *
     * @param modelName display name of the model
     * @param modelId id of the model for the API
     * @param tokenEncoding encoding of the tokens
     * @param pricePerMillionInputTokensUsd price per million input tokens
     * @param pricePerMillionOutputTokensUsd price per million output tokens
     * @param maxContextLengthInTokens maximum context length in tokens
     * @param visionProperties properties for vision requests
     */
    public OpenAiModel(OpenAiModelName modelName, String modelId, EncodingType tokenEncoding, double pricePerMillionInputTokensUsd, double pricePerMillionOutputTokensUsd, int maxContextLengthInTokens, OpenAiVisionProperties visionProperties) {
        this.MODEL_NAME = modelName;
        this.MODEL_ID = modelId;
        this.PRICE_PER_MILLION_INPUT_TOKENS_USD = pricePerMillionInputTokensUsd;
        this.PRICE_PER_MILLION_OUTPUT_TOKENS_USD = pricePerMillionOutputTokensUsd;
        this.MAX_CONTEXT_LENGTH_IN_TOKENS = maxContextLengthInTokens;
        this.visionProperties = visionProperties;

        // Get the encoding for the token encoding to count tokens in strings
        EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
        this.encoding = registry.getEncoding(tokenEncoding);
    }

    /**
     * Get the price for the input text.
     *
     * @param text input text
     * @return price for the input text
     */
    public double getPriceForInputText(String text) {
        return PRICE_PER_MILLION_INPUT_TOKENS_USD * encoding.countTokens(text) / 1_000_000D;
    }

    /**
     * Get the price for the image.
     *
     * @param imageWidth image width
     * @param imageHeight image height
     * @return price for the image
     */
    public double getPriceForImage(int imageWidth, int imageHeight) {
        return visionProperties.getPriceForImage(imageWidth, imageHeight, PRICE_PER_MILLION_INPUT_TOKENS_USD);
    }

    /**
     * Get the price for the output text.
     *
     * @param text output text
     * @return price for the output text
     */
    public double getPriceForOutputText(String text) {
        return PRICE_PER_MILLION_OUTPUT_TOKENS_USD * encoding.countTokens(text) / 1_000_000D;
    }

    public boolean visionAvailable() {
        return this.visionProperties  != null;
    }
}

