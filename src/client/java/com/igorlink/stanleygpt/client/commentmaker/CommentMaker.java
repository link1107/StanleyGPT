package com.igorlink.stanleygpt.client.commentmaker;

import com.igorlink.stanleygpt.client.AudioPlayer;
import com.igorlink.stanleygpt.client.ScreenshotMaker;
import com.igorlink.stanleygpt.client.gpt.openai.service.OpenAiRequestResult;
import dev.ai4j.openai4j.chat.*;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import static com.igorlink.stanleygpt.client.StanleyGptClient.*;

/**
 * The comment maker.
 */
@Slf4j
public class CommentMaker implements ICommentMaker {
    // The list of current messages
    private final LinkedList<Message> currentMessagesList = new LinkedList<>();
    // The maximum length of the comment
    private final int MAX_COMMENT_LENGTH = 400;
    // The maximum amount of comments
    private final int MAX_COMMENTS_AMOUNT = 15;

    private final CommentMakerTimer commentMakerTimer = new CommentMakerTimer(this);

    // The audio player
    private final AudioPlayer audioPlayer = new AudioPlayer();
    // The screenshot maker
    private final ScreenshotMaker screenshotMaker = new ScreenshotMaker();

    // The priority of the comment thread currently running but not yet playing the audio
    private int commentMakingThreadPriority = -1;

    // The scheduler for the comment creation
    private ScheduledExecutorService commentScheduler = Executors.newScheduledThreadPool(1);

    private ScheduledFuture<?> currentProcessingThread;

    private ReentrantLock threadLock = new ReentrantLock(true);



    /**
     * Make a comment in a parallel thread.
     *
     * @param eventPriority    the priority of the event
     * @param eventDescription the description of the event
     * @param takeScreenshot   whether to take a screenshot
     */
    public void makeNewComment(int eventPriority, @Nullable String eventDescription, boolean takeScreenshot) {

        // Check if the event priority is lower than the priority of the event currently being played
        int audioPlayerPriority = audioPlayer.getEventPriority();
        if (audioPlayerPriority >= eventPriority) {
            MinecraftClient.getInstance().execute(
                    () -> MinecraftClient.getInstance().player.sendMessage(Text.of("Потенциальный комментарий " +
                            "был отменен из-за меньшего приоритета в плеере: " + eventPriority + " <= " + audioPlayerPriority), false)
            );
            return;
        }

        // Check if the event priority is lower than the priority of the comment thread currently
        // running but not yet playing the audio
        if (commentMakingThreadPriority >= eventPriority) {
            MinecraftClient.getInstance().execute(
                    () -> MinecraftClient.getInstance().player.sendMessage(Text.of("Потенциальный комментарий " +
                            "был отменен из-за меньшего приоритета в потоке: " + eventPriority + " <= " + commentMakingThreadPriority), false)
            );
            return;
        }

        // Set the priority of the comment thread currently running
        commentMakingThreadPriority = eventPriority;

        // Reset the comment maker timer
        resetTimer();

        // Start the new comment creation
        commentScheduler = Executors.newScheduledThreadPool(1);

        if (currentProcessingThread != null && !currentProcessingThread.isDone() && !currentProcessingThread.isCancelled()) {
            currentProcessingThread.cancel(true);
            MinecraftClient.getInstance().execute(
                    () -> MinecraftClient.getInstance().player.sendMessage(Text.of("Предыдущий комментарий " +
                            "был отменен в пользу комментария с приоритетом " + eventPriority), false)
            );
        }

        // Schedule the comment creation
        currentProcessingThread = commentScheduler.schedule(() -> {
            threadLock.lock();

            // Set the priority of the comment thread currently running
            commentMakingThreadPriority = eventPriority;

            try {
                CompletableFuture<Void> future;
                if (takeScreenshot) {
                    // Make the comment with a screenshot
                    future = CompletableFuture
                            .runAsync(() -> makeCommentWithScreenshot(eventPriority,
                                    eventDescription));
                } else {
                    // Make the comment
                    future = CompletableFuture
                            .runAsync(() -> finalizeAndVoiceOverComment(eventPriority,
                                    eventDescription,
                                    null));
                }

                // Wait for the CompletableFuture to finish
                future.join(); // Blocks until completion
            } catch (Exception e) {
                log.error("Error while creating a comment: {}", e.getMessage(), e);
            } finally {
                commentMakingThreadPriority = -1;
                threadLock.unlock();
            }


        }, 0, TimeUnit.SECONDS);

    }


