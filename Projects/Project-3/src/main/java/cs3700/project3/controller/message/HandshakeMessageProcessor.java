package cs3700.project3.controller.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import cs3700.project3.model.message.HandshakeMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
class HandshakeMessageProcessor implements MessageProcessor {
    @NonNull
    private final ObjectMapper objectMapper;

    @NonNull
    private final Set<String> peers;

    @Override
    public Map<String, String> get() {
        return peers.stream().collect(Collectors.toMap(Function.identity(), this::createHandshakeMessage));
    }

    @SneakyThrows
    private String createHandshakeMessage(@NonNull String peer) {
        final HandshakeMessage handshakeMessage = new HandshakeMessage();

        handshakeMessage.setSrc(MessageProcessorUtil.getSrcAddressFrom(peer));
        handshakeMessage.setDst(peer);

        return objectMapper.writeValueAsString(handshakeMessage);
    }
}
