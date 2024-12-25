package com.igorlink.stanleygpt.client.gpt.openai;

import com.igorlink.stanleygpt.client.exceptions.FailedStateException;
import com.igorlink.stanleygpt.client.exceptions.GptClientInitException;
import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.chat.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import com.igorlink.stanleygpt.client.gpt.openai.service.OpenAiModelName;
import com.igorlink.stanleygpt.client.gpt.openai.service.OpenAiRequestResult;

import static com.igorlink.stanleygpt.client.StanleyGptClient.GPT_CLIENT;
import static com.igorlink.stanleygpt.client.StanleyGptClient.MOD_SETTINGS;
import static com.igorlink.stanleygpt.client.Utils.convertJpgToBase64;


@Slf4j
public class OpenAiApiClient {

    private OpenAiClient client;

    @Getter
    private boolean enabled = false;

    /**
     * Initialize the OpenAI client.
     *
     * @param apiKey OpenAI API key
     */
    public void init(String apiKey) {
        updateApiKey(apiKey);
    }


    /**
     * Update the OpenAI API key.
     *
     * @param apiKey OpenAI API key
     * @throws GptClientInitException if the API key is invalid
     */
    public void updateApiKey(String apiKey) throws GptClientInitException {
        try {
            OpenAiClient tmpClient = OpenAiClient.builder()
                    .openAiApiKey(apiKey)
                    .build();

            testApiConnection(tmpClient);

            this.client = tmpClient;
            this.enabled = true;
        } catch (Exception e) {
            log.warn("Failed to connect to OpenAI: {}", e.getLocalizedMessage(), e);

            if (e.getMessage() != null && e.getMessage().contains("unsupported_country_region_territory")) {
                throw new FailedStateException();
            }

            throw new GptClientInitException(e.getLocalizedMessage());
        }
    }


    /**
     * Get completion for text.
     *
     * @param openAiModelName gpt model name
     * @param messages user and assistant messages
     * @return completion result
     */
    public OpenAiRequestResult getTextCompletion(OpenAiModelName openAiModelName, List<Message> messages) {
        return getTextCompletion(openAiModelName, messages, this.client);
    }


    /**
     * Get completion for text.
     *
     * @param openAiModelName gpt model name
     * @param messages user and assistant messages
     * @param client OpenAI client
     * @return completion result
     */
    private OpenAiRequestResult getTextCompletion(OpenAiModelName openAiModelName, List<Message> messages, OpenAiClient client) {
        // Getting the GPT model
        OpenAiModel openAiModel = OpenAiModelLibrary.getModel(openAiModelName);

        // Calculating the cost of the request
        double price = 0;
        for (Message message : messages) {
            if (message instanceof UserMessage) {
                price += openAiModel.getPriceForInputText(((UserMessage) message).content().toString());
            } else {
                price += openAiModel.getPriceForInputText(((AssistantMessage) message).content());
            }
        }

        // Sending a text-completion request to the API
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(openAiModel.MODEL_ID)
                .messages(messages)
                .build();

        // Getting the response
        ChatCompletionResponse response = client.chatCompletion(request).execute();

        // Calculating the cost of the response
        price += openAiModel.getPriceForOutputText(response.content());

        return new OpenAiRequestResult(response.content(), price);
    }


    /**
     * Get completion for image.
     *
     * @param openAiModelName gpt model name
     * @param bufferedImage image
     * @param text text
     * @return completion result
     */
    public OpenAiRequestResult getImageVisionCompletion(OpenAiModelName openAiModelName, BufferedImage bufferedImage, String text) {
        // Getting the GPT model
        OpenAiModel openAiModel = OpenAiModelLibrary.getModel(openAiModelName);

        // Calculating the cost of the request
        double price = openAiModel.getPriceForImage(bufferedImage.getWidth(), bufferedImage.getHeight());
        price += openAiModel.getPriceForInputText(text);

        // Sending a vision-completion request to the API
        List<Content> contentList = new ArrayList<>();

        // Adding image content
        Content imageContent = Content.builder()
                .imageUrl(ImageUrl.builder()
                        .url("data:image/jpeg;base64," + convertJpgToBase64(bufferedImage))
                        .build())
                .type(ContentType.IMAGE_URL)
                .build();
        contentList.add(imageContent);

        // Adding text content
        Content textContent = Content.builder()
                .text(text)
                .type(ContentType.TEXT)
                .build();
        contentList.add(textContent);

        // Creating a user message with an image and text
        UserMessage userMessage = UserMessage.builder()
                .content(contentList)
                .build();

        // Creating a vision-completion request
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(openAiModel.MODEL_ID)
                .messages(userMessage)
                .build();

        // Sending the request and getting the response
        ChatCompletionResponse response = client.chatCompletion(request).execute();

        // Calculating the cost of the response
        price += openAiModel.getPriceForOutputText(response.content());

        return new OpenAiRequestResult(response.content(), price);
    }


    /**
     * Test the API connection.
     *
     * @param testClient OpenAI client
     */
    private void testApiConnection(OpenAiClient testClient) {
            UserMessage userMessage = UserMessage.builder()
                    .content(List.of(Content.builder()
                            .type(ContentType.TEXT)
                            .text("Пожалуйста, ответь \"Test\"")
                            .build()))
                    .build();

            GPT_CLIENT.getTextCompletion(MOD_SETTINGS.getSelectedCommentModel(), List.of(userMessage), testClient);
    }


}
