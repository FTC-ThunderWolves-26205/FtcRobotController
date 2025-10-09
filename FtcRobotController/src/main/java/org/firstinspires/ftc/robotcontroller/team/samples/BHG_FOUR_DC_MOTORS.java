package org.firstinspires.ftc.robotcontroller.team.samples;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@TeleOp(name = "Ben 4 Motors", group = "Teleop")
@Disabled
public class BHG_FOUR_DC_MOTORS extends LinearOpMode{
    public DcMotor frontLeft;
    public DcMotor frontRight;
    public DcMotor backLeft;
    public DcMotor backRight;
    Servo servo;
    TouchSensor touchSensor;
    DistanceSensor distanceSensor;

    public VisionPortal visionPortal;
    public AprilTagProcessor aprilTagProcessor;

    @Override
    public void runOpMode() {
        hardwareStart();
        double speed = 1.5;
        double servoPosition = 0;
        boolean isPosition = false;

        waitForStart();

        while (opModeIsActive()) {
            touchSensor = hardwareMap.get(TouchSensor.class, "touchSensor");
            distanceSensor = hardwareMap.get(DistanceSensor.class, "distanceSensor");

            double forward = gamepad1.left_stick_y;
            double strafe = -gamepad1.left_stick_x;
            double turn = -gamepad1.right_stick_x/2;
            boolean isPressed = touchSensor.isPressed();

            double frontLeftPower = (forward + strafe + turn) * speed;
            double frontRightPower = (forward - strafe - turn) * speed;
            double backLeftPower = (forward - strafe + turn) * speed;
            double backRightPower = (forward + strafe - turn) * speed;

            frontLeft.setPower(frontLeftPower);
            frontRight.setPower(frontRightPower);
            backLeft.setPower(backLeftPower);
            backRight.setPower(backRightPower);
            servo.setPosition(servoPosition);

             if(gamepad1.a){
                frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            } else if(gamepad1.b) {
                frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            }

           if(servoPosition == 0) {
                isPosition = false;
            } else if(servoPosition == 0.3) {
                isPosition = true;
            }

            if(gamepad1.right_trigger > 0) {
                speed = 1;
            } else if(gamepad1.left_stick_button) {
                speed = 1;
            } else {
                speed = 0.5;
            }

            if(gamepad1.left_bumper && !isPosition) {
                servoPosition = 0.3;
            } else if(gamepad1.left_bumper && isPosition) {
                servoPosition = 0;
            }

            List<AprilTagDetection> detections = aprilTagProcessor.getDetections(); // Retrieves detected AprilTags
            if (detections.isEmpty()) {
                telemetry.addData("NO TAG DETECTED", "No AprilTags found");
            } else {


                for (AprilTagDetection tag : detections) { // Loops through all detected AprilTags
                    telemetry.addData("AprilTag Detected!", "ID: %d", tag.id);



                }


            }

            double distance = distanceSensor.getDistance(DistanceUnit.INCH);
            if (Double.isNaN(distance)) {
                telemetry.addData("Distance", "Invalid");
                telemetry.update();
                continue;
            }
            if(distance <= 12) {
                speed = 0.25; //Makes the robot slower so I can move it away from stuff
            }

            if(speed > 0.5) {
                telemetry.addData("State of Speed", "Boosted");
            } else if(speed < 0.5) {
                telemetry.addData("State of Speed", "Diminshed");
            } else {
                telemetry.addData("State of Speed", "Normal");
            }
            telemetry.addData("Forward", forward);
            telemetry.addData("Turn", turn);
            telemetry.addData("Strafe", strafe);
            telemetry.addData("FL Encoder", frontLeft.getCurrentPosition());
            telemetry.addData("FR Encoder", frontRight.getCurrentPosition());
            telemetry.addData("BL Encoder", backLeft.getCurrentPosition());
            telemetry.addData("BR Encoder", backRight.getCurrentPosition());
            telemetry.addData("Speed Multiplier Level", speed*2);
            telemetry.addData("Touch Sensor", isPressed ? "PRESSED" : "NOT PRESSED");
            telemetry.addData("Distance", distance);
            telemetry.update();

        }
    }
    private void hardwareStart() {
        frontLeft = hardwareMap.get(DcMotor.class, "FL");
        frontRight  = hardwareMap.get(DcMotor.class, "FR");
        backLeft = hardwareMap.get(DcMotor.class, "BL");
        backRight  = hardwareMap.get(DcMotor.class, "BR");
        //servo = hardwareMap.get(Servo.class, "servo");

        aprilTagProcessor = new AprilTagProcessor.Builder().build(); // Creates an AprilTagProcessor instance

        // Create VisionPortal with webcam
        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1")) // Uses the configured webcam
                .addProcessor(aprilTagProcessor) // Adds the AprilTag processor to handle detections
                .build(); // Builds the VisionPortal instance

        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);


        telemetry.addData("Status","Initialized");
        telemetry.update();
    }
}
