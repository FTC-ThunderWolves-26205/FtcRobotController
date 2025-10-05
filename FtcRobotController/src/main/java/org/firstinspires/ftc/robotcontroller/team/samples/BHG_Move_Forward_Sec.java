package org.firstinspires.ftc.robotcontroller.team.samples;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name = "BHG_Move_Forward_Sec", group = "Autonomous")
public class BHG_Move_Forward_Sec extends LinearOpMode {

    private DcMotor leftDrive;
    private DcMotor rightDrive;

    @Override
    public void runOpMode() throws InterruptedException {

        leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive = hardwareMap.get(DcMotor.class, "right_drive");


        leftDrive.setDirection(DcMotor.Direction.REVERSE);
        rightDrive.setDirection(DcMotor.Direction.FORWARD);


        waitForStart();


        leftDrive.setPower(-0.4);
        rightDrive.setPower(-0.4);
        sleep(4000);


        leftDrive.setPower(0.5);
        rightDrive.setPower(1);
        sleep(1000);


        leftDrive.setPower(0);
        rightDrive.setPower(0);
    }
}