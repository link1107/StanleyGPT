package com.igorlink.stanleygpt.client.gpt.openai.service;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents the result of a GPT request.
 */
@AllArgsConstructor
public class OpenAiRequestResult {

    /**
     * The text generated by the GPT model.
     */
    @Getter
    private final String text;

    /**
     * The total price for the request.
     */
    @Getter
    private final double price;


}
