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
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

@Autonomous(name = "Close Blue Auto", group = "Autonomous")

public class                  TB_CloseBlue extends LinearOpMode {
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
    private ElapsedTime shooterTimer = new ElapsedTime();
    private double output;
    private boolean firstTwoShot = false;
    private boolean firstTwoShotX = false;
    private boolean thirdShot = false;
    private boolean thirdShotX = false;
    private boolean finishedShots = false;
    private boolean finishedShotsX = false;
    private double targetShooterVelocity = 1460;
    private GoBildaPinpointDriver pinpoint;




    @Override
    public void runOpMode() throws InterruptedException {

        hardwareStart();
        boolean stepOne = false;
        boolean stepTwo = false;
        boolean stepThree = false;
        boolean stepFour = false;
        boolean stepFive = false;
        boolean stepSix = false;
        boolean intakeReverse = false;

        shooterControl = new PIDFController(kP, kI, kD, kF);
        FtcDashboard dashboard = FtcDashboard.getInstance();

        pinpoint.resetPosAndIMU();

        servo.setPosition(RESTING_SERVO);

        waitForStart();

        while (opModeIsActive()) {

            pinpoint.update();

            double shooterVelocity = shooter.getVelocity();
            output = shooterControl.calculate(shooterVelocity, targetShooterVelocity);

            TelemetryPacket packet = new TelemetryPacket();
            packet.put("Target Velocity", targetShooterVelocity);
            packet.put("Actual Velocity", shooter.getVelocity());
            packet.put("Output Power", output);
            dashboard.sendTelemetryPacket(packet);

            if(!stepOne) {
                if(pinpoint.getPosX(DistanceUnit.INCH) > -48.5) {
                    setPowers(-0.5,-0.5,-0.5,-0.5);
                } else{
                    setPowers(0,0,0,0);
                    shootThree();
                    if(finishedShots) {
                        shooter.setPower(0);
                        stepOne = true;
                    }
                }
            }

            if(stepOne && !stepTwo) {
                if(pinpoint.getHeading(AngleUnit.DEGREES) < 43) {
                    setPowers(-0.5,0.5,-0.5,0.5);
                } else {
                    setPowers(0,0,0,0);
                    stepTwo = true;
                    pinpoint.resetPosAndIMU();
                }
            }

            if(stepTwo && !stepThree) {
                intakeSet(0.75,0.75);
                if (pinpoint.getPosX(DistanceUnit.INCH) < 43.5) {
                    setPowers(0.4,0.4,0.4,0.4);
                    timer.reset();
                } else {
                    setPowers(0,0,0,0);
                    if (timer.milliseconds()>750) {
                        intakeSet(0, 0);
                        stepThree = true;
                        pinpoint.resetPosAndIMU();
                    }
                }
            }

            if(stepThree && !stepFour) {
                if(pinpoint.getPosX(DistanceUnit.INCH) > -40) {
                    setPowers(-0.5,-0.5,-0.5,-0.5);
                } else {
                    setPowers(0,0,0,0);
                    timer.reset();
                    if(!intakeReverse) {
                        intakeSet(-0.25, -0.1);

                        if (!intakeReverse && timer.milliseconds() > 100) {
                            intakeSet(0, 0);
                            intakeReverse = true;
                        }
                    }
                    stepFour = true;
                }
            }

            if(stepFour && !stepFive) {
                if(pinpoint.getHeading(AngleUnit.DEGREES) > -43) {
                    setPowers(0.5, -0.5, 0.5, -0.5);
                } else {
                    setPowers(0,0,0,0);
                    shootThreeMore();
                    if(finishedShotsX) {
                        stepFive = true;
                        pinpoint.resetPosAndIMU();
                    }
                }
            }

            if(stepFive && !stepSix) {
                if(pinpoint.getPosY(DistanceUnit.INCH) > -24) {
                    setPowers(-0.5,0.5,0.5,-0.5);
                } else {
                    setPowers(0,0,0,0);
                    requestOpModeStop();
                }
            }

            telemetry.addData("X Position", pinpoint.getPosX(DistanceUnit.INCH));
            telemetry.addData("Y Position", pinpoint.getPosY(DistanceUnit.INCH));
            telemetry.addData("Theta Position", pinpoint.getHeading(AngleUnit.DEGREES));
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
        iIntake.setDirection(DcMotorSimple.Direction.REVERSE);

        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
        pinpoint.setPosition(new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0));

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

        if(!firstTwoShot && timer.milliseconds() == 0) {
            timer.reset();
        }

        if (atTargetSpeed(shooter.getVelocity(), targetShooterVelocity ,RANGE) && !firstTwoShot && timer.milliseconds() > 1000) {
            intakeSet(1,1);
            timer.reset();
            firstTwoShot = true;
        }
        if (firstTwoShot && !thirdShot && timer.milliseconds() > 3000) {
            servoMovement();
            thirdShot = true;
        }


        if (thirdShot && servoTimer.milliseconds() > SERVO_DURATION) {
            servo.setPosition(RESTING_SERVO);
            intakeSet(0,0);
            finishedShots = true;
        }
    }

    private void shootThreeMore() {
        shooter.setPower(Math.abs(output));

        if(!firstTwoShotX && shooterTimer.milliseconds() == 0) {
            shooterTimer.reset();
        }

        if (atTargetSpeed(shooter.getVelocity(), targetShooterVelocity ,RANGE) && !firstTwoShotX && shooterTimer.milliseconds() > 1000) {
            intakeSet(1,1);
            shooterTimer.reset();
            firstTwoShotX = true;
        }
        if (firstTwoShotX && !thirdShotX && shooterTimer.milliseconds() > 3000) {
            servoMovement();
            thirdShotX = true;
        }


        if (thirdShotX && servoTimer.milliseconds() > SERVO_DURATION) {
            servo.setPosition(RESTING_SERVO);
            intakeSet(0,0);
            finishedShotsX = true;
        }
    }

    public boolean atTargetSpeed(double shooterVelocity, double targetVelocity, double range) {
        return Math.abs(shooterVelocity - targetVelocity) <= range;
    }
}
