package org.firstinspires.ftc.robotcontroller.external.samples.samples;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
@TeleOp(name = "Ben Shooter", group = "Teleop")
@Disabled
public class BHG_Shooter extends LinearOpMode {
    private DcMotor leftDrive;
    private DcMotor rightDrive;


    @Override
    public void runOpMode() {
        leftDrive = hardwareMap.get(DcMotor.class, "leftDrive");
        rightDrive  = hardwareMap.get(DcMotor.class, "rightDrive");

        leftDrive.setDirection(DcMotor.Direction.FORWARD);
        rightDrive.setDirection(DcMotor.Direction.REVERSE);

        waitForStart();

        while(opModeIsActive()) {

            if(gamepad1.a) {
                leftDrive.setPower(0.5);
                rightDrive.setPower(0.5);

            } else if(gamepad1.b) {
                leftDrive.setPower(1);
                rightDrive.setPower(1);
            } else {
                leftDrive.setPower(0);
                rightDrive.setPower(0);
            }
        }
    }
}