    /**
     * Makes a comment with a screenshot in a parallel thread.
     *
     * @param eventDescription the description of the event
     * @param eventPriority    the priority of the event
     */
    private void makeCommentWithScreenshot(int eventPriority, @Nullable String eventDescription) {
        // Use a CompletableFuture to handle the screenshot-taking in the main thread
        CompletableFuture.supplyAsync(() -> {
            AtomicReference<BufferedImage> screenshot = new AtomicReference<>();
            CountDownLatch latch = new CountDownLatch(1);

            // Schedule the screenshot in the main thread
            MinecraftClient.getInstance().execute(() -> {
                screenshot.set(screenshotMaker.takeCustomScreenshot());
                latch.countDown();
            });

            try {
                latch.await(); // Wait until the screenshot is taken
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Failed to take screenshot in the main thread", e);
            }

            return screenshot.get();
        }).thenCompose(screenshot -> {
            // Create a prompt for the screenshot request
            String screenshotRequestText = "Это скриншот из клиента Minecraft. Опиши происходящее в игре.";

            // Get the description of the screenshot
            OpenAiRequestResult screenshotAnalyzeResult = GPT_CLIENT.getImageVisionCompletion(
                    MOD_SETTINGS.getSelectedScreenshotModel(),
                    screenshot,
                    screenshotRequestText
            );

            // Создаем комментарий
            return CompletableFuture.runAsync(() -> finalizeAndVoiceOverComment(eventPriority, eventDescription, screenshotAnalyzeResult));
        });
    }


    /**
     * Create a comment and voice it over.
     *
     * @param eventPriority           the priority of the event
     * @param eventDescription        text description of the event
     * @param screenshotAnalyzeResult the result of the screenshot analysis
     *                                (if the comment is made with a screenshot)
     */
    private void finalizeAndVoiceOverComment(int eventPriority,
                                             @Nullable String eventDescription,
                                             @Nullable OpenAiRequestResult screenshotAnalyzeResult) {

        if (eventDescription == null && screenshotAnalyzeResult == null) {
            throw new IllegalArgumentException("One event-description or screenshot-analyze-result must be provided, but both are null!");
        }

        double price = screenshotAnalyzeResult == null ? 0.00D : screenshotAnalyzeResult.getPrice();

        // Create a prompt for the comment request
        String commentRequestText = getCommentRequestText(
                eventDescription,
                screenshotAnalyzeResult == null ? null : screenshotAnalyzeResult.getText());

        // Get the comment for the comment request
        OpenAiRequestResult commentResult = GPT_CLIENT.getTextCompletion(
                MOD_SETTINGS.getSelectedCommentModel(),
                getMessageChainForCommentRequest(currentMessagesList, commentRequestText)
        );

        log.info("Comment response text: {}", commentResult.getText());

        // Add the new messages to the current messages list
        addNewMessagesToCurrentMessagesList(commentRequestText, commentResult.getText());

        // Add the price of the request to the total price
        price += commentResult.getPrice();

        // Format the price with 2 decimal places
        DecimalFormat df = new DecimalFormat("0.0000");
        String formattedPrice = df.format(price);

        // Display the comment price in the chat with 2 decimal places
        MinecraftClient.getInstance().execute(() -> {
            MinecraftClient.getInstance().inGameHud.getChatHud()
                    .addMessage(Text.of("Comment price: " + formattedPrice + " USD"));
        });

        // Play the comment audio
        audioPlayer.playOggFile(
                TTS_CLIENT.getOggForText(
                        commentResult.getText(),
                        MOD_SETTINGS.getSelectedVoice()
                ),
                eventPriority
        );

    }


