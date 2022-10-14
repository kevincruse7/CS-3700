package cs3700.project3.model.message;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

@JsonSerialize(using = EmptyObject.Serializer.class)
class EmptyObject {
    public static class Serializer extends StdSerializer<EmptyObject> {
        @SuppressWarnings("unused")
        public Serializer() {
            this(null);
        }

        public Serializer(Class<EmptyObject> t) {
            super(t);
        }

        @Override
        public void serialize(
            EmptyObject emptyObject, JsonGenerator jsonGenerator, SerializerProvider serializerProvider
        ) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeEndObject();
        }
    }
}
