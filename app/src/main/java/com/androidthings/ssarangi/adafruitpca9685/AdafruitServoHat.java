package com.androidthings.ssarangi.adafruitpca9685;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class AdafruitServoHat {
    private AdafruitPCA9685 mPwm;
    private List<AdafruitServo> servos;
    private int mNumServos;

    public AdafruitServoHat(String i2cName, int i2cAddress, int numServos) {
        mPwm = new AdafruitPCA9685(i2cName, i2cAddress);
        mPwm.setPwmFreq(Constants.DEFAULT_SERVO_FREQUENCY);
        mNumServos = numServos;

        servos = new ArrayList<>();
        for (int i = 0; i < numServos; ++i) {
            servos.add(null);
        }
    }

    public void createServo(int index, int freq) {
        if (index < 0 || index > mNumServos) {
            Timber.d("Servo index must be between 0 & %s", mNumServos);
            throw new RuntimeException("Servo index must be between 0 & " + mNumServos);
        }
        AdafruitServo servo = new AdafruitServo(index, freq);
        servos.set(servo.getIndex(), servo);
    }

    public void setPulse(int index, int pulse) {
        int pulse_length = 1000000;
        pulse_length /= 60;
        pulse_length /= 4096;
        pulse *= 1000;
        pulse /= pulse_length;
        mPwm.setPwm(index, 0, pulse);
    }

    public void rotateToAngle(int index, int angle) throws IllegalArgumentException {
        if (index < 0 || index > mNumServos) {
            Timber.d("Servo index must be between 0 & %s", mNumServos);
            throw new IllegalArgumentException("Servo index must be between 0 & " + mNumServos);
        }
        servos.get(index).setAngle(angle);
    }

    public void run(int index) throws IllegalArgumentException {
        if (index < 0 || index > mNumServos) {
            Timber.d("Servo index must be between 0 & %s", mNumServos);
            throw new IllegalArgumentException("Servo index must be between 0 & " + mNumServos);
        }
        AdafruitServo servo = servos.get(index);

        Timber.d("Duty cycle: %s", servo.getDutyCycle());
        mPwm.setPwm(servo.getIndex(), 0, servo.getDutyCycle());
    }

    public void execute(int index, int angle) throws IllegalArgumentException {
        if (index < 0 || index > mNumServos) {
            Timber.d("Servo index must be between 0 & %s", mNumServos);
            throw new IllegalArgumentException("Servo index must be between 0 & " + mNumServos);
        }

        mPwm.setPwm(index, 0, angle);
    }

    public void close() {
        mPwm.close();
    }
}
