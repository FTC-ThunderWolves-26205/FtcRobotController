package org.firstinspires.ftc.robotcontroller.team.samples;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@Autonomous(name = "JLG_Auto_AprilTag_Driving_Threads", group = "Autonomous")
@Disabled
public class JLG_Auto_AprilTag_Driving_Threads extends LinearOpMode {

    private DcMotor leftDrive;
    private DcMotor rightDrive;
    private DistanceSensor distanceSensor;
    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTagProcessor;
    private volatile boolean tagDetected = false;
    private volatile double tagRange = Double.MAX_VALUE;

    private static final int DESIRED_TAG_ID = 12;
    private static final double DESIRED_DISTANCE = 6.0; // Stop at 6 inches
    private static final double SPEED_GAIN = 0.02;
    private static final double TURN_GAIN = 0.01;

    @Override
    public void runOpMode() throws InterruptedException {
        initHardware();
        initAprilTag();

        telemetry.addData("Status", "Initialized, waiting for START");
        telemetry.update();
        waitForStart();

        // Start the AprilTag detection thread
        Thread tagDetectionThread = new Thread(() -> {
            while (opModeIsActive()) {
                List<AprilTagDetection> detections = aprilTagProcessor.getDetections();
                for (AprilTagDetection detection : detections) {
                    if (detection.id == DESIRED_TAG_ID) {
                        tagDetected = true;
                        tagRange = detection.ftcPose.range;
                        telemetry.addData("AprilTag Detected!", detection.id);
                        break;
                    }
                }
                telemetry.update();
                sleep(100);
            }
        });

        // Start the movement control thread
        Thread movementThread = new Thread(() -> {
            while (opModeIsActive() && !tagDetected) {
                leftDrive.setPower(0.5);
                rightDrive.setPower(0.2);
                sleep(100);
            }

            leftDrive.setPower(0);
            rightDrive.setPower(0);
            sleep(500);

            while (opModeIsActive() && tagDetected && tagRange > DESIRED_DISTANCE) {
                double rangeError = tagRange - DESIRED_DISTANCE;
                double drive = -rangeError * SPEED_GAIN;
                leftDrive.setPower(drive);
                rightDrive.setPower(drive);
                sleep(100);
            }

            leftDrive.setPower(0);
            rightDrive.setPower(0);
        });

        tagDetectionThread.start();
        movementThread.start();

        tagDetectionThread.join();
        movementThread.join();
    }

    private void initHardware() {
        leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        distanceSensor = hardwareMap.get(DistanceSensor.class, "distanceSensor");
        leftDrive.setDirection(DcMotor.Direction.REVERSE);
        rightDrive.setDirection(DcMotor.Direction.FORWARD);
    }

    private void initAprilTag() {
        aprilTagProcessor = new AprilTagProcessor.Builder().build();
        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .addProcessor(aprilTagProcessor)
                .build();
    }
}