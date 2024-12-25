package com.igorlink.stanleygpt.client;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.SoundOptionsScreen;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A class for playing audio files.
 */
@Slf4j
public class AudioPlayer {
    /**
     * The priority of the event that is currently being played.
     */
    @Getter
    private int eventPriority = -1;

    private final ReentrantLock threadLock = new ReentrantLock(true);
    private volatile boolean isPlaying = false;

    /**
     * Plays an OGG audio file.
     *
     * @param oggData The OGG audio file data.
     */
    public void playOggFile(byte[] oggData, int eventPriority) {
        log.info("Playing audio file! Size: {}", oggData.length);

        // Check if the event with the same or lower priority is already playing
        if (eventPriority < this.eventPriority) {
            MinecraftClient.getInstance().execute(
                    () -> {
                        MinecraftClient.getInstance().player.sendMessage(
                                net.minecraft.text.Text.of("Event with priority " + eventPriority + " is ignored because the event with priority " + this.eventPriority + " is already playing!"),
                                false
                        );
                    }
            );
            return;
        }

        MinecraftClient.getInstance().execute(
                () -> {
                    MinecraftClient.getInstance().player.sendMessage(
                            Text.of("Event with priority " + eventPriority + " is launched"),
                            false
                    );
                }
        );

        // Stop the current playback
        stopCurrentPlayback();

        // Schedule the playback
        ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);
        // Play the audio file in a separate thread
        scheduler.schedule(() -> {
            // Starting the new playback
            threadLock.lock();
            isPlaying = true;
            this.eventPriority = eventPriority;

            // Set the context class loader to the class loader of this class (to avoid issues with the OGG SPI)
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

            try {
                // Set the flag that the playback is in progress
                this.eventPriority = eventPriority;

                // Create an audio stream from the OGG byte data
                AudioInputStream vorbisStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(oggData));

                // Convert the OGG audio stream to PCM
                AudioFormat baseFormat = vorbisStream.getFormat();
                AudioFormat pcmFormat = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,  // Signed PCM
                        baseFormat.getSampleRate(),       // Sample rate
                        16,                               // Sample size (16 bit)
                        baseFormat.getChannels(),         // Channels
                        baseFormat.getChannels() * 2,     // Frame size
                        baseFormat.getSampleRate(),       // Frame rate
                        false                             // Little-endian
                );


                // Create a PCM audio stream
                AudioInputStream pcmStream = AudioSystem.getAudioInputStream(pcmFormat, vorbisStream);

                // Create a SourceDataLine for playback
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, pcmFormat);
                SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);


                // Open the line and start the playback
                line.open(pcmFormat);
                line.start();

                // Play the audio stream chunk by chunk
                byte[] buffer = new byte[4096];
                int bytesRead;
                while (((bytesRead = pcmStream.read(buffer, 0, buffer.length)) != -1)
                        && isPlaying) {

                    float volume = MinecraftClient.getInstance().options.getSoundVolume(SoundCategory.VOICE);
                    for (int i = 0; i < bytesRead; i += 2) {
                        // Применяем громкость к каждому сэмплу
                        short sample = (short) ((buffer[i + 1] << 8) | (buffer[i] & 0xff));
                        sample = (short) (sample * volume); // Умножаем на громкость (0.0–1.0)
                        buffer[i] = (byte) (sample & 0xff);
                        buffer[i + 1] = (byte) ((sample >> 8) & 0xff);
                    }

                    line.write(buffer, 0, bytesRead);
                }

                // Finish the playback
                line.drain();
                line.stop();
                line.close();
                pcmStream.close();

                // Set the flag that the playback is finished
                this.eventPriority = -1;

            } catch (UnsupportedAudioFileException | LineUnavailableException |
                     IOException e) {
                log.error("Error while playing audio file", e);
            } finally {
                this.eventPriority = -1;
                this.isPlaying = false;

                threadLock.unlock();
            }
        }, 0, java.util.concurrent.TimeUnit.SECONDS);


    }

    /**
     * Stops the audio playback.
     */
    public void stopCurrentPlayback() {
        isPlaying = false;
    }


}
