/*
My Autonomous code:
XCVS
turns on the shooter
uses servo to kick up the first artifact up to the shooter
turns on the intakes
shoots the second artifact
uses servo to kick up the third artifact up to the shooter
strafes to the left
stops everything

 */


package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.arcrobotics.ftclib.controller.PIDFController;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@Autonomous(name = "PIDF Auto Test", group = "Autonomous")

public class BHG_PIDF_AUTO_TEST extends LinearOpMode {
    private DcMotor frontRight;
    private DcMotor frontLeft;
    private DcMotor backRight;
    private DcMotor backLeft;
    private DcMotorEx shooter;
    private DcMotor iIntake;
    private DcMotor oIntake;
    private Servo servo;
    private static final double RESTING_SERVO = 0.6;
    private static final double LAUNCHING_SERVO = 0.1;
    private final double TARGET_VELOCITY = 1460;
    private final double RANGE = 40;
    private final long SERVO_DURATION = 750;
    private PIDFController shooterControl;
    public static double kP = 0.004;
    public static double kI = 0.0;
    public static double kD = 0.00001;
    public static double kF = 0.00045;



    @Override
    public void runOpMode() throws InterruptedException {

        hardwareStart();
        boolean movement = false;
        boolean thirdShot = false;
        boolean firstShot = false;
        boolean secondShot = false;

        double targetShooterVelocity = 0;
        double output;

        shooterControl = new PIDFController(kP, kI, kD, kF);
        FtcDashboard dashboard = FtcDashboard.getInstance();

        servo.setPosition(RESTING_SERVO);

        waitForStart();

        while (opModeIsActive()) {

            double shooterVelocity = shooter.getVelocity();
            output = shooterControl.calculate(shooterVelocity, targetShooterVelocity);

            shooter.setPower(Math.abs(output));

            TelemetryPacket packet = new TelemetryPacket();
            packet.put("Target Velocity", targetShooterVelocity);
            packet.put("Actual Velocity", shooter.getVelocity());
            packet.put("Output Power", output);
            dashboard.sendTelemetryPacket(packet);

            if (!movement) {
                setPowers(-0.5, -0.5, -0.5, -0.5);
                sleep(2000);
                setPowers(0, 0, 0, 0);
                movement = true;
            }

            if(atTargetSpeed(shooter.getVelocity(),TARGET_VELOCITY,RANGE) && movement && !firstShot) {
                servoMovement();
                sleep(500);
                firstShot = true;
            }
            if (atTargetSpeed(shooter.getVelocity(),TARGET_VELOCITY,RANGE) && firstShot && !secondShot) {
                intakeSet(1,0.5);
                sleep(3000);
                secondShot = true;
            }
            if (atTargetSpeed(shooter.getVelocity(),TARGET_VELOCITY,RANGE) && secondShot && !thirdShot) {
                intakeSet(0,0);
                servoMovement();
                sleep(500);
                thirdShot = true;

            }
            if (thirdShot) {
                shooter.setVelocity(0);

                setPowers(0.5,-0.5,-0.5,0.5);
                sleep(2000);
                setPowers(0,0,0,0);
                break;
            }

        }
    }
    private void hardwareStart() {
        frontRight = hardwareMap.get(DcMotor.class, "FR");
        frontLeft = hardwareMap.get(DcMotor.class, "FL");
        backRight = hardwareMap.get(DcMotor.class, "BR");
        backLeft = hardwareMap.get(DcMotor.class, "BL");
        shooter = hardwareMap.get(DcMotorEx.class, "SD");
        oIntake = hardwareMap.get(DcMotor.class,"OID");
        iIntake = hardwareMap.get(DcMotor.class,"IID");
        servo = hardwareMap.get(Servo.class, "servo");

        shooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        oIntake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        iIntake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        frontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        shooter.setDirection(DcMotorSimple.Direction.REVERSE);
        oIntake.setDirection(DcMotorSimple.Direction.FORWARD);
        iIntake.setDirection(DcMotorSimple.Direction.REVERSE);

        telemetry.addData("Status","Initialized");
        telemetry.update();
    }
    private void intakeSet(double iIntakePower, double oIntakePower) {
        iIntake.setPower(iIntakePower);
        oIntake.setPower(oIntakePower);
    }
    private void servoMovement() {
        servo.setPosition(LAUNCHING_SERVO);

        sleep(SERVO_DURATION);

        servo.setPosition(RESTING_SERVO);
    }
    private void setPowers(double frontLeftPower, double frontRightPower, double
            backLeftPower, double backRightPower) {

        frontLeft.setPower(frontLeftPower);
        frontRight.setPower(frontRightPower);
        backLeft.setPower(backLeftPower);
        backRight.setPower(backRightPower);
    }

    public boolean atTargetSpeed(double shooterVelocity, double targetVelocity, double range) {
        return Math.abs(shooterVelocity - targetVelocity) <= range;
    }
}
