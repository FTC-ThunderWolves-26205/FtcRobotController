package org.firstinspires.ftc.robotcontroller.team.samples;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "MCM_Test", group = "TeleOp")
public class MCM_Test extends LinearOpMode{
    private DcMotor frontLeft;
    private DcMotor frontRight;

    @Override
    public void runOpMode() {
        starter();
        telemetry.addData("Status", "Ready to start");
        telemetry.addData("me", " hello world");
        telemetry.update();

        frontRight  = hardwareMap.get(DcMotor.class, "Front Right");
        frontLeft = hardwareMap.get(DcMotor.class, "Front Left");

        waitForStart();

        while (opModeIsActive()) {

            if (gamepad1.a) {
                frontRight.setPower(1);
            } else {
                frontRight.setPower(0);
            }





        }


    }
    private void starter() {

        // Set motor direction (adjust based on physical setup)
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);


        telemetry.addData("Status", "Initialized");
        telemetry.update();

    }
   /* private void handleDistance() {
        double distance = distanceSensor.getDistance(DistanceUnit.INCH);
        while (distance <= 12 && opModeIsActive()) {
            leftDrive.setPower(0);
            rightDrive.setPower(0);
            sleep(500);
            leftDrive.setPower(gamepad1.left_stick_y);
            rightDrive.setPower(gamepad1.left_stick_y);

            // Update the distance for the next iteration
            distance = distanceSensor.getDistance(DistanceUnit.INCH);
        }

    }*/
}