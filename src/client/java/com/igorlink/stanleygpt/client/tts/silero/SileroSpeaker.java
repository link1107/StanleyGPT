package com.igorlink.stanleygpt.client.tts.silero;

/**
 * Represents a Silero speaker.
 */
public enum SileroSpeaker {
    GMAN, SQUIDWARD, SPONGEBOB;

    /**
     * Gets the display name of the speaker.
     *
     * @param speaker The speaker.
     * @return The display name of the speaker.
     */
    public static String getSpeakerDisplayName(SileroSpeaker speaker) {
        return switch (speaker) {
            case GMAN -> "G-MAN";
            case SQUIDWARD -> "Squidward";
            case SPONGEBOB -> "SpongeBob";
        };
    }
}
