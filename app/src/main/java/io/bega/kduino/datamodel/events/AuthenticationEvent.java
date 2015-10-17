package io.bega.kduino.datamodel.events;

public class AuthenticationEvent {

    private final byte[] measurements;

    public AuthenticationEvent(byte[] measurements) {
        this.measurements = measurements;
    }

    public byte[] getMeasurements() {
        return measurements;
    }

}