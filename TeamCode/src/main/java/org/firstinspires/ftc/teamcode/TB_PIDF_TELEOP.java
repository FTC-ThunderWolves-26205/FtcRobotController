package org.firstinspires.ftc.teamcode;

/*
This code performs four basic functions:  basic mecanum drive + power-based shooter control + intakes on/off + servo increments.
 FBGDFtwtwrt3
CONTROLS:

    GAMEPAD 1:
        LEFT STICK Y: Moves ROBOT forward
        LEFT STICK X: Strafes ROBOT
        RIGHT STICK X: Turns ROBOT
        RIGHT BUMPER: Sets speed to 1 (double speed)
        LEFT BUMPER: Sets speed to 0.5 (half speed)
    GAMEPAD 2:
        DPAD UP: Adds 0.1 (1/10 power) to current shooter power
        DPAD DOWN: Subtracts 0.1 (1/10 power) to current shooter power
        DPAD RIGHT: Sets shooter power to 1 (full power) or 0 (no power)
        A: Sets servo position to launching position then back to resting position
        X: Sets shooter power to 0.85
        Y: Sets shooter power to 0.7
        LEFT BUMPER: Sets outer intake power to 0 (no power) and 1 (full power)
        RIGHT BUMPER: Sets inner intake power to 0 (no power) and 1 (full power)
        LEFT TRIGGER: Sets outer intake power to 0 (no power) and -1 (reverse)
        RIGHT TRIGGER: Sets inner intake power to 0 (no power) and -1 (reverse)

TO DO:  1.  Clean up our edge detection to use FTCLib .wasJustPressed method.  Remove all timers.
                - driver.wasJustPressed(GamepadKeys.Button.A) is an example


LONGER TO DO (Things to Try Before 2nd Tournament?):
        1.  Add webcam, vision portal, apriltag processor
        2.  Automate shooting velocity based on detected distance to AprilTag.

*/


import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.arcrobotics.ftclib.controller.PIDFController;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "PIDF TeleOp Mode", group = "Teleop")
@Config

public class TB_PIDF_TELEOP extends LinearOpMode {
    private DcMotor frontRight;
    private DcMotor frontLeft;
    private DcMotor backRight;
    private DcMotor backLeft;
    private DcMotorEx shooter;
    private DcMotor iIntake;
    private DcMotor oIntake;
    private Servo servo;
    private ElapsedTime shooterTimer = new ElapsedTime();
    private ElapsedTime servoTimer = new ElapsedTime();
    private ElapsedTime iIntakeTimer = new ElapsedTime();
    private ElapsedTime oIntakeTimer = new ElapsedTime();
    private static final double RESTING_SERVO = 0.6;
    private static final double LAUNCHING_SERVO = 0.1;
    private final double NORMAL_SPEED = 0.75;
    private final double SLOW_SPEED = 0.5;
    private final double TURBO_SPEED = 1.0;
    private final double SERVO_DURATION = 750;
    private final double TICKS_PER_REV = 28.0; // GoBilda 6k Motor has 28 Ticks per Rev per GoBilda website
    private PIDFController shooterControl;
    private static double kP = 0;
    private static double kI = 0.0;
    private static double kD = 0;
    private static double kF = 0;

