package aziz.keycloak.authenticator;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;

public class PatternAuthenticatorFactory implements AuthenticatorFactory {
    public static final String PROVIDER_ID = "pattern-authenticator";
    private static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
            AuthenticationExecutionModel.Requirement.REQUIRED,
            AuthenticationExecutionModel.Requirement.DISABLED
    };

    private static final PatternAuthenticator SINGLETON = new PatternAuthenticator();
    /**
     * Friendly name for the authenticator
     *
     * @return
     */
    @Override
    public String getDisplayType() {
        return "Pattern authenticator";
    }

    /**
     * General authenticator type, i.e. totp, password, cert.
     *
     * @return null if not a referencable category
     */
    @Override
    public String getReferenceCategory() {
        return "pattern";
    }

    /**
     * Is this authenticator configurable?
     *
     * @return
     */
    @Override
    public boolean isConfigurable() {
        return false;
    }

    /**
     * What requirement settings are allowed.
     *
     * @return
     */
    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    /**
     * Does this authenticator have required actions that can set if the user does not have
     * this authenticator set up?
     *
     * @return true since we have a required action to set up the secret answer on first login.
     */
    @Override
    public boolean isUserSetupAllowed() {
        return true;
    }

    @Override
    public String getHelpText() {
        return "Authenticate using a pattern selection.";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return null;
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        return SINGLETON;
    }

    /**
     * Only called once when the factory is first created.  This config is pulled from keycloak_server.json
     *
     * @param config
     */
    @Override
    public void init(Config.Scope config) {
        // no-op
    }

    /**
     * Called after all provider factories have been initialized
     *
     * @param factory
     */
    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // no-op
    }

    /**
     * This is called when the server shuts down.
     */
    @Override
    public void close() {
        // no-op
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
