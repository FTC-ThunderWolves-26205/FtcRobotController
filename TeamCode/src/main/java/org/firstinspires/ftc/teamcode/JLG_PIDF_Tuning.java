package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.arcrobotics.ftclib.controller.PIDFController;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Shooter PIDF Tuner", group = "Tuning")
@Config

public class JLG_PIDF_Tuning extends LinearOpMode {

    // PIDF Coefficients: Tune these in the FTC Dashboard
    public static double kP = 0.0;
    public static double kI = 0.0;
    public static double kD = 0.0;

    public static double kF = 0.0; // Start with 1 / maxRPM


    public static double targetRPM = 3000;

    private DcMotorEx shooter;
    private PIDFController pidfController;
    private GamepadEx driver;
    private boolean shooterEnabled = false;

   private final double TICKS_PER_REV = 28.0; // For a GoBILDA 5203 series motor (e.g., 6000 RPM)

    @Override
    public void runOpMode() {
        // Hardware Mapping
        shooter = hardwareMap.get(DcMotorEx.class, "SD");
        shooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        pidfController = new PIDFController(kP, kI, kD, kF);
        driver = new GamepadEx(gamepad1);

        FtcDashboard dashboard = FtcDashboard.getInstance();

        telemetry.addLine("Ready to tune shooter PIDF. Press A to toggle shooter.");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            driver.readButtons();


            if (driver.wasJustPressed(GamepadKeys.Button.A)) {
                shooterEnabled = !shooterEnabled;
                if (!shooterEnabled) {
                    shooter.setPower(0);
                }
            }

            TelemetryPacket packet = new TelemetryPacket();

            double outputPower = 0;
            double currentRPM = ticksPerSecondToRPM(shooter.getVelocity());

            if (shooterEnabled) {
              pidfController.setPIDF(kP, kI, kD, kF);

               outputPower = pidfController.calculate(currentRPM, targetRPM);


                shooter.setPower(outputPower);

            } else {
                shooter.setPower(0);
            }


            packet.put("Status", shooterEnabled ? "ENABLED" : "DISABLED");
            packet.put("Target RPM", targetRPM);
            packet.put("Actual RPM", currentRPM);
            packet.put("Output Power", outputPower);
            dashboard.sendTelemetryPacket(packet);

            // Optional: Send data to Driver Station
            telemetry.addLine("Connect to 192.168.43.1:8080/dash for dashboard");
            telemetry.addLine ("Try kF = 0.00025, then kP = 0.002, then Kd = 0.0002.");
            telemetry.addData("Shooter Enabled", shooterEnabled);
            telemetry.addData("Target RPM", targetRPM);
            telemetry.addData("Actual RPM", currentRPM);
            telemetry.addData("Output Power", outputPower);
            telemetry.update();
        }
    }

    private double ticksPerSecondToRPM(double tps) {
        return tps * 60.0 / TICKS_PER_REV;
    }
}