package com.androidthings.ssarangi.adafruitpca9685;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


public class AdafruitPCA9685 {
    I2cDevice mI2cDevice;

    public AdafruitPCA9685() {
        this(Constants.I2C1, Constants.PCA9685_ADDRESS);
    }

    AdafruitPCA9685(String deviceName, int address) {
        Timber.i("Connecting to I2C device %s @ 0x%02X.", deviceName, address);
        try {
            PeripheralManager manager = PeripheralManager.getInstance();
            mI2cDevice = manager.openI2cDevice(deviceName, address);
        } catch (IOException e) {
            Timber.e("Unable to access PWM 9685 controller: %s", e.getMessage());
        }
        initialize();
    }

//    private List<Integer> scanI2CAvailableDevices(String i2cName) {
//        List<Integer> validAddresses = new ArrayList<>();
//        PeripheralManager manager = PeripheralManager.getInstance();
//        for (int address = 0; address < 127; ++address) {
//            try {
//                mI2cDevice = manager.openI2cDevice(i2cName, address);
//                writeRegByte(Constants.LED0_ON_L, (byte)(Constants.OUTDRV));
//                validAddresses.add(address);
//            } catch (IOException e) {
//            }
//        }
//
//        return validAddresses;
//    }

    private void checkIfI2CInitialized() {
        if (mI2cDevice == null) throw new RuntimeException("I2C device not initialized");
    }

    private void initialize() {
        checkIfI2CInitialized();
        Timber.d("Initializing PCA9685...");
        setAllPwm(0, 0);
        writeRegByte(Constants.MODE2, (byte)(Constants.OUTDRV));
        writeRegByte(Constants.MODE1, (byte)(Constants.ALLCALL));
        sleep(0.005);
        byte mode1 = readRegByte(Constants.MODE1);
        mode1 = (byte)(mode1 & ~(Constants.SLEEP)); // wake up (reset sleep)
        writeRegByte(Constants.MODE1, mode1);
        sleep(0.005); // wait for oscillator
    }

    public void close() {
        checkIfI2CInitialized();
        try {
            mI2cDevice.close();
            mI2cDevice = null;
        } catch (IOException e) {
            Timber.e("Unable to close I2C device");
        }
    }

    public void setPwmFreq(int freq) {
        double preScaleEval = Constants.FREQ_25MHz;
        preScaleEval /= 4096.0; // 12-bit
        preScaleEval /= (float) freq;
        preScaleEval -= 1.0;
        Timber.d("Setting the PWM frequency to %d Hz", freq);
        Timber.d("Estimated pre-scale: %f", preScaleEval);
        double preScale = Math.floor(preScaleEval + 0.5);
        Timber.d("Final pre-scale: %f", preScale);

        // Clear the Sleep bit and restart
        byte oldmode = readRegByte(Constants.MODE1);
        byte newmode = (byte)((oldmode & ~Constants.RESTART) | Constants.SLEEP); // Go to sleep
        writeRegByte(Constants.MODE1, newmode);
        sleep(0.005);
        // Prescale can only be set in SLEEP mode.
        writeRegByte(Constants.PRESCALE, (byte)(Math.floor(preScale)));
        sleep(0.005);
        writeRegByte(Constants.MODE1, oldmode);
        sleep(0.005);
        writeRegByte(Constants.MODE1, (byte)(oldmode | Constants.RESTART)); // Restart
    }

    public void setPwm(int channel, int on, int off) {
        int offset = 4 * channel;
        writeRegByte(Constants.LED0_ON_L + offset, (byte)(on & 0xFF));
        writeRegByte(Constants.LED0_ON_H + offset, (byte)(on >> 8));
        writeRegByte(Constants.LED0_OFF_L + offset, (byte)(off & 0xFF));
        writeRegByte(Constants.LED0_OFF_H + offset, (byte)(off >> 8));
    }

    private void setAllPwm(int on, int off) {
        writeRegByte(Constants.ALL_LED_ON_L, (byte)(on & 0xFF));
        writeRegByte(Constants.ALL_LED_ON_H, (byte)(on >> 8));
        writeRegByte(Constants.ALL_LED_OFF_L, (byte)(off & 0xFF));
        writeRegByte(Constants.ALL_LED_OFF_H, (byte)(off >> 8));
    }

    private void writeRegByte(int reg, byte data) {
        try {
            mI2cDevice.writeRegByte(reg, data);
        } catch (IOException e) {
            Timber.e("Error writing register %s: %s", reg, e.getMessage());
        }

        Timber.d("Wrote to register %s: %s", reg, data);
    }

    private byte readRegByte(int reg) {
        byte data = 0;

        try {
            data = mI2cDevice.readRegByte(reg);
        } catch (IOException e) {
            Timber.e("Error reading register %s: %s", reg, e.getMessage());
        }

        Timber.d("Read from register %s: %s", reg, data);
        return data;
    }

    private void sleep(double seconds) {
        try {
            Thread.sleep((long) (seconds * 1000));
        } catch (InterruptedException e) {
            Timber.e(e, "Sleep failed");
        }
    }
}
