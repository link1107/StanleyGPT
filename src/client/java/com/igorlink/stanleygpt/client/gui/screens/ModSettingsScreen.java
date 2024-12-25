package com.igorlink.stanleygpt.client.gui.screens;

import com.igorlink.stanleygpt.client.StanleyGptClient;
import com.igorlink.stanleygpt.client.Utils;
import com.igorlink.stanleygpt.client.exceptions.FailedStateException;
import com.igorlink.stanleygpt.client.exceptions.GptClientInitException;
import com.igorlink.stanleygpt.client.exceptions.TtsClientInitException;
import com.igorlink.stanleygpt.client.gpt.openai.OpenAiModelLibrary;
import com.igorlink.stanleygpt.client.gpt.openai.service.OpenAiModelName;
import com.igorlink.stanleygpt.client.gui.widgets.MaskedTextFieldWidget;
import com.igorlink.stanleygpt.client.tts.silero.SileroEndpoint;
import com.igorlink.stanleygpt.client.tts.silero.SileroSpeaker;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.List;

import static com.igorlink.stanleygpt.client.StanleyGptClient.*;

/**
 * The screen for the mod settings.
 */
@Slf4j
public class ModSettingsScreen extends Screen {
    private final Screen parent;

    private MaskedTextFieldWidget openAiKeyField;
    private MaskedTextFieldWidget sileroKeyField;

    private final List<OpenAiModelName> gptTextModelsList = Arrays.asList(OpenAiModelName.values());
    private final List<OpenAiModelName> gptVisionModelsList = Arrays.asList(
            Arrays.stream(OpenAiModelName.values())
                    .filter(
                            (model) -> OpenAiModelLibrary.getModel(model).visionAvailable()
                    ).toArray(OpenAiModelName[]::new));

    private final List<SileroEndpoint> sileroEndpoints = Arrays.asList(SileroEndpoint.values());

    private OpenAiModelName selectedScreenshotModel;
    private OpenAiModelName selectedCommentModel;
    private int commentIntensity;

    // API-keys
    private String openAiKeyFieldContent;
    private String sileroKeyFieldContent;

    // Selected endpoint for tts
    private SileroEndpoint selectedSileroEndpoint;

    // List of available voices
    private final List<SileroSpeaker> sileroSpeakerList = Arrays.asList(SileroSpeaker.values());

    // Selected voice
    private SileroSpeaker selectedVoice;

