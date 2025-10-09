package org.firstinspires.ftc.robotcontroller.team.samples;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@TeleOp(name = "JLG_Mecanum_Approach", group = "Teleop")
@Disabled
public class JLG_Mecanum_Approach extends LinearOpMode{
    public DcMotor frontLeft;
    public DcMotor frontRight;
    public DcMotor backLeft;
    public DcMotor backRight;

    private AprilTagDetection desiredTag = null;
    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTag;


    // Servo servo;
    //DistanceSensor distanceSensor;

    @Override
    public void runOpMode() {
        hardwareStart();


        // Initializes the AprilTag Detection process
        aprilTag = new AprilTagProcessor.Builder().build();

        //This can be changed to trade-off detection-range for detection-rate.
        aprilTag.setDecimation(2);

        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .addProcessor(aprilTag)
                .build();
        //double speed = 0.5;
        //double servoPosition = 0;
        //boolean isPosition = false;

        waitForStart();

        while (opModeIsActive()) {
            //distanceSensor = hardwareMap.get(DistanceSensor.class, "distanceSensor");

            double forward = gamepad1.left_stick_y;
            double turn = -gamepad1.right_stick_x;
            double strafe = -gamepad1.left_stick_x;

            double frontLeftPower = (forward + turn + strafe) /2;
            double frontRightPower = (forward - turn - strafe) /2;
            double backLeftPower = (forward - turn + strafe) /2;
            double backRightPower = (forward + turn - strafe) /2;

            frontLeft.setPower(frontLeftPower);
            frontRight.setPower(frontRightPower);
            backLeft.setPower(backLeftPower);
            backRight.setPower(backRightPower);

            List<AprilTagDetection> detections; // Retrieves detected AprilTags
            detections = aprilTag.getDetections(); // ✅ instance call

            if (detections.isEmpty()) {
                telemetry.addData("NO TAG DETECTED", "No AprilTags found");
            } else {
                for (AprilTagDetection tag : detections) { // Loops through all detected AprilTags
                    telemetry.addData("AprilTag Detected!", "ID: %d", tag.id);


                }

                telemetry.update(); // Updates the telemetry display with the detection info
            }








            telemetry.update();

        }
    }
    private void hardwareStart() {
        frontLeft = hardwareMap.get(DcMotor.class, "FL");
        frontRight  = hardwareMap.get(DcMotor.class, "FR");
        backLeft = hardwareMap.get(DcMotor.class, "BL");
        backRight  = hardwareMap.get(DcMotor.class, "BR");
               //servo = hardwareMap.get(Servo.class, "servo");

        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);




        telemetry.addData("Status","Initialized");
        telemetry.update();
    }
}
