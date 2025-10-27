package org.firstinspires.ftc.teamcode.pedroPathing;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
@TeleOp(name = "Shooter Tester", group = "Teleop")

public class TB_Shooter extends LinearOpMode {
    private DcMotor shooterDrive;



    @Override
    public void runOpMode() {
        shooterDrive = hardwareMap.get(DcMotor.class, "SD");




        waitForStart();

        while(opModeIsActive()) {

            if(gamepad1.a) {
                shooterDrive.setPower(0.5);

            } else if(gamepad1.b) {
                shooterDrive.setPower(1);
            } else {
                shooterDrive.setPower(0);
            }
        }
    }
}

