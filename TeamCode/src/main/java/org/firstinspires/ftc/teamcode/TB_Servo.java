package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "Basic TeleOp Mode", group = "Teleop")

public class TB_Servo extends LinearOpMode {

        private Servo servo;
        private ElapsedTime timer = new ElapsedTime();



    @Override
    public void runOpMode() {
        servo = hardwareMap.get(Servo.class, "servo");
        double servoPosition = 0.5;

        waitForStart();
        timer.reset();

        while(opModeIsActive()) {

            if (gamepad1.dpad_up && timer.milliseconds() > 500) {
                servoPosition += 0.1;
                timer.reset();
            } else if (gamepad1.dpad_down && timer.milliseconds() > 500) {
                servoPosition -= 0.1;
                timer.reset();
            }
            servo.setPosition(clampPos(servoPosition));
            telemetry.addData("Servo Position", servoPosition);
            telemetry.update();
        }

    }
    private double clampPos(double val) {
        return Math.max(0.0, Math.min(1.0, val));
    }
}