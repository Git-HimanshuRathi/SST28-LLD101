public class TransportBookingService {
    private final DistanceService distService;
    private final AllocationService allocService;
    private final PaymentService payService;

    public TransportBookingService(DistanceService distService, AllocationService allocService, PaymentService payService) {
        this.distService = distService;
        this.allocService = allocService;
        this.payService = payService;
    }

    public void book(TripRequest req) {
        double km = distService.km(req.from, req.to);
        System.out.println("DistanceKm=" + km);

        String driver = allocService.allocate(req.studentId);
        System.out.println("Driver=" + driver);

        double fare = 50.0 + km * 6.6666666667;
        fare = Math.round(fare * 100.0) / 100.0;

        String txn = payService.charge(req.studentId, fare);
        System.out.println("Payment=PAID txn=" + txn);

        BookingReceipt r = new BookingReceipt("R-501", fare);
        System.out.println("RECEIPT: " + r.id + " | fare=" + String.format("%.2f", r.fare));
    }
}
