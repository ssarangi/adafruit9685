package com.androidthings.ssarangi.adafruitpca9685;

public class Constants {

    // Registers
    public static final int PCA9685_ADDRESS    = 0x40;
    public static final int MODE1              = 0x00;
    public static final int MODE2              = 0x01;
    public static final int SUBADR1            = 0x02;
    public static final int SUBADR2            = 0x03;
    public static final int SUBADR3            = 0x04;
    public static final int PRESCALE           = 0xFE;
    public static final int LED0_ON_L          = 0x06;
    public static final int LED0_ON_H          = 0x07;
    public static final int LED0_OFF_L         = 0x08;
    public static final int LED0_OFF_H         = 0x09;
    public static final int ALL_LED_ON_L       = 0xFA;
    public static final int ALL_LED_ON_H       = 0xFB;
    public static final int ALL_LED_OFF_L      = 0xFC;
    public static final int ALL_LED_OFF_H      = 0xFD;

    // Bits
    public static final int RESTART            = 0x80;
    public static final int SLEEP              = 0x10;
    public static final int ALLCALL            = 0x01;
    public static final int INVRT              = 0x10;
    public static final int OUTDRV             = 0x04;

    // Constants
    public static final double FREQ_25MHz         = 25000000.0f; // 25MHz
    public static final int DEFAULT_SERVO_FREQUENCY = 60;
    public static final float DEFAULT_MIN_PULSE_DURATION_MS = 1;
    public static final float DEFAULT_MAX_PULSE_DURATION_MS = 2;
    public static final float DEFAULT_MIN_ANGLE_DEG = 0;
    public static final float DEFAULT_MAX_ANGLE_DEG = 180;
    public static final int EIGHT_CHANNEL = 8;
    public static final int SIXTEEN_CHANNEL = 16;

    public static String I2C1 = "I2C1";
    public static String I2C2 = "I2C2";
}

