package com.androidthings.ssarangi.adafruitpca9685;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;

import timber.log.Timber;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 * <p>
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG)
            Timber.plant(new Timber.DebugTree());
        else
            Timber.plant(new NotLoggingTree());

        AdafruitServoHat servoHat = new AdafruitServoHat(
                Constants.I2C1,
                Constants.PCA9685_ADDRESS,
                Constants.SIXTEEN_CHANNEL);

        int servoIdx = 0;
        servoHat.createServo(0, Constants.DEFAULT_SERVO_FREQUENCY);
        servoHat.rotateToAngle(servoIdx, 45);
        servoHat.execute(0);


        // Close the servoHat
        // servoHat.close();
        setContentView(R.layout.activity_main);
    }
}
