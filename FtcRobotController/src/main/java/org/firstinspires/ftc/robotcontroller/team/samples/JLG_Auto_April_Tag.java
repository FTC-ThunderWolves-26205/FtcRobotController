package org.firstinspires.ftc.robotcontroller.team.samples;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;


@Autonomous(name = "JLG_Auto_April_Tag", group = "Autonomous")
@Disabled
public class JLG_Auto_April_Tag extends LinearOpMode {

    //Declaring Variables for Motors and Distance Sensor
    private DcMotor leftDrive;
    private DcMotor rightDrive;
    DistanceSensor distanceSensor;

    //Declaring Variables for Autodrive Stuff
    final double    DESIRED_DISTANCE = 8.0; //Sets desired distance to 8 inches.  Let's figure out how to set it to cm later.
    final double    SPEED_GAIN = 0.02; //
    final double    TURN_GAIN = 0.01;
    final double    MAX_AUTO_SPEED = 0.5;
    final double    MAX_AUTO_TURN = 0.25;

    //Declaring Variables for the AprilTag
    private static final int DESIRED_TAG_ID = 12; //Set to -1 for any tag, apparently?
    private VisionPortal visionPortal; // Manages video source
    private AprilTagProcessor aprilTag; // Manages AprilTag detection process
    private AprilTagDetection desiredTag = null; // Holds data for a detected AprilTag

    @Override
    public void runOpMode() throws InterruptedException {

        boolean targetFound = false;  //Variable to define if AprilTag has been found
        double drive = 0;  //Forward power/speed (-1 to +1) +ve is forward
        double turn = 0;  //Turning power/speed (-1 to +1) +ve is CounterClockwise

        //This goes to a class at the bottom.  Initializes the AprilTag Detection process
        aprilTag = new AprilTagProcessor.Builder().build();
        aprilTag.setDecimation(2);

        visionPortal = new VisionPortal.Builder()
                .setCamera(BuiltinCameraDirection.BACK)
                .addProcessor(aprilTag)
                .build();


        // Standard Drive Stuff for 2 motors we know this already
        leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        distanceSensor = hardwareMap.get(DistanceSensor.class, "distanceSensor");
        leftDrive.setDirection(DcMotor.Direction.REVERSE);
        rightDrive.setDirection(DcMotor.Direction.FORWARD);

        //We can use the below line to mess with exposure time of the webcam.  Motion blur can mess up settings I guess?



        telemetry.addData("Status", "Initialized" ,"Camera Stream Available");
        telemetry.addData("<", "Touch START to start OpMode");

        waitForStart();


        //Okay here is where it's going to get messy.  I want the robot to drive around randomly, but when it sees AprilTag, it approaches it
        while (opModeIsActive()) {


            targetFound = false;
            desiredTag = null;

            List<AprilTagDetection> currentDetections = aprilTag.getDetections();

            for (AprilTagDetection detection : currentDetections) { // This goes through the list of detected tags
                //Better to start the if with tag detected or no tag detected?  Test if it works with detection.metadata == null
                if (detection.metadata != null) {
                    if ((DESIRED_TAG_ID == 12) || (detection.id == DESIRED_TAG_ID)) {
                        targetFound = true;
                        desiredTag = detection;
                        break; }// This makes it stop looking since it found the tag




                    }
                }

            if (targetFound) {
                double rangeError = (desiredTag.ftcPose.range - DESIRED_DISTANCE);
                double headingError = desiredTag.ftcPose.bearing;
                drive = -rangeError * SPEED_GAIN;
                turn = headingError * TURN_GAIN;
                }
           else {
            double distance = distanceSensor.getDistance(DistanceUnit.INCH);

            if (distance > 12) {
                // Move forward
                leftDrive.setPower(-0.35);
                rightDrive.setPower(-0.35);
            } else if (distance <= 12) {
                // Reverse when too close
                leftDrive.setPower(0.25);
                rightDrive.setPower(0.25);
                sleep(1500);
                leftDrive.setPower(-0.5);
                rightDrive.setPower(0);
                sleep(1000);
            }




            }



            }



        }
    }

