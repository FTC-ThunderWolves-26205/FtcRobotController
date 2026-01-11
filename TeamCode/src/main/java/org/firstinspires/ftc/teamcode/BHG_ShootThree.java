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

@Autonomous(name = "Shoot Three - BEN", group = "Autonomous")

public class BHG_ShootThree extends LinearOpMode {

    private DcMotor frontRight;
    private DcMotor frontLeft;
    private DcMotor backRight;
    private DcMotor backLeft;
    private DcMotorEx shooter;
    private DcMotor iIntake;
    private DcMotor oIntake;
    private Servo servo;
    private static final double RESTING_SERVO = TB_Constants.RESTING_SERVO;
    private static final double LAUNCHING_SERVO = TB_Constants.LAUNCHING_SERVO;
    private final double RANGE = 40;
    private final long SERVO_DURATION = 500;
    private PIDFController shooterControl;
    public static double kP = TB_Constants.kP;
    public static double kI = TB_Constants.kI;
    public static double kD = TB_Constants.kD;
    public static double kF = TB_Constants.kF;
    private ElapsedTime timer = new ElapsedTime();
    private ElapsedTime servoTimer = new ElapsedTime();
    private ElapsedTime startTimer = new ElapsedTime();
    private double output;
    private boolean firstTwoShot = false;
    private boolean thirdShot = false;
    private double TARGET_SHOOTER_VELOCITY = 1460;
    private double targetShooterVelocity = 1460;
    private enum ShooterState {
        IDLE,
        SHOOT_TWO,
        SHOOT_THIRD,
        STOP_INTAKES,
        END
    }
    private ShooterState shooterState = ShooterState.IDLE;


    @Override
    public void runOpMode () throws InterruptedException {

        hardwareStart();
        boolean firstThree = false;
        shooterState = ShooterState.IDLE;

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
    private void hardwareStart () {
        frontRight = hardwareMap.get(DcMotor.class, "FR");
        frontLeft = hardwareMap.get(DcMotor.class, "FL");
        backRight = hardwareMap.get(DcMotor.class, "BR");
        backLeft = hardwareMap.get(DcMotor.class, "BL");
        shooter = hardwareMap.get(DcMotorEx.class, "SD");
        oIntake = hardwareMap.get(DcMotor.class, "OID");
        iIntake = hardwareMap.get(DcMotor.class, "IID");
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

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }
    private void intakeSet ( double iIntakePower, double oIntakePower){
        iIntake.setPower(iIntakePower);
        oIntake.setPower(oIntakePower);
    }
    private void servoMovement () {
        servo.setPosition(LAUNCHING_SERVO);
        servoTimer.reset();
    }
    private void setPowers ( double frontLeftPower, double frontRightPower, double
            backLeftPower,double backRightPower){

        frontLeft.setPower(frontLeftPower);
        frontRight.setPower(frontRightPower);
        backLeft.setPower(backLeftPower);
        backRight.setPower(backRightPower);
    }

    private void shootThree() {
        double shooterVelocity = shooter.getVelocity();
        output = shooterControl.calculate(shooterVelocity, targetShooterVelocity);
        shooter.setPower(output);


        switch (shooterState) {

            case IDLE:
                timer.reset();
                servoTimer.reset();
                targetShooterVelocity = TARGET_SHOOTER_VELOCITY;
                shooterState = ShooterState.SHOOT_TWO;
                break;


            case SHOOT_TWO:

                if (atTargetSpeed(shooter.getVelocity(), targetShooterVelocity, RANGE) && timer.milliseconds() > 1000) {
                    intakeSet(1, 0.8);
                    timer.reset();
                    shooterState = ShooterState.SHOOT_THIRD;
                }
                break;

            case SHOOT_THIRD:
                if (timer.milliseconds() > 3000) {
                    servoMovement();
                    shooterState = ShooterState.STOP_INTAKES;
                }
                break;

            case STOP_INTAKES:
                if (servoTimer.milliseconds() > SERVO_DURATION) {
                    servo.setPosition(RESTING_SERVO);
                    intakeSet(0, 0);
                    targetShooterVelocity = 0;
                    shooterState = ShooterState.END;
                }
                break;

            case END:

                break;
        }
    }

    public boolean atTargetSpeed ( double shooterVelocity, double targetVelocity, double range){
        return Math.abs(shooterVelocity - targetVelocity) <= range;
    }
}

