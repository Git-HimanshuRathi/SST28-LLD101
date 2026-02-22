/**
 * LSP-compliant: SMS channel uses phone and body fields.
 * Subject is not applicable to SMS (documented, not a surprise).
 */
public class SmsSender extends NotificationSender {
    public SmsSender(AuditLog audit) {
        super(audit);
    }

    @Override
    public SendResult send(Notification n) {
        System.out.println("SMS -> to=" + n.phone + " body=" + n.body);
        audit.add("sms sent");
        return SendResult.ok();
    }
}
