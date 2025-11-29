//Basic TeleOp Mode for Ben's Bot
//Controls match main bot

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "JLG_Mecanum", group = "Teleop")

public class JLG_Mecanum extends LinearOpMode{
    public DcMotor frontLeft;
    public DcMotor frontRight;
    public DcMotor backLeft;
    public DcMotor backRight;

    private final double NORMAL_SPEED = 0.75;
    private final double SLOW_SPEED = 0.5;
    private final double TURBO_SPEED = 1.0;
    // Servo servo;
    //DistanceSensor distanceSensor;

    @Override
    public void runOpMode() {
        hardwareStart();
        double speed = NORMAL_SPEED;
        //double speed = 0.5;
        //double servoPosition = 0;
        //boolean isPosition = false;

        waitForStart();

        while (opModeIsActive()) {
            //distanceSensor = hardwareMap.get(DistanceSensor.class, "distanceSensor");

            double forward = gamepad1.left_stick_y;
            double strafe = -gamepad1.right_stick_x;
            double turn = -gamepad1.left_stick_x;

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
            if (gamepad1.a) {
                frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            }



            telemetry.addData("Front Left Encoder", frontLeft.getCurrentPosition());
            telemetry.addData("Front Right Encoder", frontRight.getCurrentPosition());
            telemetry.addData("Back Left Encoder", backLeft.getCurrentPosition());
            telemetry.addData("Back Right Encoder", backRight.getCurrentPosition());




            telemetry.update();

        }
    }

    private double clampFull(double val) {
        return Math.max(-1.0, Math.min(1.0, val));
    }
    private void hardwareStart() {
        frontLeft = hardwareMap.get(DcMotor.class, "FL");
        frontRight  = hardwareMap.get(DcMotor.class, "FR");
        backLeft = hardwareMap.get(DcMotor.class, "BL");
        backRight  = hardwareMap.get(DcMotor.class, "BR");
        //servo = hardwareMap.get(Servo.class, "servo");

        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);


        telemetry.addData("Status","Initialized");
        telemetry.update();
    }
}
