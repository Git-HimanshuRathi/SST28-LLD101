/**
 * LSP-compliant: sends full body without truncation.
 * Uses: email, subject, body fields.
 */
public class EmailSender extends NotificationSender {
    public EmailSender(AuditLog audit) {
        super(audit);
    }

    @Override
    public SendResult send(Notification n) {
        System.out.println("EMAIL -> to=" + n.email + " subject=" + n.subject + " body=" + n.body);
        audit.add("email sent");
        return SendResult.ok();
    }
}
