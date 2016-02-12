package at.ftec.aerogear.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.jboss.aerogear.unifiedpush.api.AndroidVariant;
import org.jboss.aerogear.unifiedpush.api.PushApplication;
import org.jboss.aerogear.unifiedpush.api.Variant;
import org.jboss.aerogear.unifiedpush.api.VariantType;
import org.jboss.aerogear.unifiedpush.api.iOSVariant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Fischelmayer
 */
public class PushApplicationDeserializer extends JsonDeserializer<PushApplication> {
    @Override
    public PushApplication deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        PushApplication pushApplication = new PushApplication();

        JsonNode root = p.getCodec().readTree(p);

        pushApplication.setDescription( root.get("description").asText() );
        pushApplication.setDeveloper( root.get("developer").asText() );
        pushApplication.setMasterSecret( root.get("masterSecret").asText() );
        pushApplication.setName( root.get("name").asText() );
        pushApplication.setPushApplicationID( root.get("pushApplicationID").asText() );


        List<Variant> variantList = new ArrayList<Variant>();
        JsonNode variants = root.get("variants");
        if(variants.isArray()) {
            for(JsonNode v : variants) {
                String typeString = v.get("type").asText();

                Variant variant = null;
                if (typeString.equals("ios")) {
                    variant = new iOSVariant();
                } else if(typeString.equals("android")) {
                    variant = new AndroidVariant();
                }
                variantList.add( variant );
            }
        }

        pushApplication.setVariants( variantList );

        return pushApplication;
    }
}
