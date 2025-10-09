package org.firstinspires.ftc.robotcontroller.external.samples.samples;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Autonomous(name = "BHG_Object Avoidance", group = "Autonomous")
@Disabled
public class BHG_Object_Avoidance extends LinearOpMode {
    private DcMotor leftDrive;
    private DcMotor rightDrive;
    DistanceSensor distanceSensor;

    @Override
    public void runOpMode() throws InterruptedException {
        leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        distanceSensor = hardwareMap.get(DistanceSensor.class, "distanceSensor");
        // Set motor direction (adjust based on physical setup)
        leftDrive.setDirection(DcMotor.Direction.REVERSE);
        rightDrive.setDirection(DcMotor.Direction.FORWARD);

        telemetry.addData("Status", "Initialized");

        waitForStart();

        while (opModeIsActive()) {
            double distance = distanceSensor.getDistance(DistanceUnit.INCH);

            if (Double.isNaN(distance)) {
                telemetry.addData("Distance", "Invalid");
                telemetry.update();
                continue; // Skip loop iteration if the distance is invalid
            }

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
            telemetry.addData("Distance", distance);
            telemetry.update();
        }
    }
}