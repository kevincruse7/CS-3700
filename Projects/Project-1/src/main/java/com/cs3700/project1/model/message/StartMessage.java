package com.cs3700.project1.model.message;

import lombok.Data;

/** Simple data object for a 'start' message. */
@Data
public class StartMessage {
    String type;
    String id;
}