    /**
     * Get the text for the comment request.
     *
     * @param screenshotDescription the description of the screenshot
     * @return the text for the comment request
     */
    private String getCommentRequestText(@Nullable String eventDescription, @Nullable String screenshotDescription) {
        if (eventDescription == null && screenshotDescription == null) {
            throw new IllegalArgumentException("One of event-description or screenshot-description must be provided, but both are null!");
        }

        StringBuilder currentStateDescription = new StringBuilder();
        currentStateDescription
                .append("[ДАТА/ВРЕМЯ ")
                .append(LocalDateTime.now())
                .append("] ");

        if (eventDescription != null) {
            currentStateDescription.append(eventDescription).append("\n\n");
        }

        if (screenshotDescription != null) {
            currentStateDescription.append(screenshotDescription).append("\n\n");
        }

        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            currentStateDescription.append("ДОПОЛНИТЕЛЬНАЯ ИНФОРМАЦИЯ\n");
            currentStateDescription.append("Непустые ячейки инвентаря игрока в данный момент:\n");
            boolean hasItems = false;
            for (int i = 0; i < player.getInventory().size(); i++) {
                ItemStack stack = player.getInventory().getStack(i);
                if (!stack.isEmpty()) {
                    hasItems = true;
                    currentStateDescription
                            .append("Слот ")
                            .append(i).append(": ")
                            .append(
                                    stack.getItem().equals(Items.NAME_TAG) ? "Именная бирка для животных" :
                                    stack.getName().getString()
                            )
                            .append(
                                    stack.getCustomName() == null
                                            ? ""
                                            : ((stack.getItem().equals(Items.NAME_TAG) ? " (имя на бирке: " :
                                            " (кастомное название: ") + stack.getCustomName().getString() + ")")
                            )
                            .append(" x ").append(stack.getCount())
                            .append("\n");
                }
            }
            if (!hasItems) {
                currentStateDescription.append("Инвентарь пуст.\n");
            }

            currentStateDescription.append("\n");

            currentStateDescription
                    .append("Текущее здоровье игрока: ")
                    .append(player.getHealth())
                    .append("\n");

            currentStateDescription.append("Текущий уровень сытости игрока: ")
                    .append(player.getHungerManager().getFoodLevel())
                    .append("\n");

            currentStateDescription.append("Текущий уровень опыта игрока: ")
                    .append(player.experienceLevel)
                    .append("\n");

            currentStateDescription
                    .append("Текущее время суток в игре в тиках: ")
                    .append(player.getWorld().getTimeOfDay())
                    .append("\n");

            currentStateDescription
                    .append("Текущая погода в игре: ")
                    .append(player.getWorld().isRaining() ? "дождь или снег (в зависимости от биома)" : "ясно")
                    .append("\n");

            currentStateDescription
                    .append("Текущий мир игрока: ")
                    .append(player.getEntityWorld().getRegistryKey().getValue())
                    .append("\n");

            currentStateDescription
                    .append("Текущие координаты игрока: ")
                    .append(player.getBlockPos())
                    .append("\n");

            currentStateDescription
                    .append("Текущий биом игрока: ")
                    .append(player.getEntityWorld().getBiome(player.getBlockPos()).getType().name())
                    .append("\n");

            currentStateDescription
                    .append("У игрока в правой руке: ")
                    .append(
                            player.getMainHandStack().isEmpty()
                                    ? "пусто"
                                    :
                                    player.getMainHandStack().getName().getString()
                    )
                    .append(
                            player.getMainHandStack().getCustomName() == null
                                    ? ""
                                    :
                                    " (кастомное название: " + player.getMainHandStack().getCustomName().getString() + ")"
                    )
                    .append("\n");

            currentStateDescription
                    .append("У игрока в левой руке: ")
                    .append(
                            player.getOffHandStack().isEmpty()
                                    ? "пусто"
                                    :
                                    player.getOffHandStack().getName().getString())
                    .append(
                            player.getOffHandStack().getCustomName() == null
                                    ? ""
                                    :
                                    " (кастомное название: " + player.getOffHandStack().getCustomName().getString() + ")"
                    )
                    .append("\n");

            currentStateDescription
                    .append("У игрока в слоте брони головы: ")
                    .append(
                            player.getEquippedStack(EquipmentSlot.HEAD).isEmpty()
                                    ? "пусто"
                                    :
                                    player.getEquippedStack(EquipmentSlot.HEAD).getName().getString())
                    .append("\n");

            currentStateDescription
                    .append("У игрока в слоте брони тела: ")
                    .append(
                            player.getEquippedStack(EquipmentSlot.CHEST).isEmpty()
                                    ? "пусто"
                                    :
                                    player.getEquippedStack(EquipmentSlot.CHEST).getName().getString())
                    .append("\n");

            currentStateDescription
                    .append("У игрока в слоте брони ног: ")
                    .append(
                            player.getEquippedStack(EquipmentSlot.LEGS).isEmpty()
                                    ? "пусто"
                                    :
                                    player.getEquippedStack(EquipmentSlot.LEGS).getName().getString())
                    .append("\n");

            currentStateDescription
                    .append("У игрока в слоте брони ног: ")
                    .append(
                            player.getEquippedStack(EquipmentSlot.FEET).isEmpty()
                                    ? "пусто"
                                    :
                                    player.getEquippedStack(EquipmentSlot.FEET).getName().getString())
                    .append("\n");

            currentStateDescription.append("\n");
        }

