package org.firstinspires.ftc.teamcode;

// basic shooter testing (incremental) + drive (mechanum) + intake (boolean)

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

            frontLeft.setPower(Math.max(0.0, Math.min(1.0, frontLeftPower)));
            frontRight.setPower(Math.max(0.0, Math.min(1.0, frontRightPower)));
            backLeft.setPower(Math.max(0.0, Math.min(1.0, backLeftPower)));
            backRight.setPower(Math.max(0.0, Math.min(1.0, backRightPower)));

            if (dpadTimer.milliseconds() > 500) {
                if (gamepad1.dpad_up && !wasDpadUp) {
                    shooterPower += 0.1;
                    dpadTimer.reset();
                } else if (gamepad1.dpad_down && !wasDpadDown) {
                    shooterPower -= 0.1;
                    dpadTimer.reset();
                }
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
            telemetry.addData("Sho0ter Velocity",shooter.getVelocity());
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