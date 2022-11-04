package cs3700.project4.model;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

import java.io.Serializable;

/**
 * Dataclass for representing TCP packets in the application.
 */
@Value
public class TCPPacket implements Serializable {
    int sequence;
    String data;

    @EqualsAndHashCode.Exclude
    int hash;

    public TCPPacket(int sequence, @NonNull String data) {
        this.sequence = sequence;
        this.data = data;
        this.hash = hashCode();
    }
}
