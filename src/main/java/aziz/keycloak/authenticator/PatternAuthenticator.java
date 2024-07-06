package aziz.keycloak.authenticator;

import aziz.keycloak.credential.PatternCredentialProvider;
import aziz.keycloak.credential.PatternCredentialProviderFactory;
import aziz.keycloak.form.Attributes;
import aziz.keycloak.form.CustomMessages;
import aziz.keycloak.form.Templates;
import aziz.keycloak.requiredaction.PatternRequiredActionFactory;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.CredentialValidator;
import org.keycloak.credential.CredentialProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.sessions.AuthenticationSessionModel;

public class PatternAuthenticator implements Authenticator, CredentialValidator<PatternCredentialProvider> {
    private static final Logger logger = Logger.getLogger(PatternAuthenticator.class);

    @Override
    public PatternCredentialProvider getCredentialProvider(KeycloakSession session) {
        return (PatternCredentialProvider) session.getProvider(CredentialProvider.class, PatternCredentialProviderFactory.PROVIDER_ID);

    }

    /**
     * Initial call for the authenticator.  This method should check the current HTTP request to determine if the request
     * satifies the Authenticator's requirements.  If it doesn't, it should send back a challenge response by calling
     * the AuthenticationFlowContext.challenge(Response).  If this challenge is a authentication, the action URL
     * of the form must point to
     * <p>
     * /realms/{realm}/login-actions/authenticate?code={session-code}&execution={executionId}
     * <p>
     * or
     * <p>
     * /realms/{realm}/login-actions/registration?code={session-code}&execution={executionId}
     * <p>
     * {session-code} pertains to the code generated from AuthenticationFlowContext.generateAccessCode().  The {executionId}
     * pertains to the AuthenticationExecutionModel.getId() value obtained from AuthenticationFlowContext.getExecution().
     * <p>
     * The action URL will invoke the action() method described below.
     *
     * @param context
     */
    @Override
    public void authenticate(AuthenticationFlowContext context) {
        Response response = context
                .form()
                .createForm(Templates.PATTERN_LOGIN_TEMPLATE);
        context.challenge(response);
    }

    /**
     * Called from a form action invocation.
     *
     * @param context
     */
    @Override
    public void action(AuthenticationFlowContext context) {
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        if (formData.containsKey("cancel")) {
            context.resetFlow();
            return;
        }
        boolean validated = validatePattern(context);
        if (!validated) {
            Response challenge =  context.form()
                    .addError(new FormMessage(Attributes.PATTERN_ATTRIBUTE, CustomMessages.ERROR_WRONG_PATTERN))
                    .createForm(Templates.PATTERN_LOGIN_TEMPLATE);
            context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challenge);
            return;
        }
        context.success();
    }

    protected boolean validatePattern(AuthenticationFlowContext context) {
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String patternSubmitted = formData.getFirst(Attributes.PATTERN_ATTRIBUTE);
        String credentialId = formData.getFirst(Attributes.CREDENTIAL_ID_FORM_ATTRIBUTE);
        if (credentialId == null || credentialId.isEmpty()) {

            credentialId = getCredentialProvider(context.getSession())
                    .getDefaultCredential(context.getSession(), context.getRealm(), context.getUser()).getId();
            logger.info("==> Retrieving default credential ID from the credential provider");
        }
        UserCredentialModel input = new UserCredentialModel(credentialId, getType(context.getSession()), patternSubmitted);
        return getCredentialProvider(context.getSession()).isValid(context.getRealm(), context.getUser(), input);
    }

    /**
     * Does this authenticator require that the user has already been identified?  That AuthenticatorContext.getUser() is not null?
     *
     * @return
     */
    @Override
    public boolean requiresUser() {
        return true;
    }

    /**
     * Is this authenticator configured for this user.
     *
     * @param session
     * @param realm
     * @param user
     * @return
     */
    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        boolean result = getCredentialProvider(session).isConfiguredFor(realm, user, getType(session));
        return result;
    }

    /**
     * Set actions to configure authenticator
     *
     * @param session
     * @param realm
     * @param user
     */
    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        AuthenticationSessionModel authenticationSession = session.getContext().getAuthenticationSession();
        if (!authenticationSession.getRequiredActions().contains(PatternRequiredActionFactory.PROVIDER_ID)) {
            authenticationSession.addRequiredAction(PatternRequiredActionFactory.PROVIDER_ID);
        }
    }

    @Override
    public void close() {
        // no-op
    }
}
