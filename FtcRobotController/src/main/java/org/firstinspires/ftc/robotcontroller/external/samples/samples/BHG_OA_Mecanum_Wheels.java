package org.firstinspires.ftc.robotcontroller.external.samples.samples;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Autonomous(name = "Ben Object Avoidance Mecanum Wheels", group = "Autonomous")
@Disabled
public class BHG_OA_Mecanum_Wheels extends LinearOpMode {
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;
    DistanceSensor distanceSensor;

    @Override
    public void runOpMode() throws InterruptedException {
        frontLeft = hardwareMap.get(DcMotor.class, "FL");
        frontRight  = hardwareMap.get(DcMotor.class, "FR");
        backLeft = hardwareMap.get(DcMotor.class, "BL");
        backRight  = hardwareMap.get(DcMotor.class, "BR");

        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.FORWARD);
        distanceSensor = hardwareMap.get(DistanceSensor.class, "distanceSensor");
        // Set motor direction (adjust based on physical setup)

        telemetry.addData("Status", "Initialized");

        waitForStart();//hi

        while (opModeIsActive()) {
            double distance = distanceSensor.getDistance(DistanceUnit.INCH);

            if (Double.isNaN(distance)) {
                telemetry.addData("Distance", "Invalid");
                telemetry.update();
                continue; // Skip loop iteration if the distance is invalid
            }

            if (distance > 12) {
                frontLeft.setPower(0.25);
                frontRight.setPower(0.25);
                backLeft.setPower(0.25);
                backRight.setPower(0.25);
            } else if (distance <= 12) {
               frontLeft.setPower(-0.25);
               frontRight.setPower(-0.25);
               backLeft.setPower(-0.25);
               backRight.setPower(-0.25);
               sleep(1500);
               frontLeft.setPower(0.5);
               backLeft.setPower(0.5);
               frontRight.setPower(0);
               backRight.setPower(0);
               sleep(1000);
            }
            telemetry.addData("Distance", distance);
            telemetry.update();
        }
    }
}
//