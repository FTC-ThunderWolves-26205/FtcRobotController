package org.firstinspires.ftc.teamcode;

/*
This code performs four basic functions:  basic mecanum drive + power-based shooter control + intakes on/off + servo increments.

CONTROLS:

    GAMEPAD 1:
        LEFT STICK Y: Moves ROBOT forward
        LEFT STICK X: Strafes ROBOT
        RIGHT STICK X: Turns ROBOT
        RIGHT BUMPER: Sets speed to 1 (double speed)
        LEFT BUMPER: Sets= speed to 0.25 (half speed)
        LEFT BUMPER & RIGHT BUMPER: Sets= speed to 0.1 (1/5 speed)
    GAMEPAD 2:
        DPAD UP: Adds 0.1 (1/10 power) to current shooter power
        DPAD DOWN: Subtracts 0.1 (1/10 power) to current shooter power
        DPAD RIGHT: Sets shooter power to 1 (full power) or 0 (no power)
        A: Sets servo position to 0.5 (resting position) or 0.1 (launching position)
        LEFT BUMPER: Sets outer intake power to 0 (no power) and 1 (full power)
        RIGHT BUMPER: Sets inner intake power to 0 (no power) and 1 (full power)

TO DO:  For basic testing, we are using .setPower() for the shooter.  But for better control over the shooter motor
speed, we should instead use .setVelocity() with PIDF.



 */


import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "Ben's Basic Shooter Testing", group = "Teleop")

public class BHG_TeleOp extends LinearOpMode {
    private DcMotor frontRight;
    private DcMotor frontLeft;
    private DcMotor backRight;
    private DcMotor backLeft;
    private DcMotorEx shooter;
    private DcMotor iIntake;
    private DcMotor oIntake;
    private Servo servo;
    private boolean iIntakePressed = false;
    private boolean oIntakePressed = false;
    private ElapsedTime shooterTimer = new ElapsedTime();
    private ElapsedTime servoTimer = new ElapsedTime();
    private ElapsedTime iIntakeTimer = new ElapsedTime();
    private ElapsedTime oIntakeTimer = new ElapsedTime();
    private static final double MAX_SERVO = 0.5;
    private static final double MIN_SERVO = 0.1;

    @Override
    public void runOpMode() {
        hardwareStart();
        double speed = 0.5;
        double servoPosition = 0.5;
        double shooterPower = 0;
        double iIntakePower = 0;
        double oIntakePower = 0;
        waitForStart();
        shooterTimer.reset();
        servoTimer.reset();
        iIntakeTimer.reset();
        oIntakeTimer.reset();
        while(opModeIsActive()) {

            double forward = -gamepad1.left_stick_y;
            double strafe = gamepad1.left_stick_x;
            double turn = gamepad1.right_stick_x/2;

            double frontLeftPower = (forward + strafe + turn) * speed;
            double backLeftPower = (forward - strafe + turn) * speed;
            double frontRightPower = (forward - strafe - turn) * speed;
            double backRightPower = (forward + strafe - turn) * speed;

            frontLeft.setPower(clampFull(frontLeftPower));
            frontRight.setPower(clampFull(frontRightPower));
            backLeft.setPower(clampFull(backLeftPower));
            backRight.setPower(clampFull(backRightPower));
            servo.setPosition(clampServo(servoPosition));

            if (gamepad1.right_bumper) {
                speed = 1;
            } else if(gamepad1.left_bumper) {
                speed = 0.25;
            } else if(gamepad1.left_bumper && gamepad1.right_bumper) {
                speed = 0.1;
            } else {
                speed = 0.5;
            }

            if (gamepad2.dpad_up && shooterTimer.milliseconds() > 500) {
                shooterPower += 0.1;
                shooterTimer.reset();
            } else if (gamepad2.dpad_down && shooterTimer.milliseconds() > 500) {
                shooterPower -= 0.1;
                shooterTimer.reset();
            } else if(gamepad2.dpad_right && shooterTimer.milliseconds() > 500) {
                shooterPower = (shooterPower == 0) ? 1 : 0;
                shooterTimer.reset();
            }

            if(gamepad2.a && servoTimer.milliseconds() > 500) {
                servoPosition = (servoPosition == MAX_SERVO) ? MIN_SERVO : MAX_SERVO;
                servoTimer.reset();
            }

            if(gamepad2.right_bumper && iIntakeTimer.milliseconds() > 500) {
                iIntakePower = (iIntakePower == 0) ? 1 : 0;
                iIntakeTimer.reset();
            } else if(gamepad2.left_bumper && oIntakeTimer.milliseconds() > 500) {
                oIntakePower = (oIntakePower == 0) ? 1 : 0;
                oIntakeTimer.reset();
            }

            shooter.setPower(clampFull(shooterPower));
            iIntake.setPower(clampPos(iIntakePower));
            oIntake.setPower(clampPos(oIntakePower));

            telemetry.addData("Shooter Power", shooterPower);
            telemetry.addData("Shooter Velocity",shooter.getVelocity());
            telemetry.addData("Servo Position", servoPosition);
            telemetry.addData("Inner Intake Power", iIntakePower);
            telemetry.addData("Outer Intake Power", oIntakePower);
            telemetry.addData("Shooter Timer", shooterTimer.milliseconds());
            telemetry.addData("Servo Timer", servoTimer.milliseconds());
            telemetry.addData("Inner Intake Timer", iIntakeTimer.milliseconds());
            telemetry.addData("Outer Intake Timer", oIntakeTimer.milliseconds());
            telemetry.addData("Battery Voltage", hardwareMap.voltageSensor.iterator().next().getVoltage());
            telemetry.update();

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

    private double clampPos(double val) {
        return Math.max(0.0, Math.min(1.0, val));
    }
    private double clampFull(double val) {
        return Math.max(-1.0, Math.min(1.0, val));
    }
    private double clampServo(double val) {
        return Math.max(MIN_SERVO, Math.min(MAX_SERVO, val));
    }
}