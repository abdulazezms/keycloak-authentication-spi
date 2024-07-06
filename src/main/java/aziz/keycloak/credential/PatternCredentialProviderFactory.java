package aziz.keycloak.credential;

import org.keycloak.credential.CredentialProvider;
import org.keycloak.credential.CredentialProviderFactory;
import org.keycloak.models.KeycloakSession;

public class PatternCredentialProviderFactory implements CredentialProviderFactory<PatternCredentialProvider> {
    public static final String PROVIDER_ID = "pattern-credential";

    @Override
    public CredentialProvider create(KeycloakSession session) {
        return new PatternCredentialProvider(session);
    }

    /**
     * This is the name of the provider and will be showed in the admin console as an option.
     *
     * @return
     */
    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
