package org.firstinspires.ftc.robotcontroller.external.samples.samples; // Defines the package where this class is located.

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;


@TeleOp(name = "JLG_Encoder_Equalize", group = "LinearOpMode") // Annotates the class as a TeleOp mode with a name and group.
@Disabled
public class JLG_Encoder_Equalize extends LinearOpMode { // Begins the class definition for the TeleOp mode.

    private DcMotor leftMotor; // Declares the left motor variable.
    private DcMotor rightMotor; // Declares the right motor variable.

    private static final double BASE_POWER = 1; // Sets the default motor power for driving.
    private static final double CORRECTION_FACTOR = 0.02; // Defines the factor for adjusting motor power discrepancies.


    @Override
    public void runOpMode() { // Main method that runs when the Driver Station starts the TeleOp mode.
        // Initialize the left and right motors using hardwareMap to link them with the robot's configuration.
        leftMotor = hardwareMap.get(DcMotor.class, "left_drive");
        rightMotor = hardwareMap.get(DcMotor.class, "right_drive");

        // Set the direction of the motors to ensure proper forward/reverse motion.
        leftMotor.setDirection(DcMotor.Direction.FORWARD);
        rightMotor.setDirection(DcMotor.Direction.REVERSE);

        // Reset encoders to start tracking movements from zero position.
        leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Set encoders to RUN_USING_ENCODER mode for real-time position tracking.
        leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // *** IMPORTANT: Set zero power behavior to BRAKE ***
       // leftMotor.ZeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE;
       // rightMotor.ZeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE;

        waitForStart(); // Waits for the start button to be pressed on the Driver Station.

        while (opModeIsActive()) { // Main loop that runs as long as the TeleOp mode is active.
            // Get the Y-axis value of the left stick to determine forward or reverse motion.
            double drivePower = -gamepad1.left_stick_y; // Negate the value because forward on the stick is negative.
            double turnPower = gamepad1.right_stick_x;// Get the X-axis value of the right stick for turning.

            // Retrieve the current encoder positions for both motors.
            int leftPosition = leftMotor.getCurrentPosition();
            int rightPosition = rightMotor.getCurrentPosition();

            // Set initial power values for both motors based on the joystick input.
            double leftPower = BASE_POWER * drivePower - turnPower;
            double rightPower = BASE_POWER * drivePower + turnPower;


            // Adjust power values to correct discrepancies between the motors.
            if (leftPosition < rightPosition) {
                leftPower += CORRECTION_FACTOR; // Boost power for the left motor if it's lagging behind.
                rightPower -= CORRECTION_FACTOR; // Reduce power for the right motor to match the left.
            } else if (rightPosition < leftPosition) {
                leftPower -= CORRECTION_FACTOR; // Reduce power for the left motor to match the right.
                rightPower += CORRECTION_FACTOR; // Boost power for the right motor if it's lagging behind.
            }

            if (gamepad1.left_stick_y == 0 && gamepad1.right_stick_x == 0) {
                leftPower = 0;
                rightPower = 0;
            }


            if(gamepad1.x) {
                leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

            }
           if(gamepad1.y) {
               leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
               rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
           }

            // Apply corrected power values to the motors to drive the robot.
            leftMotor.setPower(leftPower);
            rightMotor.setPower(rightPower);

            // Display telemetry data for debugging and monitoring encoder positions and motor power.
            telemetry.addData("Left Motor Position", leftPosition); // Shows the encoder position of the left motor.
            telemetry.addData("Right Motor Position", rightPosition); // Shows the encoder position of the right motor.
            telemetry.addData("Left Motor Power", leftPower); // Displays the current power of the left motor.
            telemetry.addData("Right Motor Power", rightPower); // Displays the current power of the right motor.
            telemetry.update(); // Updates the Driver Station with the latest telemetry data.
        }
    }
}