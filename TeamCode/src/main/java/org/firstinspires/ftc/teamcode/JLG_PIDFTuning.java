/*
This is code to tune our PIDF controller for our shooter.

It is important to use a tuned PIDF controller to ensure that our shooter motor reaches its
target velocity as quickly as possible after a ball is shot.

PIDF stands for Proportional, Integral, Derivative, and Feedforward.

The code below uses the FTC Dashboard so that we can "live tune" the PIDF coefficients.

By connecting to our control hub, we can view a live graph comparing actual velocity to target velocity.

We will tune the PIDF coefficients to make those lines meet as quickly as possible.

We are also using the FTC Lib extension for PIDF calculations instead of the motor encoder.

General consensus is that this  provides much better control.

This code also uses GamepadEx and associated methods, also from FTC Lib extension.

This simplifies all things related to the game controller, such as edge detection.

We will want to implement this in the TeleOp code Ben wrote up.  That way we can remove all timers.

 */

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
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "Shooter PIDF Tuner", group = "Tuning")
@Config

public class JLG_PIDFTuning extends LinearOpMode {

    // These are the PIDF coefficients which we will tune using FTC Dashboard.
    //NOTE:  FTC Dashboard will allow any variable labeled as "public static" to be "live tuned"
    public static double kP = 0.0;
    public static double kI = 0.0;
    public static double kD = 0.0;

    public static double kF = 0.0; // Start with 1 / maxRPM

//This is just a starting RPM.  We can edit it live in the dashboard.
    public static double targetRPM = 3000;

    //We are using DcMotorEx so that we can .setVelocity and .getVelocity
    private DcMotorEx shooter;

    //This is creating the PIDF Controller
    private PIDFController pidfController;

    //GamepadEx opens up a lot of methods that will come in handy for us, esp in TeleOp.
    private GamepadEx driver;
    private boolean shooterEnabled = false;

   private final double TICKS_PER_REV = 28.0; // Our GoBilda 6k RPM Motor has 28 ticks per rev per GoBilda website

    @Override
    public void runOpMode() {
        // Hardware Mapping
        shooter = hardwareMap.get(DcMotorEx.class, "SD");

        //The reason we use RUN.WITHOUT.ENCODER here is to bypass the built-in PIDF controller
        // This does NOT disable the encoder (we can still read velocity), but does turn off the PIDF controller
        shooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        shooter.setDirection(DcMotorSimple.Direction.REVERSE);

        pidfController = new PIDFController(kP, kI, kD, kF);
        driver = new GamepadEx(gamepad1);

        FtcDashboard dashboard = FtcDashboard.getInstance();

        telemetry.addLine("Ready to tune shooter PIDF. Press A to toggle shooter.");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            driver.readButtons();  //Always need this line in a loop to read the buttons under GamepadEx

            //.wasJustPressed is a simple method to perform edge detection
            if (driver.wasJustPressed(GamepadKeys.Button.A)) {
                shooterEnabled = !shooterEnabled;
                if (!shooterEnabled) {
                    shooter.setPower(0);
                }
            }

            //This sends a telemetry packet to the FTC Dashboard so we can graph values
            TelemetryPacket packet = new TelemetryPacket();

            double outputPower = 0;
            double currentRPM = ticksPerSecondToRPM(shooter.getVelocity());

            if (shooterEnabled) {
              pidfController.setPIDF(kP, kI, kD, kF);

               outputPower = pidfController.calculate(currentRPM, targetRPM);


                shooter.setPower(outputPower);

            }

            //Here is us defining the "packet" of values to send to the Dashboard
            packet.put("Status", shooterEnabled ? "ENABLED" : "DISABLED");
            packet.put("Target RPM", targetRPM);
            packet.put("Actual RPM", currentRPM);
            packet.put("Output Power", outputPower);
            dashboard.sendTelemetryPacket(packet);

            // Displayed on the Driver Station
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