/*
Learning FTC Dashboard functionality

TO GRAPH:
-create FtcDashboard variable:  private FtcDashboard dashboard;
-On INIT, before start, set dashboard = FtcDashboard.getInstance()
-In the main loop, declare a new TelemetryPacket:  TelemetryPacket packet = new TelemetryPacket();
-Add whatever variables you want to graph to the packet (as many as you wish):  packet.put("Front Left Power", frontLeftPower);
-send the telemetry packet:  dashboard.sendTelemetryPacket(packet);

TO CHANGE VARIABLES WHILE BOT IS RUNNING FOR TUNING PURPOSES (PIDF COEFFICIENTS):
-Need to add @Config at @Override spot
-To mark a field as a config variable, declare it static and not final and annotate the enclosing class with @Config
-public static PIDCoefficients ShooterPIDF = new PIDCoefficients();
 */



package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.arcrobotics.ftclib.gamepad.GamepadEx;


@TeleOp(name = "DashBoard Testing", group = "Teleop")
@Disabled
public class JLG_DashBoard_Testing extends LinearOpMode {
    private DcMotor frontRight;
    private DcMotor frontLeft;
    private DcMotor backRight;
    private DcMotor backLeft;

    private FtcDashboard dashboard;

    @Override
    public void runOpMode() {
        hardwareStart();
        dashboard = FtcDashboard.getInstance();

        double speed = 0.5;
        waitForStart();

        while (opModeIsActive()) {
            double forward = -gamepad1.left_stick_y;
            double strafe = gamepad1.left_stick_x;
            double turn = gamepad1.right_stick_x / 2;

            double frontLeftPower = (forward + strafe + turn) * speed;
            double backLeftPower = (forward - strafe + turn) * speed;
            double frontRightPower = (forward - strafe - turn) * speed;
            double backRightPower = (forward + strafe - turn) * speed;

            frontLeft.setPower(frontLeftPower);
            frontRight.setPower(frontRightPower);
            backLeft.setPower(backLeftPower);
            backRight.setPower(backRightPower);

            // Send power values to Dashboard graph
            TelemetryPacket packet = new TelemetryPacket();
            packet.put("Front Left Power", frontLeftPower);
            packet.put("Front Right Power", frontRightPower);
            packet.put("Back Left Power", backLeftPower);
            packet.put("Back Right Power", backRightPower);
            dashboard.sendTelemetryPacket(packet);

            if (gamepad1.dpad_left) {
                strafe = -0.5;
                sleep(5);
                strafe = -gamepad1.left_stick_x;
            } else if (gamepad1.dpad_right) {
                strafe = 0.5;
                sleep(5);
                strafe = -gamepad1.left_stick_x;
            }

            if (gamepad1.left_bumper) {
                speed = 1.0;
            } else if (gamepad1.right_bumper) {
                speed = 0.25;
            } else {
                speed = 0.5;
            }
        }
    }

    private void hardwareStart() {
        frontRight = hardwareMap.get(DcMotor.class, "FR");
        frontLeft = hardwareMap.get(DcMotor.class, "FL");
        backRight = hardwareMap.get(DcMotor.class, "BR");
        backLeft = hardwareMap.get(DcMotor.class, "BL");

        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        frontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeft.setDirection(DcMotorSimple.Direction.FORWARD);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }
}