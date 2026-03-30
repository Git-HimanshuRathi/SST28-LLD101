package com.example.elevator;

public class Request {
    private final int floor;
    private final Direction direction;
    private final RequestType type;
    private final long timestamp;

    public Request(int floor, Direction direction, RequestType type) {
        this.floor = floor; this.direction = direction; this.type = type;
        this.timestamp = System.currentTimeMillis();
    }

    public int floor() { return floor; }
    public Direction direction() { return direction; }
    public RequestType type() { return type; }
    public long timestamp() { return timestamp; }

    @Override
    public String toString() { return type + " request: floor=" + floor + " dir=" + direction; }
}
