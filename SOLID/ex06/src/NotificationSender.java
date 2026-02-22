/**
 * LSP-compliant base contract:
 * 1. send() never throws for a non-null Notification
 * 2. send() always returns a SendResult
 * 3. Channels that cannot deliver should return SendResult.error()
 * 4. Each channel uses the fields relevant to it (documented per subclass)
 */
public abstract class NotificationSender {
    protected final AuditLog audit;

    protected NotificationSender(AuditLog audit) {
        this.audit = audit;
    }

    public abstract SendResult send(Notification n);
}
