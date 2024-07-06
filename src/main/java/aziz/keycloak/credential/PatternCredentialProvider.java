package aziz.keycloak.credential;

import aziz.keycloak.credential.model.PatternModel;
import aziz.keycloak.requiredaction.PatternRequiredActionFactory;
import org.jboss.logging.Logger;
import org.keycloak.credential.*;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;

public class PatternCredentialProvider implements CredentialProvider<CredentialModel>, CredentialInputValidator {
    private static final Logger logger = Logger.getLogger(PatternCredentialProvider.class);
    private static final String HELP_TEXT = "An authenticator based on pattern selection";
    public static final String DISPLAY_NAME = "Pattern authenticator";

    protected KeycloakSession session;

    public PatternCredentialProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public String getType() {
        return PatternModel.TYPE;
    }

    @Override
    public CredentialModel createCredential(RealmModel realm, UserModel user, CredentialModel credentialModel) {
        CredentialModel oldCredential = getDefaultCredential(session, realm, user), createdCredential;
        if (oldCredential == null) {
            logger.info("==> creating a new credential");
            createdCredential = user.credentialManager().createStoredCredential(credentialModel);
        } else{
            logger.info("==> updating existing credential");
            credentialModel.setId(oldCredential.getId());
            user.credentialManager().updateStoredCredential(credentialModel);
            createdCredential = credentialModel;
        }
        return createdCredential;
    }

    @Override
    public boolean deleteCredential(RealmModel realm, UserModel user, String credentialId) {
        return user.credentialManager().removeStoredCredentialById(credentialId);
    }

    @Override
    public PatternModel getCredentialFromModel(CredentialModel model) {
        return PatternModel.createCredentialFromModel(model);
    }

    @Override
    public CredentialTypeMetadata getCredentialTypeMetadata(CredentialTypeMetadataContext metadataContext) {
        return CredentialTypeMetadata.builder()
                .type(getType())
                .category(CredentialTypeMetadata.Category.TWO_FACTOR)
                .displayName(DISPLAY_NAME)
                .createAction(PatternRequiredActionFactory.PROVIDER_ID)
                .helpText(HELP_TEXT)
                .removeable(true)
                .build(session);
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return getType().equals(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        if (!supportsCredentialType(credentialType)) return false;
        return user.credentialManager().getStoredCredentialsByTypeStream(credentialType).findAny().isPresent();
    }

    /**
     * Tests whether a credential is valid
     *
     * @param realm           The realm in which to which the credential belongs to
     * @param user            The user for which to test the credential
     * @param credentialInput the credential details to verify
     * @return true if the passed secret is correct
     */
    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput credentialInput) {
        if(!(credentialInput instanceof UserCredentialModel)) {
            logger.info("==> Expected instance of user credential model");
            return false;
        }
        if(!credentialInput.getType().equals(PatternModel.TYPE)) {
            logger.info("==> Different credential types");
            return false;
        }
        String challengeResponse = credentialInput.getChallengeResponse();
        if(challengeResponse == null) {
            logger.info("==> Empty challenge response");
            return false;
        }
        CredentialModel credentialModel = user.credentialManager().getStoredCredentialById(credentialInput.getCredentialId());
        PatternModel patternModel = getCredentialFromModel(credentialModel);
        return patternModel.getPatternSecretData().getValue().equals(challengeResponse);
    }
}
