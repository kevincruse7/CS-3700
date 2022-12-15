package cs3700.project6.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import cs3700.project6.Config;
import cs3700.project6.controller.socket.StringSocket;
import cs3700.project6.controller.socket.StringSocketFactory;
import cs3700.project6.model.Model;
import cs3700.project6.model.ModelFactory;
import lombok.NonNull;

import java.io.IOException;
import java.util.Random;
import java.util.Set;

/**
 * Factory class for creating top-level application controllers.
 */
public class ControllerFactory {
    /**
     * Creates a top-level application controller from the given values.
     *
     * @param port Port of UDP socket to connect to.
     * @param id ID of this replica.
     * @param remotes Set of IDs for remote replicas.
     * @return Created top-level application controller.
     * @throws IOException Failed to connect to UDP socket.
     */
    public static Controller createController(
        int port,
        @NonNull String id,
        @NonNull Set<String> remotes
    ) throws IOException {
        Random random = new Random();
        ObjectMapper objectMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
        Model model = ModelFactory.createModel(random, id, remotes);
        StringSocket stringSocket = StringSocketFactory.createStringSocket(Config.SOCKET_HOST, port);

        return new BasicController(random, objectMapper, model, stringSocket);
    }
}
