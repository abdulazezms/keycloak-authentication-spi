package aziz.keycloak.credential.model;

import org.keycloak.common.util.Time;
import org.keycloak.credential.CredentialModel;
import org.keycloak.util.JsonSerialization;

import java.io.IOException;

public class PatternModel extends CredentialModel {
    public static final String TYPE = "pattern";

    private final PatternSecretData secretData;


    private static final String USER_LABEL = "Pattern Authentication";

    private PatternModel(PatternSecretData secretData) {
        this.secretData = secretData;
    }

    private PatternModel(String secretData) {
        this.secretData = new PatternSecretData(secretData);
    }

    public static PatternModel createPattern(String pattern) {
        PatternModel credentialModel = new PatternModel(pattern);
        credentialModel.fillCredentialModelFields();
        return credentialModel;
    }

    private void fillCredentialModelFields(){
        try {
            setSecretData(JsonSerialization.writeValueAsString(secretData));
            setType(TYPE);
            setUserLabel(USER_LABEL);
            setCreatedDate(Time.currentTimeMillis());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static PatternModel createCredentialFromModel(CredentialModel credentialModel) {
        try {
            PatternSecretData secretData = JsonSerialization
                    .readValue(credentialModel.getSecretData(), PatternSecretData.class);
            PatternModel patternModel = new PatternModel(secretData);
            patternModel.setUserLabel(credentialModel.getUserLabel());
            patternModel.setCreatedDate(credentialModel.getCreatedDate());
            patternModel.setId(credentialModel.getId());
            patternModel.setType(TYPE);
            return patternModel;
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public PatternSecretData getPatternSecretData() {
        return secretData;
    }
}
