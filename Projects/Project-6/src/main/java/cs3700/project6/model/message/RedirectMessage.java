package cs3700.project6.model.message;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * Data class representing a {@code redirect} message.
 */
@Data
@Jacksonized
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RedirectMessage extends Message {
    @Builder.Default
    String type = "redirect";
}
