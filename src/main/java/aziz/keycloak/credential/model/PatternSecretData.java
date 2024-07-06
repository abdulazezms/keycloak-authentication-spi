package aziz.keycloak.credential.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PatternSecretData {
    private final String value;

    @JsonCreator
    public PatternSecretData(@JsonProperty("value") String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
