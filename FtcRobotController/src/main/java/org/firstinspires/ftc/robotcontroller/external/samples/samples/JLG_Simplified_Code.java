package org.firstinspires.ftc.robotcontroller.external.samples.samples;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Basic Shooter Testing", group = "Teleop")
public class JLG_Simplified_Code extends LinearOpMode {

    private DcMotor frontRight, frontLeft, backRight, backLeft;
    private DcMotorEx shooter;
    private DcMotor iIntake, oIntake;
    private Servo servo;
    private GamepadEx driver;

    @Override
    public void runOpMode() {
        hardwareStart();
        driver = new GamepadEx(gamepad1);

        double speed = 0.5;
        double servoPosition = 0.5;
        double shooterPower = 0;
        double iIntakePower = 0;
        double oIntakePower = 0;

        waitForStart();

        while (opModeIsActive()) {
            driver.readButtons();

            // Drive control
            double forward = -driver.getLeftY();
            double strafe = driver.getLeftX();
            double turn = driver.getRightX() / 2;

            double frontLeftPower = (forward + strafe + turn) * speed;
            double backLeftPower = (forward - strafe + turn) * speed;
            double frontRightPower = (forward - strafe - turn) * speed;
            double backRightPower = (forward + strafe - turn) * speed;

            frontLeft.setPower(clamp(frontLeftPower));
            frontRight.setPower(clamp(frontRightPower));
            backLeft.setPower(clamp(backLeftPower));
            backRight.setPower(clamp(backRightPower));

            // Turbo mode
            speed = (driver.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER) > 0) ? 1.0 : 0.5;


            // Shooter control
            if (driver.wasJustPressed(GamepadKeys.Button.DPAD_UP)) shooterPower += 0.1;
            if (driver.wasJustPressed(GamepadKeys.Button.DPAD_DOWN)) shooterPower -= 0.1;
            if (driver.wasJustPressed(GamepadKeys.Button.DPAD_RIGHT)) shooterPower = (shooterPower == 0) ? 1 : 0;

            shooter.setPower(clamp(shooterPower));

            // Servo control
            if (driver.wasJustPressed(GamepadKeys.Button.X)) servoPosition += 0.1;
            if (driver.wasJustPressed(GamepadKeys.Button.Y)) servoPosition -= 0.1;
            if (driver.wasJustPressed(GamepadKeys.Button.A)) servoPosition = (servoPosition == 0.5) ? 0.1 : 0.5;

            servo.setPosition(clamp(servoPosition));

            // Intake toggles
            if (driver.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)) iIntakePower = (iIntakePower == 0) ? 1 : 0;
            if (driver.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)) oIntakePower = (oIntakePower == 0) ? 1 : 0;

            iIntake.setPower(iIntakePower);
            oIntake.setPower(oIntakePower);

            // Telemetry
            telemetry.addData("Shooter Power", shooterPower);
            telemetry.addData("Shooter Velocity", shooter.getVelocity());
            telemetry.addData("Servo Position", servoPosition);
            telemetry.addData("Inner Intake Power", iIntakePower);
            telemetry.addData("Outer Intake Power", oIntakePower);
            telemetry.update();
        }
    }

    private void hardwareStart() {
        frontRight = hardwareMap.get(DcMotor.class, "FR");
        frontLeft = hardwareMap.get(DcMotor.class, "FL");
        backRight = hardwareMap.get(DcMotor.class, "BR");
        backLeft = hardwareMap.get(DcMotor.class, "BL");
        shooter = hardwareMap.get(DcMotorEx.class, "SD");
        oIntake = hardwareMap.get(DcMotor.class, "OID");
        iIntake = hardwareMap.get(DcMotor.class, "IID");
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

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    private double clamp(double val) {
        return Math.max(0.0, Math.min(1.0, val));
    }
}
