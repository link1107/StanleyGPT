package com.igorlink.stanleygpt.client.tts.silero;

public enum SileroEndpoint {
    IGOR_LINK_PREMIUM, SILERO_ORIGIN;

    public String getEndpoint() {
        return switch (this) {
            case IGOR_LINK_PREMIUM -> "https://tts-api.igorlink.com/proxy/silero";
            case SILERO_ORIGIN -> "https://api-tts.silero.ai/voice";
        };
    }

    public String getDisplayName() {
        return switch (this) {
            case IGOR_LINK_PREMIUM -> "ILP";
            case SILERO_ORIGIN -> "Silero";
        };
    }
}
