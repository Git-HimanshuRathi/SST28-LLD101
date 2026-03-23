package com.example.tickets;

import java.util.Arrays;

public class TryIt {
    public static void main(String[] args) {
        System.out.println("=== HelpLite Immutable Tickets ===");

        TicketService svc = new TicketService();
        IncidentTicket t1 = svc.createTicket();

        // Demonstrate immutability: original is unchanged after escalation
        IncidentTicket t2 = svc.escalate(t1);

        System.out.println();
        System.out.println("Original still HIGH? " + t1.priority().equals("HIGH"));
        System.out.println("Escalated is CRITICAL? " + t2.priority().equals("CRITICAL"));

        // Demonstrate tags immutability
        try {
            t1.tags().add("hacked");
            System.out.println("FAIL: tags list is mutable!");
        } catch (UnsupportedOperationException e) {
            System.out.println("PASS: tags list is immutable");
        }

        // Demonstrate validation
        try {
            new IncidentTicket.Builder("bad id!", "not-email", "")
                    .build();
            System.out.println("FAIL: validation missed!");
        } catch (IllegalArgumentException e) {
            System.out.println("PASS: validation caught — " + e.getMessage());
        }
    }
}
