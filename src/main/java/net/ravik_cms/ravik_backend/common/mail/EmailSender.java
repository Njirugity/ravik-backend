package net.ravik_cms.ravik_backend.common.mail;

/**
 * Minimal outbound email abstraction — kept separate from the password-reset flow so the
 * sending mechanism (SMTP today) can change without touching callers.
 */
public interface EmailSender {
    /**
     * Sends a plain-text email.
     *
     * @param to      recipient address
     * @param subject email subject
     * @param body    plain-text body
     */
    void send(String to, String subject, String body);
}
