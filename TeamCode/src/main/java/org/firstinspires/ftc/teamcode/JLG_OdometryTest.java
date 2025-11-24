package org.firstinspires.ftc.teamcode;

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




@TeleOp(name = "Odometry Drive", group = "Teleop")

public class JLG_OdometryTest extends LinearOpMode {
    private DcMotor frontRight;
    private DcMotor frontLeft;
    private DcMotor backRight;
    private DcMotor backLeft;

    private Servo lift;

    private GoBildaPinpointDriver pinpoint;

    private ElapsedTime servoTimer = new ElapsedTime();

    private double liftPosition;




    @Override
    public void runOpMode() {
        hardwareStart();
        double speed = 0.5;
        lift.setPosition(0);
        servoTimer.reset();
        waitForStart();

        while(opModeIsActive()) {

            pinpoint.update();

            double forward = -gamepad1.left_stick_y;
            double strafe = gamepad1.left_stick_x;
            double turn = gamepad1.right_stick_x;

            double frontLeftPower = (forward + strafe + turn) * speed;
            double backLeftPower = (forward - strafe + turn) * speed;
            double frontRightPower = (forward - strafe - turn) * speed;
            double backRightPower = (forward + strafe - turn) * speed;

            frontLeft.setPower(frontLeftPower);
            frontRight.setPower(frontRightPower);
            backLeft.setPower(backLeftPower);
            backRight.setPower(backRightPower);

            if (gamepad1.dpad_left) {
                strafe = -0.5;
                sleep(5);
                strafe = -gamepad1.left_stick_x;
            } else if (gamepad1.dpad_right){
                strafe = 0.5;
                sleep(5);
                strafe = -gamepad1.left_stick_x;
            }

            if (gamepad1.left_bumper) {
                speed = 1; //turooo
            } else if (gamepad1.right_bumper) {
                speed = 0.25;
            } else {
                speed = 0.5;
            }

            if(gamepad1.a) {
                pinpoint.resetPosAndIMU();
            }

            if (gamepad1.dpad_up && servoTimer.milliseconds() > 500) {
                lift.setPosition(0.5);
                servoTimer.reset();
            }

            if (gamepad1.dpad_down && servoTimer.milliseconds() > 500) {
                lift.setPosition(0.9);
                servoTimer.reset();

            }

            if (gamepad1.dpad_left && servoTimer.milliseconds() > 500) {
                lift.setPosition(0.2);
                servoTimer.reset();
            }




            telemetry.addData("X (in)", pinpoint.getPosX(DistanceUnit.INCH));
            telemetry.addData("Y (in)", pinpoint.getPosY(DistanceUnit.INCH));
            telemetry.addData("Heading", pinpoint.getHeading(AngleUnit.DEGREES));
            telemetry.addData("Lift Position", lift.getPosition());
            telemetry.update();


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

        lift = hardwareMap.get(Servo.class, "lift");
        //((PwmControl) lift).setPwmRange(new PwmControl.PwmRange(0.9, 2.1));





        telemetry.addData("Status","Initialized");
        //blow up world... just not this house
    }

    // Move the robot to a target X/Y coordinate with a desired heading
    private void goToPose(double targetX, double targetY, double targetHeading, double power) {
        // Keep running until opmode ends
        while (opModeIsActive()) {
            // Refresh odometry readings from Pinpoint
            pinpoint.update();

            // Get current position and heading from odometry
            double currentX = pinpoint.getX();
            double currentY = pinpoint.getY();
            double currentHeading = pinpoint.getHeading();

            // Calculate error (difference) between target and current position
            double dx = targetX - currentX;
            double dy = targetY - currentY;

            // If robot is close enough to target (within 0.5 in and 2°), stop
            if (Math.abs(dx) < 0.5 && Math.abs(dy) < 0.5 &&
                    Math.abs(currentHeading - targetHeading) < 2.0) {
                setPowers(0, 0, 0, 0); // stop all motors
                break;                 // exit loop
            }

            // Normalize direction vector (dx, dy) to unit length
            double magnitude = Math.sqrt(dx*dx + dy*dy);
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
}

