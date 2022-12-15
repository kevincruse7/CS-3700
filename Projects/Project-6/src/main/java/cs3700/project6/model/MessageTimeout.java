package cs3700.project6.model;

import cs3700.project6.model.message.Message;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * Data class representing timeout information for a particular sent message.
 */
@Data
@Jacksonized
@SuperBuilder
public class MessageTimeout {
    @NonNull
    final Message message;

    int numTimeouts;
    long timeout;
}
