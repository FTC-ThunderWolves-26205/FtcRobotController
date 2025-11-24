package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

import com.qualcomm.robotcore.hardware.ServoControllerEx;

import com.qualcomm.robotcore.hardware.PwmControl;




@Autonomous(name = "Jason's Odometry Drive", group = "Autonomous")

public class JLG_OdometryTest extends LinearOpMode {
    private DcMotor frontRight;
    private DcMotor frontLeft;
    private DcMotor backRight;
    private DcMotor backLeft;

      private GoBildaPinpointDriver pinpoint;





    @Override
    public void runOpMode() {
        hardwareStart();

        waitForStart();

        while(opModeIsActive()) {

            pinpoint.update();

            goToPose(30, 0, 0, 0.5);

            sleep(1500);

            pinpoint.resetPosAndIMU();

            goToPose(0, 0, 90, 0.5);

            sleep(1500);

            break;






        }
    }
    private void hardwareStart() {
        frontRight = hardwareMap.get(DcMotor.class, "FR");
        frontLeft = hardwareMap.get(DcMotor.class, "FL");
        backRight = hardwareMap.get(DcMotor.class, "BR");
        backLeft = hardwareMap.get(DcMotor.class, "BL");

        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        frontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeft.setDirection(DcMotorSimple.Direction.FORWARD);

        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
        pinpoint.setPosition(new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0));



        telemetry.addData("Status","Initialized");

    }

    // Move the robot to a target X/Y coordinate with a desired heading
    private void goToPose(double targetX, double targetY, double targetHeading, double power) {
        // Keep running until opmode ends
        while (opModeIsActive()) {
            // Refresh odometry readings from Pinpoint
            pinpoint.update();

            // Get current position and heading from odometry
            double currentX = pinpoint.getPosX(DistanceUnit.INCH);
            double currentY = pinpoint.getPosY(DistanceUnit.INCH);
            double currentHeading = pinpoint.getHeading(AngleUnit.DEGREES);

            // Calculate error (difference) between target and current position
            double dx = targetX - currentX;
            double dy = targetY - currentY;

            // If robot is close enough to target (within 0.5 in and 2°), stop
            if (Math.abs(dx) < 0.5 && Math.abs(dy) < 0.5 &&
                    Math.abs(currentHeading - targetHeading) < 2.0) {
                setPowers(0, 0, 0, 0); // stop all motors
                return;                 // exit loop
            }

            // Normalize direction vector (dx, dy) to unit length
            double magnitude = Math.sqrt(dx*dx + dy*dy);
            if (magnitude < 0.01) magnitude = 0.01; // prevent divide by zero
            double xPower = (dx / magnitude) * power; // scaled X power
            double yPower = (dy / magnitude) * power; // scaled Y power

            // Heading correction: difference between target and current heading
            double headingError = targetHeading - currentHeading;
            headingError = normalizeHeading(headingError); // wrap to [-180, 180]

            // Apply proportional correction (kH is tuning constant)
            double kH = 0.02;
            double turnPower = headingError * kH;

            // Mecanum drive mixing:
            // Combine forward/strafe (yPower/xPower) with rotation (turnPower)
            double fl = yPower + xPower - turnPower; // front left
            double fr = yPower - xPower + turnPower; // front right
            double bl = yPower - xPower - turnPower; // back left
            double br = yPower + xPower + turnPower; // back right

            // Send calculated powers to motors
            setPowers(fl, fr, bl, br);

            // Telemetry for debugging: show target vs current pose
            telemetry.addData("Target Pose", "(%.1f, %.1f, %.1f°)", targetX, targetY, targetHeading);
            telemetry.addData("Current Pose", "(%.1f, %.1f, %.1f°)", currentX, currentY, currentHeading);
            telemetry.update();
        }
    }

    // Normalize heading error so it always falls between -180° and +180°
    private double normalizeHeading(double heading) {
        while (heading > 180) heading -= 360;
        while (heading <= -180) heading += 360;
        return heading;
    }

    private void setPowers(double frontLeftPower, double frontRightPower, double
            backLeftPower, double backRightPower) {

        frontLeft.setPower(frontLeftPower);
        frontRight.setPower(frontRightPower);
        backLeft.setPower(backLeftPower);
        backRight.setPower(backRightPower);
    }
}

