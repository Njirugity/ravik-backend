package net.ravik_cms.ravik_backend.common.password;

import net.ravik_cms.ravik_backend.common.exception.WeakPasswordException;
import org.passay.CharacterCharacteristicsRule;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;
import org.springframework.stereotype.Component;
/**
 * Centralized password strength policy, used by every endpoint that sets a new password
 * (change-password, reset-password). Backed by Passay rather than hand-rolled regex.
 * <p>
 * Policy: 8-16 characters, no whitespace, and all 4 character classes (uppercase, lowercase,
 * digit, special) present.
 */
@Component
public class PasswordPolicy {
    private static final int MIN_LENGTH = 6;
    private static final int MAX_LENGTH = 16;
    private static final int MIN_CHARACTER_CLASSES = 4;

    private final PasswordValidator validator;

    public PasswordPolicy() {
        CharacterCharacteristicsRule characterRule = new CharacterCharacteristicsRule(
                MIN_CHARACTER_CLASSES,
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.Special, 1)
        );
        this.validator = new PasswordValidator(
                new LengthRule(MIN_LENGTH, MAX_LENGTH),
                new WhitespaceRule(),
                characterRule
        );
    }

    /**
     * Validates a candidate password against the strength policy.
     *
     * @param password the candidate password
     * @throws WeakPasswordException if it fails any rule, with all failure reasons joined together
     */
    public void validate(String password) {
        RuleResult result = validator.validate(new PasswordData(password));
        if (!result.isValid()) {
            String message = String.join("; ", validator.getMessages(result));
            throw new WeakPasswordException(message);
        }
    }
}
