package org.firstinspires.ftc.robotcontroller.external.samples.samples;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Autonomous(name = "JLG_OA_Mecanum", group = "Autonomous")
@Disabled
public class JLG_OA_Mecanum extends LinearOpMode {
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

        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);



        distanceSensor = hardwareMap.get(DistanceSensor.class, "distanceSensor");
        // Set motor direction (adjust based on physical setup)

        telemetry.addData("Status", "Initialized");

        waitForStart();

        while (opModeIsActive()) {
            double distance = distanceSensor.getDistance(DistanceUnit.INCH);

            if (Double.isNaN(distance)) {
                telemetry.addData("Distance", "Invalid");
                telemetry.update();
                continue; // Skip loop iteration if the distance is invalid
            }

            if (gamepad1.x) {
                frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            }

            if (gamepad1.y) {
                frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            }
//bkdkdk

            if (distance > 12) {
                frontLeft.setPower(-0.5);
                frontRight.setPower(-0.5);
                backLeft.setPower(-0.5);
                backRight.setPower(-0.5);
            }
            else if (distance <= 12) {
                frontLeft.setPower(0.5);
                frontRight.setPower(0.5);
                backLeft.setPower(0.5);
                backRight.setPower(0.5);
            }

            telemetry.addData("Distance", distance);
            telemetry.addData("FL Position:", frontLeft.getCurrentPosition());
            telemetry.addData("FR Position:", frontRight.getCurrentPosition());
            telemetry.addData("BL Position:", backLeft.getCurrentPosition());
            telemetry.addData("BR Position:", backRight.getCurrentPosition());

            telemetry.update();
        }
    }
}
