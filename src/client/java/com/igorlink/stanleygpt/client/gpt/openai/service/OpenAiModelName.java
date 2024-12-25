package com.igorlink.stanleygpt.client.gpt.openai.service;

/**
 * Represents the name of a GPT model.
 */
public enum OpenAiModelName {
    GPT_4O, GPT_4O_MINI, O1_PREVIEW, O1_MINI;

    /**
     * Returns the display name of the model.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return switch (this) {
            case GPT_4O -> "GPT 4o";
            case GPT_4O_MINI -> "GPT 4o-mini";
            case O1_PREVIEW -> "o1-preview";
            case O1_MINI -> "o1-mini";
        };
    }
}
