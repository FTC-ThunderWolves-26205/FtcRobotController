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

import dalvik.system.DelegateLastClassLoader;

@Autonomous(name = "Auto Far Move Left", group = "Autonomous")

public class BHG_Auto_Close_Left extends LinearOpMode {
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
    private final double SHOOTER_SPEED = 1800;
    private final long SERVO_DURATION = 750;
    private GoBildaPinpointDriver pinpoint;
    private final double DISTANCE_ONE = 12;
    private final double DISTANCE_TWO = 5;


    @Override
    public void runOpMode() throws InterruptedException {

        hardwareStart();
        double targetVelocity = 1500;
        boolean moveOne = false;


        servo.setPosition(RESTING_SERVO);

        waitForStart();

        while (opModeIsActive()) {
            double shooterVelocity = shooter.getVelocity();

            pinpoint.update();

            if(pinpoint.getPosX(DistanceUnit.INCH) < DISTANCE_ONE && !moveOne) {
                pinpoint.update();
                setPowers(-0.5, -0.5, -0.5, -0.5);
            } else {
                pinpoint.update();
                setPowers(0, 0, 0, 0);
                moveOne = true;
            }

            shooter.setVelocity(targetVelocity);

            if(shooterVelocity > targetVelocity - 40 && shooterVelocity < targetVelocity + 40) {
                servoMovement();
            } else {
                servo.setPosition(0);
            }

            if(shooterVelocity > targetVelocity - 40 && shooterVelocity < targetVelocity + 40) {
                intakeSet(1, 0.5);
                sleep(5000);
                intakeSet(0, 0);
            } else {
                intakeSet(0, 0);
            }

            if(shooterVelocity > targetVelocity - 40 && shooterVelocity < targetVelocity + 40) {
                servoMovement();
            } else {
                servo.setPosition(RESTING_SERVO);
            }

            if(pinpoint.getPosY(DistanceUnit.INCH) < DISTANCE_TWO && pinpoint.getPosX(DistanceUnit.INCH) >= DISTANCE_ONE) {
                setPowers(0.5, -0.5, -0.5, 0.5);
            } else {
                setPowers(0, 0, 0, 0);
            }


            telemetry.addData("Shooter Velocity", shooterVelocity);
            telemetry.addData("Target Velocity", targetVelocity);
            telemetry.addData("X (in)", pinpoint.getPosX(DistanceUnit.INCH));
            telemetry.addData("Y (in)", pinpoint.getPosY(DistanceUnit.INCH));
            telemetry.addData("Heading", pinpoint.getHeading(AngleUnit.DEGREES));
            telemetry.update();

            //BELLA WUS HERE

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
}
