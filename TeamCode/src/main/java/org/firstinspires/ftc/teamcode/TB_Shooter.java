package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "Shooter Tester", group = "Teleop")

public class TB_Shooter extends LinearOpMode {
    private DcMotor shooterDrive;



    @Override
    public void runOpMode() {
        shooterDrive = hardwareMap.get(DcMotor.class, "SD");
        shooterDrive.setDirection(DcMotor.Direction.REVERSE);

        double shooterSpeed = 0;


        waitForStart();

        while(opModeIsActive()) {
//The problem with this code is that it is rapidly taking the speed to 1 due to the while loop being
            //run many many times per second.  Need to add wasPressed
            if(gamepad1.a) {
               if (shooterSpeed < 1) {
                   shooterSpeed += 0.1;
               }
            } else if(gamepad1.b) {
                if (shooterSpeed > 0) {
                    shooterSpeed -= 0.1;
                }
            } else if (gamepad1.x) {
             shooterSpeed = 0;
            }

            shooterDrive.setPower(shooterSpeed);
            telemetry.addData("Shooter speed",shooterSpeed);
            telemetry.update();

        }
    }
}

