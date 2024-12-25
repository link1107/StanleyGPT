package com.igorlink.stanleygpt.client.gpt.openai;

import lombok.AllArgsConstructor;

/**
 * Represents the properties for vision requests.
 */
@AllArgsConstructor
public class OpenAiVisionProperties {
    // Base tokens for vision requests
    private final int VISION_BASE_TOKENS;
    // Dimension pixel size per tile for vision requests
    private final int VISION_TILE_SIZE = 512;
    // Tokens per tile for vision requests
    private final int VISION_TOKENS_PER_TILE;


    /**
     * Get the price for a vision request.
     *
     * @param imageWidth image width
     * @param imageHeight image height
     * @param pricePerMillionInputTokens price per million input tokens
     * @return price for the vision request
     */
    double getPriceForImage(int imageWidth, int imageHeight, double pricePerMillionInputTokens) {
        // Get the number of tiles in the image
        int tilesX = (int) Math.ceil((double) imageWidth / VISION_TILE_SIZE);
        int tilesY = (int) Math.ceil((double) imageHeight / VISION_TILE_SIZE);

        // Get the total number of tiles
        int totalTiles = tilesX * tilesY;

        // Get the total number of tokens
        int totalTokens = VISION_BASE_TOKENS + (VISION_TOKENS_PER_TILE * totalTiles);

        // Calculate the price
        return (totalTokens / 1_000_000D) * pricePerMillionInputTokens;
    }
}
