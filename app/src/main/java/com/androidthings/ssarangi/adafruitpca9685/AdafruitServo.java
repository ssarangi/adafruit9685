package com.androidthings.ssarangi.adafruitpca9685;

import timber.log.Timber;

public class AdafruitServo {
    private double mMinPulseDuration = Constants.DEFAULT_MIN_PULSE_DURATION_MS;
    private double mMaxPulseDuration = Constants.DEFAULT_MAX_PULSE_DURATION_MS;
    private double mMinAngle = Constants.DEFAULT_MIN_ANGLE_DEG;
    private double mMaxAngle = Constants.DEFAULT_MAX_ANGLE_DEG;

    private long mPulseLength;
    private double mAngle = mMinAngle;

    private int mIndex;
    private int mFrequencyHz;
    private double mPulse;
    private int mDutyCycle;

    public AdafruitServo(int index, int frequencyHz) {
        mIndex = index;
        mFrequencyHz = frequencyHz;
        mPulseLength = (1000000 / mFrequencyHz) / 4096; // Per Adafruit PWM Servo Docs, 1,000,000 us per second, 12 bits of resolution
        Timber.d("Pulse Length: %s", mPulseLength);
        setPulse();
    }

    /**
     * Set the pulse duration range. These determine the duty cycle range, where {@code minMs}
     * corresponds to the minimum angle value and {@code maxMs} corresponds to the maximum angle
     * value. If the servo is enabled, it will update its duty cycle immediately.
     *
     * @param minMs the minimum pulse duration in milliseconds
     * @param maxMs the maximum pulse duration in milliseconds
     */
    public void setPulseDurationRange(double minMs, double maxMs) {
        if (minMs >= maxMs) {
            throw new IllegalArgumentException("Minimum pulse duration must be less than maximum pulse duration");
        }
        if (minMs < 0) {
            throw new IllegalArgumentException("Minimum pulse duration must be greater than zero");
        }
        mMinPulseDuration = minMs;
        mMaxPulseDuration = maxMs;
        setPulse();
    }

    /**
     * Set the range of angle values the servo accepts. If the servo is enabled and its current
     * position is outside this range, it will update its position to the new minimum or maximum,
     * whichever is closest.
     *
     * @param minAngle the minimum angle in degrees
     * @param maxAngle the maximum angle in degrees
     */
    public void setAngleRange(double minAngle, double maxAngle) {
        if (minAngle >= maxAngle) {
            throw new IllegalArgumentException("The minimum angle must be less than maximum angle");
        }
        mMinAngle = minAngle;
        mMaxAngle = maxAngle;
        // clamp mAngle to new range
        if (mAngle < mMinAngle) {
            mAngle = mMinAngle;
        } else if (mAngle > mMaxAngle) {
            mAngle = mMaxAngle;
        }
        setPulse();
    }

    /**
     * Set the angle position, calculates the pulse and duty cycle
     *
     * @param angle the angle position in degrees
     */
    public void setAngle(float angle) {
        if (angle < this.getMinimumAngle() || angle > this.getMaximumAngle()) {
            throw new IllegalArgumentException("Angle (" + angle + ") not in range [" + this.getMinimumAngle()
                    + " - " + this.getMaximumAngle() + "]");
        }
        mAngle = angle;
        Timber.d("Angle set to: %s degrees", mAngle);
        setPulse();
    }

    /**
     * Sets the pulse
     */
    private void setPulse() {
        double pulse = interpolate(mAngle, mMinAngle, mMaxAngle, mMinPulseDuration, mMaxPulseDuration);
        if (pulse < mMinPulseDuration || pulse > mMaxPulseDuration) {
            throw new IllegalArgumentException("Pulse (" + pulse + ") not in range [" + mMinPulseDuration
                    + " - " + mMaxPulseDuration + "]");
        }
        mPulse = pulse;
        Timber.d("Pulse set to: %s", mPulse);
        setDutyCycle();
    }

    /**
     * Sets the duty cycle
     */
    private void setDutyCycle() {
        mDutyCycle = (int) (mPulse * 1000 / mPulseLength);
        Timber.d("Duty cycle set to: %s", mDutyCycle);
    }

    /**
     * Linearly interpolates angle between servo ranges to get a pulse width in milliseconds
     * More about interpolation at: https://en.wikipedia.org/wiki/Linear_interpolation
     */
    private double interpolate(double angle, double minAngle, double maxAngle, double minPulse, double maxPulse) {
        double normalizedAngleRatio = (angle - minAngle) / (maxAngle - minAngle);
        double pulseWidth = minPulse + (maxPulse - minPulse) * normalizedAngleRatio;
        Timber.d("Pulse width: %s ms",  pulseWidth);
        return pulseWidth;
    }

    /**
     * @return the current minimum pulse duration
     */
    public double getMinimumPulseDuration() {
        return mMinPulseDuration;
    }

    /**
     * @return the current maximum pulse duration
     */
    public double getMaximumPulseDuration() {
        return mMaxPulseDuration;
    }

    /**
     * @return the minimum angle in degrees
     */
    public double getMinimumAngle() {
        return mMinAngle;
    }

    /**
     * @return the maximum angle in degrees
     */
    public double getMaximumAngle() {
        return mMaxAngle;
    }

    /**
     * @return the current angle in degrees
     */
    public double getAngle() {
        return mAngle;
    }

    /**
     * Returns the pulse duration in ms
     */
    public double getPulse() {
        return mPulse;
    }

    /**
     * Returns the frequency in Hz
     */
    public int getFrequencyHz() {
        return mFrequencyHz;
    }

    /**
     * Returns the index of the servo in the hat
     */
    public int getIndex() {
        return mIndex;
    }

    /**
     * Returns the duty cycle
     *
     * @return
     */
    public int getDutyCycle() {
        return mDutyCycle;
    }
}
