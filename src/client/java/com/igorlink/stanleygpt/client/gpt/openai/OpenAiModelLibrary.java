package com.igorlink.stanleygpt.client.gpt.openai;

import com.knuddels.jtokkit.api.EncodingType;

import com.igorlink.stanleygpt.client.gpt.openai.service.OpenAiModelName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Represents a library of GPT models.
 */
public class OpenAiModelLibrary {
    // Map of GPT models by name
    private static final HashMap<OpenAiModelName, OpenAiModel> modelMap;

    static {
        // List of GPT models
        List<OpenAiModel> modelList = new ArrayList<>();

        // Add GPT models
        modelList.add(
                new OpenAiModel(
                        OpenAiModelName.GPT_4O,
                        "gpt-4o",
                        EncodingType.CL100K_BASE,
                        2.50,
                        10.00,
                        128_000,
                        new OpenAiVisionProperties(85, 170)
                ));

        modelList.add(
                new OpenAiModel(
                        OpenAiModelName.GPT_4O_MINI,
                        "gpt-4o-mini",
                        EncodingType.CL100K_BASE,
                        0.150,
                        0.600,
                        128_000,
                        new OpenAiVisionProperties(2833, 5667)
                ));

        modelList.add(
                new OpenAiModel(
                        OpenAiModelName.O1_PREVIEW,
                        "o1-preview",
                        EncodingType.CL100K_BASE,
                        15.00,
                        60.00,
                        200_000,
                        new OpenAiVisionProperties(75, 150)
                ));

        modelList.add(
                new OpenAiModel(
                        OpenAiModelName.O1_MINI,
                        "o1-mini",
                        EncodingType.CL100K_BASE,
                        3.00,
                        12.00,
                        128_000,
                        null
                ));

        // Create map of models to easily get a model by name
        modelMap = new HashMap<>();
        modelList.forEach(model -> modelMap.put(model.MODEL_NAME, model));
    }


    /**
     * Get a GPT model by name.
     *
     * @param modelName name of the model
     * @return GPT model
     */
    public static OpenAiModel getModel(OpenAiModelName modelName) {
        // Check if the model exists
        if (!modelMap.containsKey(modelName)) {
            throw new IllegalArgumentException("Model not found: " + modelName);
        }

        return modelMap.get(modelName);
    }


}
