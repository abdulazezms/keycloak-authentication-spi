package aziz.keycloak.requiredaction;

import aziz.keycloak.credential.PatternCredentialProvider;
import aziz.keycloak.credential.PatternCredentialProviderFactory;
import aziz.keycloak.credential.model.PatternModel;
import aziz.keycloak.form.Attributes;
import aziz.keycloak.form.CustomMessages;
import aziz.keycloak.form.Templates;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.keycloak.authentication.CredentialRegistrator;
import org.keycloak.authentication.InitiatedActionSupport;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.credential.CredentialProvider;
import org.keycloak.models.utils.FormMessage;

public class PatternRequiredAction implements RequiredActionProvider, CredentialRegistrator {
    private static final Logger logger = Logger.getLogger(PatternRequiredAction.class);

    /**
     * Determines what type of support is provided for application-initiated
     * actions.
     *
     * @return InitiatedActionsSupport
     */
    @Override
    public InitiatedActionSupport initiatedActionSupport() {
        return InitiatedActionSupport.SUPPORTED;
    }


    /**
     * Called every time a user authenticates.  This checks to see if this required action should be triggered.
     * The implementation of this method is responsible for setting the required action on the UserModel.
     * <p>
     * For example, the UpdatePassword required actions checks the password policies to see if the password has expired.
     *
     * @param context
     */
    @Override
    public void evaluateTriggers(RequiredActionContext context) {
        // no-op
    }

    /**
     * If the user has a required action set, this method will be the initial call to obtain what to display to the
     * user's browser.  Return null if no action should be done.
     *
     * @param context
     * @return
     */
    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        logger.info("==> user is required to configure the pattern authenticator");
        Response challenge = context.form()
                .createForm(Templates.PATTERN_SET_UP_TEMPLATE);
        context.challenge(challenge);
    }

    /**
     * Called when a required action has form input you want to process.
     *
     * @param context
     */
    @Override
    public void processAction(RequiredActionContext context) {

        String pattern = context
                .getHttpRequest()
                .getDecodedFormParameters()
                .getFirst(Attributes.PATTERN_ATTRIBUTE);

        if(pattern == null || pattern.isEmpty()) {
            Response challenge = context.form()
                    .addError(new FormMessage(Attributes.PATTERN_ATTRIBUTE, CustomMessages.ERROR_PATTERN_SETUP))
                    .createForm(Templates.PATTERN_SET_UP_TEMPLATE);
            context.challenge(challenge);
            return;
        }
        var provider = (PatternCredentialProvider) context
                .getSession()
                .getProvider(CredentialProvider.class, PatternCredentialProviderFactory.PROVIDER_ID);

        PatternModel model = PatternModel.createPattern(pattern);
        provider.createCredential(context.getRealm(), context.getUser(), model);
        context.success();
    }

    @Override
    public void close() {
        // no-op
    }
}
