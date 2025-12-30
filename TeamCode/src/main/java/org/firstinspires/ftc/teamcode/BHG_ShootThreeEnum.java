/*
My Autonomous code:
turns on the intakes
shoots the first and second artifact
uses servo to kick up the third artifact up to the shooter


 */


package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.arcrobotics.ftclib.controller.PIDFController;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "Shoot Three Enum Test", group = "Autonomous")
@Disabled
public class BHG_ShootThreeEnum extends LinearOpMode {
    private DcMotor frontRight;
    private DcMotor frontLeft;
    private DcMotor backRight;
    private DcMotor backLeft;
    private DcMotorEx shooter;
    private DcMotor iIntake;
    private DcMotor oIntake;
    private Servo servo;
    private static final double RESTING_SERVO = 0.7;
    private static final double LAUNCHING_SERVO = 0.1;
    private final double RANGE = 40;
    private final long SERVO_DURATION = 500;
    private PIDFController shooterControl;
    public static double kP = 0.004;
    public static double kI = 0.0;
    public static double kD = 0.00001;
    public static double kF = 0.00045;
    private ElapsedTime timer = new ElapsedTime();
    private ElapsedTime servoTimer = new ElapsedTime();
    private double output;
    private boolean firstTwoShot = false;
    private boolean thirdShot = false;
    private double targetShooterVelocity = 1460;
    private enum ShooterStates {
        IDLE,
        LOAD,
        FIRST_TWO_SHOTS,
        THIRD_SHOT,
        END
    }
    private ShooterStates shooterState = ShooterStates.IDLE;




    @Override
    public void runOpMode() throws InterruptedException {

        hardwareStart();

        shooterControl = new PIDFController(kP, kI, kD, kF);
        FtcDashboard dashboard = FtcDashboard.getInstance();

        servo.setPosition(RESTING_SERVO);

        waitForStart();

        while (opModeIsActive()) {

            double shooterVelocity = shooter.getVelocity();
            output = shooterControl.calculate(shooterVelocity, targetShooterVelocity);

            TelemetryPacket packet = new TelemetryPacket();
            packet.put("Target Velocity", targetShooterVelocity);
            packet.put("Actual Velocity", shooter.getVelocity());
            packet.put("Output Power", output);
            dashboard.sendTelemetryPacket(packet);

            shootThree();

            telemetry.addData("Target Shooter Speed", targetShooterVelocity);
            telemetry.addData("Shooter Speed", shooter.getVelocity());
            telemetry.update();

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
        iIntake.setDirection(DcMotorSimple.Direction.FORWARD);

        telemetry.addData("Status","Initialized");
        telemetry.update();
    }
    private void intakeSet(double iIntakePower, double oIntakePower) {
        iIntake.setPower(iIntakePower);
        oIntake.setPower(oIntakePower);
    }
    private void servoMovement() {
        servo.setPosition(LAUNCHING_SERVO);
        servoTimer.reset();
    }
    private void setPowers(double frontLeftPower, double frontRightPower, double
            backLeftPower, double backRightPower) {

        frontLeft.setPower(frontLeftPower);
        frontRight.setPower(frontRightPower);
        backLeft.setPower(backLeftPower);
        backRight.setPower(backRightPower);
    }

    private void shootThree() {
        shooter.setPower(Math.abs(output));

        switch (shooterState) {

            case IDLE:
                timer.reset();
                shooterState = shooterState.LOAD;
                break;


            case LOAD:

                if (atTargetSpeed(shooter.getVelocity(), targetShooterVelocity, RANGE) && timer.milliseconds() > 1000) {
                intakeSet(1, 1);
                timer.reset();
                shooterState = shooterState.FIRST_TWO_SHOTS;
            }
            break;

            case FIRST_TWO_SHOTS:
                if (timer.milliseconds() > 2000) {
                servoMovement();
                shooterState = shooterState.THIRD_SHOT;
            }
            break;

            case THIRD_SHOT:
            if (servoTimer.milliseconds() > SERVO_DURATION) {
                servo.setPosition(RESTING_SERVO);
                intakeSet(0, 0);
                sleep(400);
                shooterState = shooterState.END;
            }
            break;

            case END:
                break;
        }
    }
    public boolean atTargetSpeed(double shooterVelocity, double targetVelocity, double range) {
        return Math.abs(shooterVelocity - targetVelocity) <= range;
    }
}