        int commentLength = (int) ((MAX_COMMENT_LENGTH - 30) * Math.pow(Math.random(), 2) + 30);
        currentStateDescription
                .append("Длина следующего комментария не должна превышать ")
                .append(commentLength)
                .append(" символов.");

        log.info("Comment request text: {}", currentStateDescription.toString());

        return currentStateDescription.toString();
    }


    /**
     * Get the message chain for the comment request.
     *
     * @param currentMessages    the current messages
     * @param commentRequestText the text of the comment request
     * @return the message chain for the comment request
     */
    private List<Message> getMessageChainForCommentRequest(LinkedList<Message> currentMessages, String commentRequestText) {
        LinkedList<Message> messagesForCommentRequest = new LinkedList<>(currentMessages);
        messagesForCommentRequest
                .addFirst(
                        UserMessage
                                .builder()
                                .content(
                                        """
                                                Я сейчас играю в майнкрафт. Периодически мод в клиенте майнкрафта отправляет
                                                тебе описание происходящего в игре в данный момент. В описании также
                                                может быть блок с Дополнительной информацией - там содержатся точные
                                                показатели об игроке и окружающем его игровом мире.
                                                Пожалуйста, каждый раз, когда тебе приходит такое описание, создавай
                                                комментарий в стиле ведущего из Stanley Parable по поводу происходящего
                                                сейчас в игре, продолжая свое повествование из предыдущих комментариев
                                                (если они уже были). Чем больше злой иронии, сарказма и троллинга, тем лучше! :)
                                                
                                                Пожалуйста, используй в ответе ТОЛЬКО русские буквы и знаки
                                                препинания (даже числа пиши словами), а также выделяй ударение в словах заглавными буквами (например, слОво).
                                                Давай приступим!"""
                                )
                                .build()
                );

        messagesForCommentRequest.addLast(
                UserMessage
                        .builder()
                        .content(commentRequestText)
                        .build()
        );

        return messagesForCommentRequest;
    }


    /**
     * Add new messages to the current messages list, removing the oldest messages if necessary.
     *
     * @param commentRequestText the text of the comment request
     * @param commentText        the text of the comment
     */
    private void addNewMessagesToCurrentMessagesList(String commentRequestText, String commentText) {
        // Create the comment request message
        Content commentRequestContent = Content.builder()
                .type(ContentType.TEXT)
                .text(commentRequestText)
                .build();

        // Create the comment message
        UserMessage commentRequestMessage = UserMessage.builder()
                .content(List.of(commentRequestContent))
                .build();

        // Add the new messages to the current messages list
        currentMessagesList.addLast(commentRequestMessage);

        // Create the comment message
        AssistantMessage commentMessage = AssistantMessage.builder()
                .content(commentText)
                .build();

        // Add the new messages to the current messages list
        currentMessagesList.addLast(commentMessage);

        // Remove the oldest messages if necessary
        if (currentMessagesList.size() > MAX_COMMENTS_AMOUNT * 2) {
            currentMessagesList.removeFirst();
            currentMessagesList.removeFirst();
        }
    }


    public void skipComment() {
        audioPlayer.stopCurrentPlayback();
    }


    public void resetTimer() {
        commentMakerTimer.reset();
    }


}