    public ModSettingsScreen(Screen parent) {
        super(Text.of("Настройки StanleyGPT"));
        this.parent = parent;
        loadSettings();
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int screenHeight = this.height;

        int currentY = 35;

        // Comment frequency slider
        SliderWidget frequencySlider = new SliderWidget(centerX - 100,
                currentY,
                200,
                20,
                Text.of("Частота комментариев: " + commentIntensity),
                Math.max(0, Math.min((double) (commentIntensity - 1) / (MOD_SETTINGS.MAX_COMMENTS_INTENSITY - 1), 1))) {

            @Override
            protected void updateMessage() {
                // Переводим "пропорцию" в реальное значение
                int tmpMaxCommentsPerMinute = commentIntensity;

                setMessage(Text.of("Частота комментариев: " + tmpMaxCommentsPerMinute));
            }

            @Override
            protected void applyValue() {
                double snappedValue = snapToSteps(this.value);
                this.value = snappedValue;

                commentIntensity = (int) Math.round((1 + (MOD_SETTINGS.MAX_COMMENTS_INTENSITY - 1) * snappedValue));
            }

            private double snapToSteps(double input) {
                // Number of steps
                int steps = MOD_SETTINGS.MAX_COMMENTS_INTENSITY;
                double stepSize = 1.0 / (steps - 1);

                double halfOfStep = stepSize / 2D;

                if (input < halfOfStep) {
                    return 0;
                }

                if (input > 1 - halfOfStep) {
                    return 1;
                }

                return Math.round(input / stepSize) * stepSize;
            }
        };
        this.addDrawableChild(frequencySlider);

        // Label for OpenAI Key field
        TextWidget openAiKeyFieldLabel = new TextWidget(
                centerX - 100,
                currentY = currentY + 25,
                200,
                20,
                Text.of("API-ключ для OpenAI:"),
                textRenderer);
        this.addDrawableChild(openAiKeyFieldLabel);

        // Input field for OpenAI Key
        this.openAiKeyField = new MaskedTextFieldWidget(
                this.textRenderer,
                centerX - 100,
                currentY = currentY + 15,
                200,
                20,
                Text.of("API-ключ для OpenAI"));
        this.openAiKeyField.setPlaceholder(Text.literal("Введите ключ...").styled(style -> style.withItalic(true)).styled(style -> style.withColor(Formatting.GRAY)));
        this.openAiKeyField.setMaxLength(200);
        this.openAiKeyField.setMaskedText(openAiKeyFieldContent);
        this.addSelectableChild(this.openAiKeyField);

        // Label for Silero TTS Key field
        TextWidget sileroKeyFieldLabel = new TextWidget(
                centerX - 100,
                currentY = currentY + 20,
                200,
                20,
                Text.of("API-ключ для Silero TTS:"),
                textRenderer);
        this.addDrawableChild(sileroKeyFieldLabel);

        // Screenshots analysis model
        this.addDrawableChild(ButtonWidget.builder(
                        Text.of(selectedSileroEndpoint.getDisplayName()),
                        button -> {
                            int index = (sileroEndpoints.indexOf(this.selectedSileroEndpoint) + 1) % sileroEndpoints.size();
                            this.selectedSileroEndpoint = sileroEndpoints.get(index);
                            button.setMessage(Text.of(this.selectedSileroEndpoint.getDisplayName()));
                        }).dimensions(centerX - 100, currentY = currentY + 15, 40, 20)
                .build());

        // Input field for Silero TTS Key
        this.sileroKeyField = new MaskedTextFieldWidget(
                this.textRenderer,
                centerX - 60,
                currentY,
                160,
                20,
                Text.of("API-ключ для Silero TTS"));
        this.sileroKeyField.setPlaceholder(
                Text.literal("Введите ключ...")
                        .styled(style -> style.withItalic(true))
                        .styled(style -> style.withColor(Formatting.GRAY)));
        this.sileroKeyField.setMaxLength(45);
        this.sileroKeyField.setMaskedText(sileroKeyFieldContent);
        this.addSelectableChild(this.sileroKeyField);

        // Screenshots analysis model
        this.addDrawableChild(ButtonWidget.builder(
                        Text.of("Анализ скриншотов: " + this.selectedScreenshotModel.getDisplayName()),
                        button -> {
                            int index = (gptVisionModelsList.indexOf(this.selectedScreenshotModel) + 1) % gptVisionModelsList.size();
                            this.selectedScreenshotModel = gptVisionModelsList.get(index);
                            button.setMessage(Text.of("Анализ скриншотов: " + this.selectedScreenshotModel.getDisplayName()));
                        }).dimensions(centerX - 100, currentY = currentY + 26, 200, 20)
                .build());


        // Comment making model
        this.addDrawableChild(ButtonWidget.builder(
                        Text.of("Комментарии: " + this.selectedCommentModel.getDisplayName()),
                        button -> {
                            int index = (gptTextModelsList.indexOf(this.selectedCommentModel) + 1) % gptTextModelsList.size();
                            this.selectedCommentModel = gptTextModelsList.get(index);
                            button.setMessage(Text.of("Комментарии: " + this.selectedCommentModel.getDisplayName()));
                        }).dimensions(centerX - 100, currentY = currentY + 22, 200, 20)
                .build());

        // Voice selection button
        this.addDrawableChild(ButtonWidget.builder(
                        Text.of("Голос: " + SileroSpeaker.getSpeakerDisplayName(this.selectedVoice)),
                        button -> {
                            int index = (sileroSpeakerList.indexOf(this.selectedVoice) + 1) % sileroSpeakerList.size();
                            this.selectedVoice = sileroSpeakerList.get(index);
                            button.setMessage(Text.of("Голос: " + SileroSpeaker.getSpeakerDisplayName(this.selectedVoice)));
                        }).dimensions(centerX - 100, currentY = currentY + 25, 200, 20)
                .build());

        // Save button
        this.addDrawableChild(ButtonWidget.builder(
                        Text.of("Отмена"),
                        button -> {
                            this.client.setScreen(this.parent);
                        }).dimensions(centerX - 100, screenHeight - 30, 95, 20)
                .build());

        // Cancel button
        this.addDrawableChild(ButtonWidget.builder(
                        Text.of("Сохранить"),
                        button -> {
                            try {
                                boolean wasEnabled = StanleyGptClient.INSTANCE.isEnabled();
                                saveSettings();
                                if (!wasEnabled) {
                                    Utils.showNotification("StanleyGPT активен", "Настройки были успешно применены!", Formatting.GREEN);
                                }
                            } catch (GptClientInitException e) {
                                Utils.showErrorNotification("Проверьте правильность API-ключа OpenAI!");
                                return;
                            } catch (TtsClientInitException e) {
                                Utils.showErrorNotification("Проверьте правильность API-ключа Silero TTS!");
                                return;
                            } catch (FailedStateException e) {
                                Utils.showErrorNotification("OpenAI не работает в вашей стране, используйте VPN.");
                            } catch (Exception e) {
                                MOD_SETTINGS.resetSettingsToDefault();
                                Utils.showErrorNotification("Неизвестная при сохранении настроек!");
                                return;
                            }
                            this.client.setScreen(this.parent);
                        }).dimensions(centerX + 5, screenHeight - 30, 95, 20)
                .build());


    }

