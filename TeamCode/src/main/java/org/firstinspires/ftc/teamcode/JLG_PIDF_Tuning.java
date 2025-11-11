package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;

import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import com.arcrobotics.ftclib.controller.PIDFController;
import com.arcrobotics.ftclib.gamepad.GamepadEx;


@TeleOp(name = "Shooter PIDF Tuner", group = "Tuning")
@Config
@Disabled
public class JLG_PIDF_Tuning extends LinearOpMode {

    public static double targetRPM = 3000;

    // FTCLib PIDF coefficients
    public static double kP = 0.0;
    public static double kI = 0.0;
    public static double kD = 0.0;
    public static double kF = 0.0;

    private DcMotorEx shooter;
    private PIDFController pidfController;
    private GamepadEx driver;
    private boolean shooterEnabled = false;

    @Override
    public void runOpMode() {
        shooter = hardwareMap.get(DcMotorEx.class, "SD");
        shooter.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        pidfController = new PIDFController(kP, kI, kD, kF);
        driver = new GamepadEx(gamepad1);

        telemetry.addLine("Ready to tune shooter PIDF. Press A to toggle.");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            driver.readButtons();

            // Toggle shooter with A button
            if (driver.wasJustPressed(GamepadKeys.Button.A)) {
                shooterEnabled = !shooterEnabled;
            }

            // Update PIDF coefficients from @Config
            pidfController.setPIDF(kP, kI, kD, kF);
            pidfController.setSetPoint(targetRPM);

            double currentTicksPerSec = shooter.getVelocity();
            double currentRPM = ticksPerSecondToRPM(currentTicksPerSec);

            double output = pidfController.calculate(currentRPM);

            if (shooterEnabled) {
                shooter.setPower(output);
            } else {
                shooter.setPower(0);
            }

            // Dashboard telemetry
            TelemetryPacket packet = new TelemetryPacket();
            packet.put("TargetRPM", targetRPM);
            packet.put("ActualRPM", currentRPM);
            packet.put("OutputPower", output);
            packet.put("kP", kP);
            packet.put("kI", kI);
            packet.put("kD", kD);
            packet.put("kF", kF);
            FtcDashboard.getInstance().sendTelemetryPacket(packet);

            // Driver station telemetry
            telemetry.addData("Shooter Enabled", shooterEnabled);
            telemetry.addData("Target RPM", targetRPM);
            telemetry.addData("Actual RPM", currentRPM);
            telemetry.addData("Output Power", output);
            telemetry.update();
        }
    }

    private double ticksPerSecondToRPM(double tps) {
        return tps * 60.0 / 28.0; // GoBILDA 6K motor: 28 ticks/rev
    }
}