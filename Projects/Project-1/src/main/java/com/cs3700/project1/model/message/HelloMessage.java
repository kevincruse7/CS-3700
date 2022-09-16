package com.cs3700.project1.model.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Simple data object for a 'hello' message. */
@Data
@NoArgsConstructor
public class HelloMessage {
    String type = "hello";

    @JsonProperty("northeastern_username")
    String username;

    public HelloMessage(String username) {
        this.username = username;
    }
}
