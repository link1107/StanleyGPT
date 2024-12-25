package com.igorlink.stanleygpt.client.eventhandlers.handlers.service;

/**
 * Represents a pair of frequency and priority values.
 */
public record FrequencyAndPriority(int frequency, int priority) {

    /**
     * Creates a new instance of {@link FrequencyAndPriority}.
     *
     * @param frequency frequency of triggering the event comment
     * @param priority priority of the event, defining can a currently executing comment be interrupted or not
     */
    public FrequencyAndPriority {
        if (priority < 0 || priority > 100) {
            throw new IllegalArgumentException("Priority must be between 0 and 100! Passed value: " + priority);
        }

        if (frequency < 0 || frequency > 100) {
            throw new IllegalArgumentException("Frequency must be between 0 and 100! Passed value: " + frequency);
        }
    }


    /**
     * Creates a new instance of {@link FrequencyAndPriority}. Factory method for better readability.
     *
     * @param frequency frequency of triggering the event comment
     * @param priority priority of the event, defining can a currently executing comment be interrupted or not
     * @return a new instance of {@link FrequencyAndPriority}
     */
    public static FrequencyAndPriority of(int frequency, int priority) {
        return new FrequencyAndPriority(frequency, priority);
    }
}
