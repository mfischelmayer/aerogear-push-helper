package at.ftec.aerogear.api.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;
import org.jboss.aerogear.unifiedpush.api.VariantType;

import java.io.IOException;

public class VariantTypeDeserializer extends StdDeserializer<VariantType> {

    public VariantTypeDeserializer() {
        this(null);
    }

    public VariantTypeDeserializer(Class<?> vc) {
        super(vc);
    }
    @Override
    public VariantType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        TextNode node = jsonParser.getCodec().readTree(jsonParser);
        return VariantType.valueOf(node.textValue().toUpperCase());
    }
}
