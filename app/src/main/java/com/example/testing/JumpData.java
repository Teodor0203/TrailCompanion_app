package com.example.testing;

public class JumpData {

    private int jumpDetected;
    private float airTime;

    public JumpData(){}

    public void setJumpDetected(int jumpDetected) {
        this.jumpDetected = jumpDetected;
    }

    public void setAirTime(float airTime) {
        this.airTime = airTime;
    }

    public int getJumpDetected() {
        return jumpDetected;
    }

    @Override
    public String toString() {
        return "jumpDetected: " + jumpDetected;
    }
}
