package org.firstinspires.ftc.robotcontroller.team.samples;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp(name = "JLG_Full_Directional", group = "TeleOp")
@Disabled
public class JLG_Full_Directional extends LinearOpMode {

    private DcMotor leftDrive;
    private DcMotor rightDrive;

    TouchSensor touchSensor;
    DistanceSensor distanceSensor;


    @Override
    public void runOpMode() {
        // Initialize motors
        leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive = hardwareMap.get(DcMotor.class, "right_drive");

        //Resetting and Initializing Encoders
        leftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Set motor direction (adjust based on physical setup)
        leftDrive.setDirection(DcMotor.Direction.REVERSE);
        rightDrive.setDirection(DcMotor.Direction.FORWARD);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        touchSensor  = hardwareMap.get(TouchSensor.class, "touchSensor");
        distanceSensor = hardwareMap.get(DistanceSensor.class, "distanceSensor");

        // Wait for driver to press the start button
        waitForStart();

        while (opModeIsActive()) {


            // Get gamepad input
            double forward = gamepad1.left_stick_y; // Forward/Backward
            double turn = -gamepad1.right_stick_x;   // Left/Right turning

            //Speed Modifiers
            double stopPower = 0;

            // Get encoder counts
            int leftEncoder = leftDrive.getCurrentPosition();
            int rightEncoder = rightDrive.getCurrentPosition();

            // Calculate motor power
            double leftPower = forward - turn;
            double rightPower = forward + turn;

            // Apply power to motors

            leftDrive.setPower(leftPower * 0.5);
            rightDrive.setPower(rightPower * 0.5);

            double leftDistance = distanceSensor.getDistance(DistanceUnit.CM);

            if (leftDistance < 10) {
                leftDrive.setPower(0.5);
                rightDrive.setPower(-0.5);
                sleep(500);
                }
            else {
                leftDrive.setPower(leftPower);
                rightDrive.setPower(leftPower);
            }

            boolean isPressed = touchSensor.isPressed();
            if(isPressed) {
                leftDrive.setPower(0.5);
                rightDrive.setPower(0.5);
                sleep(2000);
                leftDrive.setPower(0);
                rightDrive.setPower(0);
              }

            /* Making Buttons do Stuff */
            if (gamepad1.x) { //Press X to STOP
                leftDrive.setPower(stopPower);
                rightDrive.setPower(stopPower);
            }
            if (gamepad1.right_bumper) { //Press RIGHT BUMPER to SPEED UP
                leftDrive.setPower(leftPower);
                rightDrive.setPower(rightPower);
            }

            //boolean slowMode = gamepad1.left_bumper; // Capture input at start of loop
            //double speedFactor = slowMode ? 0.25 : 1.0;

            //leftDrive.setPower(leftPower * speedFactor);
            //rightDrive.setPower(rightPower * speedFactor);

            if (gamepad1.left_bumper) { //Press LEFT BUMPER to SLOW DOWN
                leftDrive.setPower(leftPower * 0.25);
                rightDrive.setPower(rightPower * 0.25);
            }
            if (gamepad1.y) {  //PRESS Y to RESET ENCODERS
                leftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                rightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            }
            if (gamepad1.a) {//HOLD A to MOVE LEFT THEN RIGHT??
                long pressStartTime = System.currentTimeMillis();

                while (gamepad1.a) {
                    long timePressed = System.currentTimeMillis() - pressStartTime;

                    if (timePressed >= 2000) {
                        gamepad1.rumble(500);
                        leftDrive.setPower(1.00);
                        rightDrive.setPower(1.00);
                        sleep(2000);
                        leftDrive.setPower(0);
                        rightDrive.setPower(1.00);
                        sleep(2000);
                        rightDrive.setPower(0);
                        leftDrive.setPower(1.00);
                        sleep(2000);
                        rightDrive.setPower(0);
                        leftDrive.setPower(0);

                        break;
                    }

                }


            }

                telemetry.addData("Distance Sensor", distanceSensor.getDistance(DistanceUnit.INCH));

                //Display Encoder Values on Drive Station
                telemetry.addData("Left Motor Encoder", leftEncoder);
                telemetry.addData("Right Motor Encoder", rightEncoder);

                // Send telemetry data
                telemetry.addData("Left Power", leftPower);
                telemetry.addData("Right Power", rightPower);


                if (gamepad1.a) {
                    telemetry.addData("Button Pressed", "A");
                    }
                else if (gamepad1.b) {
                    telemetry.addData("Button Pressed", "B");
                    }
                else if (gamepad1.x) {
                    telemetry.addData("Button Pressed", "X");
                    }
                else if (gamepad1.y) {
                    telemetry.addData("Button Pressed", "Y");
                    }
                else if (gamepad1.left_bumper) {
                        telemetry.addData("Button Pressed", "Left Bumper");
                    }
                else if (gamepad1.right_bumper) {
                        telemetry.addData("Button Pressed", "Right Bumper");
                    }
                else if (gamepad1.left_trigger > 0.1) {
                        telemetry.addData("Button Pressed", "Left Trigger");
                        }
                else if (gamepad1.right_trigger > 0.1) {
                        telemetry.addData("Button Pressed", "Right Trigger");
                    }
                else {
                        telemetry.addData("Button Pressed", "None");
                    }

                telemetry.update();//move???>??


                }
            }
        }



