package org.firstinspires.ftc.robotcontroller.team.samples;


import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "Basic Servo Mode", group = "Teleop")
@Disabled
public class TB_Servo extends LinearOpMode {

        private Servo servo;
        private ElapsedTime timer = new ElapsedTime();



    @Override
    public void runOpMode() {
        servo = hardwareMap.get(Servo.class, "servo");
        double servoPosition = 0.6;

        waitForStart();
        timer.reset();

        while(opModeIsActive()) {

            if (gamepad1.dpad_up && timer.milliseconds() > 500) {
                servoPosition += 0.1;
                timer.reset();
            } else if (gamepad1.dpad_down && timer.milliseconds() > 500) {
                servoPosition -= 0.1;
                timer.reset();
            } else if(gamepad1.dpad_right && timer.milliseconds() > 500) {
                servoPosition = (servoPosition == 0.6) ? 0.1 : 0.6;
                timer.reset();
            }
            servo.setPosition(servoPosition);


            telemetry.addData("Servo Position", servoPosition);
            telemetry.update();
        }

    }
    private double clampPos(double val) {
        return Math.max(0.0, Math.min(1.0, val));
    }
}