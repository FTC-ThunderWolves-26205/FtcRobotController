package org.firstinspires.ftc.robotcontroller.external.samples.samples;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="JLG_Servo_Test", group="TeleOp")
@Disabled
public class JLG_Servo_Test extends OpMode {

    private Servo servo;
    private double servoPosition = 0.0;

    // Here we are declaring variables to track the previous state of the dpad buttons.
    // These prevent holding the button from repeatedly changing the servo position.
    private boolean dPadRightStatus = false;
    private boolean dPadLeftStatus = false;

    @Override
    public void init() {
        // Get the servo from the hardware map and initialize it to position 0.
        servo = hardwareMap.get(Servo.class, "servo");
        servo.setPosition(0.0);

        // Display initialization message in telemetry.
        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void loop() {
        // Move servo to max position (1.0) when dpad_up is pressed.
        if (gamepad1.dpad_up) {
            servoPosition = 1.0;
        }
        // Move servo to min position (0.0) when dpad_down is pressed.
        else if (gamepad1.dpad_down) {
            servoPosition = 0.0;
        }
        // Increase servo position by 0.1 each time dpad_right is freshly pressed.
        // This prevents continuous adjustment when the button is held down.
        else if (gamepad1.dpad_right && !dPadRightStatus) {
            servoPosition += 0.1;  // Simple way to increase value by 0.1
            servoPosition = Math.min(1.0, servoPosition); // Ensure servo does not exceed max of 1.0
        }
        // Decrease servo position by 0.1 each time dpad_left is freshly pressed.
        // Prevents continuous adjustment while holding the button.
        else if (gamepad1.dpad_left && !dPadLeftStatus) {
            servoPosition -= 0.1;
            servoPosition = Math.max(0.0, servoPosition); // Ensure servo does not drop below 0.0
        }

        // Update previous button states to prevent repeated increments while held.
        dPadRightStatus = gamepad1.dpad_right;
        dPadLeftStatus = gamepad1.dpad_left;

        // Apply the new servo position.
        servo.setPosition(servoPosition);

        // Display servo position in telemetry for debugging.
        telemetry.addData("Servo Position", servoPosition);
        telemetry.update();
    }
}