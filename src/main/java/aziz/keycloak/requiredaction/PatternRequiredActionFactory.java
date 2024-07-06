package aziz.keycloak.requiredaction;

import org.keycloak.Config;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class PatternRequiredActionFactory implements RequiredActionFactory {
    public static final String PROVIDER_ID = "pattern-required-action";

    private static final PatternRequiredAction SINGLETON = new PatternRequiredAction();

    /**
     * Display text used in admin console to reference this required action
     *
     * @return
     */
    @Override
    public String getDisplayText() {
        return "Configure Pattern";
    }

    @Override
    public RequiredActionProvider create(KeycloakSession session) {
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