    private void loadSettings() {
        // Load settings
        this.commentIntensity = MOD_SETTINGS.getCommentIntensity();
        this.selectedCommentModel = MOD_SETTINGS.getSelectedCommentModel();
        this.selectedScreenshotModel = MOD_SETTINGS.getSelectedScreenshotModel();
        this.selectedVoice = MOD_SETTINGS.getSelectedVoice();
        this.openAiKeyFieldContent = MOD_SETTINGS.getOpenaiApiKey();
        this.sileroKeyFieldContent = MOD_SETTINGS.getSileroApiKey();
        this.selectedSileroEndpoint = MOD_SETTINGS.getSileroEndpoint();
    }

    private void saveSettings() {
        // Prepare the API keys
        String preparedOpenAiKey = this.openAiKeyField.getRealText().strip();
        String preparedSileroKey = this.sileroKeyField.getRealText().strip();

        // Update the API keys and check if they are valid
        GPT_CLIENT.updateApiKey(preparedOpenAiKey);
        TTS_CLIENT.updateApiKeyAndEndpoint(selectedSileroEndpoint, preparedSileroKey);

        // Save settings
        MOD_SETTINGS.setCommentIntensity(this.commentIntensity);
        MOD_SETTINGS.setSelectedCommentModel(this.selectedCommentModel);
        MOD_SETTINGS.setSelectedScreenshotModel(this.selectedScreenshotModel);
        MOD_SETTINGS.setSelectedVoice(this.selectedVoice);
        MOD_SETTINGS.setOpenaiApiKey(preparedOpenAiKey);
        MOD_SETTINGS.setSileroApiKey(preparedSileroKey);
        MOD_SETTINGS.setSileroEndpoint(selectedSileroEndpoint);
        MOD_SETTINGS.saveSettings();

        // Reset the comment maker timer
        COMMENT_MAKER.resetTimer();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Render the background
        this.renderBackground(context, mouseX, mouseY, delta);

        // Render everything else
        super.render(context, mouseX, mouseY, delta);

        // Render the title
        context.drawCenteredTextWithShadow(this.textRenderer, this.title.getString(), this.width / 2, 15, 0xFFFFFF);

        // Render the input fields
        this.openAiKeyField.render(context, mouseX, mouseY, delta);
        this.sileroKeyField.render(context, mouseX, mouseY, delta);
    }


}