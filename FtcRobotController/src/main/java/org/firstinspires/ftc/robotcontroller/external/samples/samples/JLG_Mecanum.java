package org.firstinspires.ftc.robotcontroller.external.samples.samples;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "JLG_Mecanum", group = "Teleop")
@Disabled
public class JLG_Mecanum extends LinearOpMode{
    public DcMotor frontLeft;
    public DcMotor frontRight;
    public DcMotor backLeft;
    public DcMotor backRight;
    // Servo servo;
    //DistanceSensor distanceSensor;

    @Override
    public void runOpMode() {
        hardwareStart();
        //double speed = 0.5;
        //double servoPosition = 0;
        //boolean isPosition = false;

        waitForStart();

        while (opModeIsActive()) {
            //distanceSensor = hardwareMap.get(DistanceSensor.class, "distanceSensor");

            double forward = gamepad1.left_stick_y;
            double turn = -gamepad1.right_stick_x;
            double strafe = -gamepad1.left_stick_x;

            double frontLeftPower = (forward + turn + strafe) /2;
            double frontRightPower = (forward - turn - strafe) /2;
            double backLeftPower = (forward - turn + strafe) /2;
            double backRightPower = (forward + turn - strafe) /2;

            frontLeft.setPower(frontLeftPower);
            frontRight.setPower(frontRightPower);
            backLeft.setPower(backLeftPower);
            backRight.setPower(backRightPower);

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
