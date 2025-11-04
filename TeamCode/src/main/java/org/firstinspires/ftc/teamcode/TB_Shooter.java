package org.firstinspires.ftc.teamcode;

/*
This code performs three basic functions:  basic mecanum drive + power-based shooter control + intake off/on
The basic mecanum drive uses the left stick for forward/backward/strafe and right stick for turning.  Right trigger for turbo.
The shooter control uses dPadUp/dPadDown to incrementally raise/lower the shooter motor power by 0.1
The intake control uses the right/left bumper to turn on/off the intake.

TO DO:  For basic testing, we are using .setPower() for the shooter.  But for better control over the shooter motor
speed, we should instead use .setVelocity() with PIDF.
 */


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "Basic Shooter Testing", group = "Teleop")

public class TB_Shooter extends LinearOpMode {
    private DcMotor frontRight;
    private DcMotor frontLeft;
    private DcMotor backRight;
    private DcMotor backLeft;
    private DcMotorEx shooter;
    private DcMotor intake;
    private boolean wasDpadUp = false;
    private boolean wasDpadDown = false;
    private ElapsedTime dpadTimer = new ElapsedTime();

    @Override
    public void runOpMode() {
        hardwareStart();
        double speed = 0.5;
        double shooterPower = 0;
        double intakePower = 0;
        waitForStart();
        dpadTimer.reset();
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


                if (gamepad1.dpad_up && !wasDpadUp && dpadTimer.milliseconds() > 500) {
                    shooterPower += 0.1;
                    dpadTimer.reset();
                } else if (gamepad1.dpad_down && !wasDpadDown && dpadTimer.milliseconds() > 500) {
                    shooterPower -= 0.1;
                    dpadTimer.reset();
                }


            if (gamepad1.right_trigger > 0.2) {
                speed = 1;
            } else  {
                speed = 0.5;
            }

            if (gamepad1.right_bumper) {
                intakePower = 1;
            } else if (gamepad1.left_bumper) {
                intakePower = 0;
            }

            shooterPower = Math.max(0.0, Math.min(1.0, shooterPower));
            intakePower = Math.max(0.0, Math.min(1.0, intakePower));

            shooter.setPower(shooterPower);
            intake.setPower(intakePower);

            telemetry.addData("Shooter Power", shooterPower);
            telemetry.addData("Shooter Velocity",shooter.getVelocity());//Changed Sho0ter to Shooter. Not that big of a deal
            //but it was annoying me
            telemetry.addData("Timer", dpadTimer.milliseconds());
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
        intake = hardwareMap.get(DcMotor.class,"ID");

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        //added stuff on lines 96-101. This code is making it so that when the motor power is zero,
        //it stops immediately and doesn't drift. Mainly need this for the shooter motor and this works
        //because all the motors have brakes.

        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        frontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        shooter.setDirection(DcMotorSimple.Direction.REVERSE);
        intake.setDirection(DcMotorSimple.Direction.FORWARD);

        telemetry.addData("Status","Initialized");
        telemetry.update();
    }
}