package org.firstinspires.ftc.robotcontroller.external.samples.samples;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;


@Autonomous(name = "JLG_Auto_April_Tag_Driving2", group = "Autonomous")
@Disabled
public class JLG_Auto_April_Tag_Driving_2 extends LinearOpMode {

    //Declaring Variables for Motors and Distance Sensor

    //Pink means its a variable we created that is used at some point
    private DcMotor leftDrive;  //On this line, we are assigning a variable "leftDrive" of type DcMotor.
    private DcMotor rightDrive;  //The DcMotor function(?) comes from the com.qualcomm.robotcore.hardware.DcMotor class.
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
        //double drive = 0;  //Forward power/speed (-1 to +1) +1 is forward
        //double turn = 0;  //Turning power/speed (-1 to +1) +1 is CounterClockwise

        initAprilTag(); // This is a method that can be found at the bottom.  I don't think in this case we need to use a method, but apparently it's good practice.  Let's talk about it.
        initDrive();  // same deal here, just playing with methods

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

            double distance = distanceSensor.getDistance(DistanceUnit.INCH);
            telemetry.addData("Distance", "%.2f in", distance);
            telemetry.update();

//Testing
            //Try to edit code below to have the robot approach the tag and stop at 3 inches.
            //Need to resolve the robot automatically doing its random routine when <12 inches though.

            if (targetFound) {
                double rangeError = (desiredTag.ftcPose.range - DESIRED_DISTANCE);
                double headingError = desiredTag.ftcPose.bearing;
                telemetry.addData("Target Found", "True");


                if (desiredTag.ftcPose.range > 6) {
                    telemetry.addData("Target Found And", "Distance > 6");
                    double drive = -rangeError * SPEED_GAIN;
                    double turn = headingError * TURN_GAIN;
                    leftDrive.setPower(drive + turn);
                    rightDrive.setPower(drive - turn);
                } else {
                    telemetry.addData("Target Found And", "Distance <= 6");
                    leftDrive.setPower(0);
                    rightDrive.setPower(0);
                }


            }

                if (distance > 12 && !targetFound) {
                    // Move forward
                    telemetry.addData("Distance", "Greater Than 12");
                    leftDrive.setPower(-0.35);
                    rightDrive.setPower(-0.35);
                } else if (distance <= 12 && !targetFound) {
                    telemetry.addData("Distance", "Less Than 12");
                    //So I asked CoPilot how to generate a number between -0.5 and 0.5.  It gave me several ways to do it, this seemed to be the most simple.
                    //Math.random() generates a random double between 0 and 1, so we subtract 0.5 from it to get a number between -0.5 and 0.5.
                    //This approach doesn't require any extra import statements at the top since math methods are always included.  Other options use import java.util.Random

                    double randomNumberX = Math.random() - 0.5;
                    double randomNumberY = Math.random() - 0.5;

                    // Trying to make it do random stuff when the distance sensor triggers.
                    leftDrive.setPower(randomNumberX);
                    rightDrive.setPower(randomNumberY);
                    sleep(1500);
                    
                }








        }



    }
    private void initAprilTag(){
        // Initializes the AprilTag Detection process
        aprilTag = new AprilTagProcessor.Builder().build();

        //This can be changed to trade-off detection-range for detection-rate.
        aprilTag.setDecimation(2);

        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .addProcessor(aprilTag)
                .build();
    }
    private void initDrive() {
        leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        distanceSensor = hardwareMap.get(DistanceSensor.class, "distanceSensor");
        leftDrive.setDirection(DcMotor.Direction.REVERSE);
        rightDrive.setDirection(DcMotor.Direction.FORWARD);}
}

/*
if (!targetFound && distance > 12) {
    leftDrive.setPower(-0.35);
    rightDrive.setPower(-0.35);
}
else if (!targetFound && distance <= 12) {
    double randomNumberX = Math.random() - 0.5;
    double randomNumberY = Math.random() - 0.5;

    leftDrive.setPower(randomNumberX);
    rightDrive.setPower(randomNumberY);
    sleep(1500);
    leftDrive.setPower(randomNumberX);
    rightDrive.setPower(randomNumberY);
    sleep(1000);
}
else if (targetFound) {
    double rangeError = (desiredTag.ftcPose.range - DESIRED_DISTANCE);
    double headingError = desiredTag.ftcPose.bearing;

    if (desiredTag.ftcPose.range > 6) {
        double drive = -rangeError * SPEED_GAIN;
        double turn = headingError * TURN_GAIN;
        leftDrive.setPower(drive + turn);
        rightDrive.setPower(drive - turn);
    } else {
        leftDrive.setPower(0);
        rightDrive.setPower(0);
    }
}
 */
