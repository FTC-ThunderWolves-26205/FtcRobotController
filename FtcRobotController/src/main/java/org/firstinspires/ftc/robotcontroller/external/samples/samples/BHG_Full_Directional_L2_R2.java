package org.firstinspires.ftc.robotcontroller.external.samples.samples;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@TeleOp(name = "BHG Full L2 R2", group = "TeleOp")
@Disabled
public class BHG_Full_Directional_L2_R2 extends LinearOpMode{
    private DcMotor leftDrive;
    private DcMotor rightDrive;

     TouchSensor touchSensor; //Have to do this somewhere I guess....
     DistanceSensor distanceSensor;
    public VisionPortal visionPortal; // Declares the VisionPortal for handling camera input
    public AprilTagProcessor aprilTagProcessor; // Declares the AprilTag processor for detecting AprilTags

    @Override
    public void runOpMode() {
        starter();
        telemetry.addData("Status", "Ready to start");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            touchSensor = hardwareMap.get(TouchSensor.class, "touchSensor"); //You could instead do TouchSensor touchSensor = hardwareMap.get(TouchSensor.class, "touchSensor");
            distanceSensor = hardwareMap.get(DistanceSensor.class, "distanceSensor");

            double turn = gamepad1.left_stick_x;

            if(gamepad1.right_trigger > 0) {
                leftDrive.setPower(-gamepad1.right_trigger * 0.5 + turn);
                rightDrive.setPower(-gamepad1.right_trigger * 0.5 - turn);
            } else if(gamepad1.left_trigger > 0) {

                leftDrive.setPower(gamepad1.left_trigger * 0.5 - turn);
                rightDrive.setPower(gamepad1.left_trigger * 0.5 + turn);
            } else if(turn != 0) {
                leftDrive.setPower(turn);
                rightDrive.setPower(-turn);
            } else {
                leftDrive.setPower(0);
                rightDrive.setPower(0);
            }
            boolean isPressed = touchSensor.isPressed();
            if (isPressed) {
                leftDrive.setPower(0.5);
                rightDrive.setPower(0.5);
                sleep(3000);
                leftDrive.setPower(0);
                rightDrive.setPower(0);
            }
            double distance = distanceSensor.getDistance(DistanceUnit.INCH);
            if (Double.isNaN(distance)) {
                telemetry.addData("Distance", "Invalid");
                telemetry.update();
                continue; // Skip loop iteration if the distance is invalid
            }
            handleDistance();
            List<AprilTagDetection> detections = aprilTagProcessor.getDetections(); // Retrieves detected AprilTags
            if (detections.isEmpty()) {
                telemetry.addData("NO TAG DETECTED", "No AprilTags found");

            } else {


                for (AprilTagDetection tag : detections) { // Loops through all detected AprilTags
                    telemetry.addData("AprilTag Detected!", "ID: %d", tag.id);



                }


            }
            telemetry.addData("Distance", distanceSensor.getDistance(DistanceUnit.INCH));
            telemetry.addData("Touch Sensor", isPressed ? "PRESSED" : "NOT PRESSED");

            // Send telemetry data

            telemetry.addData("Left Power", turn);
            telemetry.addData("Right Power", -turn);
            telemetry.update();
        }
        visionPortal.close();

    }
    private void starter() {
        leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive = hardwareMap.get(DcMotor.class, "right_drive");

        // Set motor direction (adjust based on physical setup)
        leftDrive.setDirection(DcMotor.Direction.REVERSE);
        rightDrive.setDirection(DcMotor.Direction.FORWARD);

        aprilTagProcessor = new AprilTagProcessor.Builder().build(); // Creates an AprilTagProcessor instance

        // Create VisionPortal with webcam
        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1")) // Uses the configured webcam
                .addProcessor(aprilTagProcessor) // Adds the AprilTag processor to handle detections
                .build(); // Builds the VisionPortal instance

        telemetry.addData("Status", "Initialized");
        telemetry.update();

    }
    private void handleDistance() {
        double distance = distanceSensor.getDistance(DistanceUnit.INCH);
        while (distance <= 12 && opModeIsActive()) {
            leftDrive.setPower(0.3);
            rightDrive.setPower(0.3);
            sleep(500);


            // Update the distance for the next iteration
            distance = distanceSensor.getDistance(DistanceUnit.INCH);

        }

    }
}
