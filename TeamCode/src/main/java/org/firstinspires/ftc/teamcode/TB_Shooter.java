package org.firstinspires.ftc.teamcode;

/*
This code performs four basic functions:  basic mecanum drive + power-based shooter control + intakes on/off + servo increments.

The basic mecanum drive uses the left stick for forward/backward/strafe and right stick for turning.  Right trigger for turbo.

The shooter control uses dPadUp/dPadDown to incrementally raise/lower the shooter motor power by 0.1, and uses dpadRight to set the power to 1 and to 0.

The outer intake uses leftBumper to turn power to 1 and 0.  The inner intake uses rightBumper to turn power to 1 and 0.

The servo uses x and y to incrementally raise and lower the servo position by 0.1.



TO DO:  For basic testing, we are using .setPower() for the shooter.  But for better control over the shooter motor
speed, we should instead use .setVelocity() with PIDF.


 */


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "Basic Shooter Testing", group = "Teleop")

public class TB_Shooter extends LinearOpMode {
    private DcMotor frontRight;
    private DcMotor frontLeft;
    private DcMotor backRight;
    private DcMotor backLeft;
    private DcMotorEx shooter;
    private DcMotor iIntake;
    private DcMotor oIntake;
    private Servo servo;
    private boolean wasDpadUp = false;
    private boolean wasDpadDown = false;
    private boolean iIntakePressed = false;
    private boolean oIntakePressed = false;
    private ElapsedTime timer = new ElapsedTime();

    @Override
    public void runOpMode() {
        hardwareStart();
        double speed = 0.5;
        double servoPosition = 0;
        double shooterPower = 0;
        double iIntakePower = 0;
        double oIntakePower = 0;
        waitForStart();
        timer.reset();
        while(opModeIsActive()) {

            double forward = -gamepad1.left_stick_y;
            double strafe = gamepad1.left_stick_x;
            double turn = gamepad1.right_stick_x/2;

            double frontLeftPower = (forward + strafe + turn) * speed;
            double backLeftPower = (forward - strafe + turn) * speed;
            double frontRightPower = (forward - strafe - turn) * speed;
            double backRightPower = (forward + strafe - turn) * speed;

            frontLeft.setPower(Math.max(-1.0, Math.min(1.0, frontLeftPower)));//changed all four of the 0.0 to -1.0
            frontRight.setPower(Math.max(-1.0, Math.min(1.0, frontRightPower)));//this is because Mecanum wheels can like,
            backLeft.setPower(Math.max(-1.0, Math.min(1.0, backLeftPower)));//go backwards, and that would've prevented it.
            backRight.setPower(Math.max(-1.0, Math.min(1.0, backRightPower)));
            servo.setPosition(Math.max(0.0, Math.min(1.0, servoPosition)));


            if (gamepad1.dpad_up && !wasDpadUp) {
                if (timer.milliseconds() > 300) {
                    shooterPower += 0.1;
                    timer.reset();
                }
            } else if (gamepad1.dpad_down && !wasDpadDown) {
                if (timer.milliseconds() > 300) {
                    shooterPower -= 0.1;
                    timer.reset();
                }
            } else if(gamepad1.dpad_right && timer.milliseconds() > 500) {
               shooterPower = (shooterPower == 0) ? 1 : 0;
               timer.reset();
            }

            if (gamepad1.right_trigger > 0) {
                speed = 1;
            } else  {
                speed = 0.5;
            }

            if(gamepad1.x && timer.milliseconds() > 300) {
                servoPosition += 0.1;
                timer.reset();
            } else if(gamepad1.y && timer.milliseconds() > 300) {
                servoPosition -= 0.1;
                timer.reset();
            }

            if(gamepad1.right_bumper && !iIntakePressed) {
               if(timer.milliseconds() > 300) {
                    iIntakePressed = true;
                    iIntake.setPower(1);
                    timer.reset();
               }
            } else if(gamepad1.right_bumper && iIntakePressed) {
                if(timer.milliseconds() > 300) {
                    iIntakePressed = false;
                    iIntake.setPower(0);
                    timer.reset();
              }
            } else if(gamepad1.left_bumper && !oIntakePressed) {
                if(timer.milliseconds() > 300) {
                    oIntakePressed = true;
                    oIntake.setPower(1);
                    timer.reset();
                }
            } else if(gamepad1.left_bumper && oIntakePressed) {
                if (timer.milliseconds() > 300) {
                    oIntakePressed = false;
                    oIntake.setPower(0);
                    timer.reset();
                }
            }

            shooter.setPower(Math.max(0.0, Math.min(1.0, shooterPower)));
            iIntake.setPower(Math.max(0.0, Math.min(1.0, iIntakePower)));
            oIntake.setPower(Math.max(0.0, Math.min(1.0, oIntakePower)));

            telemetry.addData("Shooter Power", shooterPower);
            telemetry.addData("Shooter Velocity",shooter.getVelocity());
            telemetry.addData("Servo Position", servoPosition);
            telemetry.addData("Inner Intake Power", iIntakePower);
            telemetry.addData("Outer Intake Power", oIntakePower);
            telemetry.addData("Timer", timer.milliseconds());
            telemetry.update();

            wasDpadUp = gamepad1.dpad_up;
            wasDpadDown = gamepad1.dpad_down;

        }
    }
    private void hardwareStart() {
        frontRight = hardwareMap.get(DcMotor.class, "FR");
        frontLeft = hardwareMap.get(DcMotor.class, "FL");
        backRight = hardwareMap.get(DcMotor.class, "BR");
        backLeft = hardwareMap.get(DcMotor.class, "BL");
        shooter = hardwareMap.get(DcMotorEx.class, "SD");
        oIntake = hardwareMap.get(DcMotor.class,"OID");
        iIntake = hardwareMap.get(DcMotor.class,"IID");
        servo = hardwareMap.get(Servo.class, "servo");

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        oIntake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        iIntake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        frontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        shooter.setDirection(DcMotorSimple.Direction.REVERSE);
        oIntake.setDirection(DcMotorSimple.Direction.REVERSE);
        iIntake.setDirection(DcMotorSimple.Direction.REVERSE);

        telemetry.addData("Status","Initialized");
        telemetry.update();
    }
}