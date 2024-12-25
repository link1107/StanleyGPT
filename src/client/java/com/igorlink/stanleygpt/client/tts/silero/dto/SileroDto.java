package com.igorlink.stanleygpt.client.tts.silero.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SileroDto {

    @JsonProperty("api_token")
    private String apiToken;

    @JsonProperty("text")
    private String text;

    @JsonProperty("sample_rate")
    private int sampleRate;

    @JsonProperty("speaker")
    private String speaker;

    @JsonProperty("lang")
    private String lang;

    @JsonProperty("format")
    private String format;

    @JsonProperty("ssml")
    private boolean ssml;

    @JsonProperty("word_ts")
    private boolean wordTs;

    public SileroDto(String apiToken,
                     String text,
                     int sampleRate,
                     String speaker,
                     String lang,
                     String format,
                     boolean ssml,
                     boolean wordTs) {

        this.apiToken = apiToken;
        this.text = text;
        this.sampleRate = sampleRate;
        this.speaker = speaker;
        this.lang = lang;
        this.format = format;
        this.ssml = ssml;
        this.wordTs = wordTs;
    }


}
