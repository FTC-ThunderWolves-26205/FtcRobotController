package org.firstinspires.ftc.robotcontroller.team.samples;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

public class JLG_Threads extends LinearOpMode {
    @Disabled
    private DcMotor leftDrive;
    private DcMotor rightDrive;
    private DistanceSensor distanceSensor;
    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTagProcessor;
    private volatile boolean tagDetected = false;

    @Override
    public void runOpMode() throws InterruptedException {
        leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        distanceSensor = hardwareMap.get(DistanceSensor.class, "distance_sensor");

        // Initialize camera and AprilTag detection using FTC VisionPortal
        aprilTagProcessor = AprilTagProcessor.easyCreateWithDefaults();
        visionPortal = VisionPortal.easyCreateWithDefaults(
                hardwareMap.get(WebcamName.class, "webcam"), aprilTagProcessor);

        Thread tagDetectionThread = new Thread(() -> {
            while (opModeIsActive()) {
                for (AprilTagDetection detection : aprilTagProcessor.getDetections()) {
                    if (detection.id == 11) {
                        telemetry.addData("AprilTag Detected!", detection.id);
                        tagDetected = true;
                        break;
                    }
                }
                telemetry.update();
                sleep(100);
            }
        });

        Thread movementThread = new Thread(() -> {
            while (opModeIsActive() && !tagDetected) {
                // Drive in a circle: One motor faster than the other
                leftDrive.setPower(0.5);
                rightDrive.setPower(0.2);
                sleep(100);
            }

            // Stop circular motion
            leftDrive.setPower(0);
            rightDrive.setPower(0);
            sleep(500);

            // Move towards the detected AprilTag
            while (opModeIsActive() && tagDetected && distanceSensor.getDistance(DistanceUnit.INCH) > 6) {
                leftDrive.setPower(0.4);
                rightDrive.setPower(0.4);
                sleep(100);
            }

            // Stop when within 6 inches of the AprilTag
            leftDrive.setPower(0);
            rightDrive.setPower(0);
        });

        telemetry.addData("Status", "Waiting for start...");
        telemetry.update();
        waitForStart();

        tagDetectionThread.start();
        movementThread.start();

        tagDetectionThread.join();
        movementThread.join();
    }
}