    @Override
    public void runOpMode() {
        hardwareStart();
        double speed = NORMAL_SPEED;
        servo.setPosition(RESTING_SERVO);
        double targetShooterVelocity = 0;
        double iIntakePower = 0;
        double oIntakePower = 0;
        boolean isServo = false;

        shooterControl = new PIDFController(kP, kI, kD, kF);
        FtcDashboard dashboard = FtcDashboard.getInstance();
        waitForStart();
        shooterTimer.reset();
        servoTimer.reset();
        iIntakeTimer.reset();
        oIntakeTimer.reset();
        while(opModeIsActive()) {

            double forward = -gamepad1.left_stick_y;
            double strafe = gamepad1.left_stick_x;
            double turn = gamepad1.right_stick_x;

            double frontLeftPower = (forward + strafe + turn) * speed;
            double backLeftPower = (forward - strafe + turn) * speed;
            double frontRightPower = (forward - strafe - turn) * speed;
            double backRightPower = (forward + strafe - turn) * speed;

            frontLeft.setPower(clampFull(frontLeftPower));
            frontRight.setPower(clampFull(frontRightPower));
            backLeft.setPower(clampFull(backLeftPower));
            backRight.setPower(clampFull(backRightPower));

            if (gamepad1.right_bumper) {
                speed = TURBO_SPEED;
            } else if(gamepad1.left_bumper) {
                speed = SLOW_SPEED;
            } else {
                speed = NORMAL_SPEED;
            }

            if (gamepad2.dpad_up && shooterTimer.milliseconds() > 500) {
                targetShooterVelocity += 20;
                shooterTimer.reset();
            } else if (gamepad2.dpad_down && shooterTimer.milliseconds() > 500) {
                targetShooterVelocity -= 20;
                shooterTimer.reset();
            } else if(gamepad2.dpad_right && shooterTimer.milliseconds() > 500) {
                targetShooterVelocity = (targetShooterVelocity == 0) ? 2200 : 0;
                shooterTimer.reset();
            }

            if(gamepad2.x) {
                targetShooterVelocity = 1800;
            }

            if(gamepad2.b) {
                targetShooterVelocity = 1520;
            }

            if(gamepad2.a && servoTimer.milliseconds() > SERVO_DURATION && !isServo) {
                servo.setPosition(LAUNCHING_SERVO);
                servoTimer.reset();
                isServo = true;
            }

            if(servoTimer.milliseconds() > SERVO_DURATION && isServo) {
                servo.setPosition(RESTING_SERVO);
                servoTimer.reset();
                isServo = false;
            }

            if(gamepad2.right_bumper && iIntakeTimer.milliseconds() > 500) {
                iIntakePower = (iIntakePower == 0) ? 1 : 0;
                iIntakeTimer.reset();
            } else if(gamepad2.left_bumper && oIntakeTimer.milliseconds() > 500) {
                oIntakePower = (oIntakePower == 0) ? 1 : 0;
                oIntakeTimer.reset();
            }

            if(gamepad2.right_trigger > 0 && iIntakeTimer.milliseconds() > 500) {
                iIntakePower = (iIntakePower == 0) ? -1 : 0;
                iIntakeTimer.reset();
            } else if (gamepad2.left_trigger > 0 && oIntakeTimer.milliseconds() > 500) {
                oIntakePower = (oIntakePower == 0) ? -1 : 0;
                oIntakeTimer.reset();
            }



            double shooterVelocity = shooter.getVelocity();
            iIntake.setPower(clampFull(iIntakePower));
            oIntake.setPower(clampFull(oIntakePower));
            double output = shooterControl.calculate(shooterVelocity, targetShooterVelocity);
            shooter.setPower(output);

            TelemetryPacket packet = new TelemetryPacket();
            packet.put("Target Velocity", targetShooterVelocity);
            packet.put("Actual Actual Velocity", shooter.getVelocity());
            packet.put("Output Power", output);
            dashboard.sendTelemetryPacket(packet);


            telemetry.addData("Shooter Power", targetShooterVelocity);
            telemetry.addData("Shooter Velocity", shooterVelocity);
            telemetry.addData("Servo Position", servo.getPosition());
            telemetry.addData("Inner Intake Power", iIntakePower);
            telemetry.addData("Outer Intake Power", oIntakePower);
            telemetry.addData("Shooter RPM", ticksPerSecondToRPM(shooter.getVelocity()));
            telemetry.addData("Battery Voltage", hardwareMap.voltageSensor.iterator().next().getVoltage());
            telemetry.addData("Servo Is Pressed", isServo);
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
        oIntake.setDirection(DcMotorSimple.Direction.FORWARD);
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
        return Math.max(LAUNCHING_SERVO, Math.min(RESTING_SERVO, val));
    }
    private double clampShoot(double val) { return Math.max(0.0, Math.min(2200, val));}
    private double ticksPerSecondToRPM(double tps) { return tps * 60.0 / TICKS_PER_REV; }

}