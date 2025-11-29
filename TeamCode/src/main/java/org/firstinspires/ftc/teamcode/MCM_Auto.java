/*
TELL US WHAT YOUR AUTO MODE DOES HERE

 */


package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

@Autonomous(name = "Maddie's Auto", group = "Autonomous")

public class MCM_Auto extends LinearOpMode {
    private DcMotor frontRight;
    private DcMotor frontLeft;
    private DcMotor backRight;
    private DcMotor backLeft;
    private DcMotorEx shooter;
    private DcMotor iIntake;
    private DcMotor oIntake;
    private Servo servo;
    private GoBildaPinpointDriver pinpoint;


    private static final double RESTING_SERVO = 0.6;
    private static final double LAUNCHING_SERVO = 0.1;
    private final double SERVO_DURATION = 500;


    @Override
    public void runOpMode() throws InterruptedException {
        pinpoint.resetPosAndIMU();

        hardwareStart();
        double servoPosition = 0.6;

        while (opModeIsActive()) {
            pinpoint.resetPosAndIMU();

            pinpoint.update();

            telemetry.addData("X", pinpoint.getPosX(DistanceUnit.INCH));
            telemetry.addData("Y", pinpoint.getPosY(DistanceUnit.INCH));
            telemetry.addData("Heading", pinpoint.getPosition().getHeading(AngleUnit.DEGREES));
            telemetry.update();


            shooter.setPower(0.5);

            setPowers(-0.5,-0.5,-0.5,-0.5);

            wait(500);

            setPowers(0,0,0,0);

            pinpoint.update();

            telemetry.addData("X", pinpoint.getPosX(DistanceUnit.INCH));
            telemetry.addData("Y", pinpoint.getPosY(DistanceUnit.INCH));
            telemetry.addData("Heading", pinpoint.getPosition().getHeading(AngleUnit.DEGREES));
            telemetry.update();

            wait(500);

            oIntake.setPower(0.2);
            iIntake.setPower(0.2);

            wait(500);

            oIntake.setPower(0);
            iIntake.setPower(0);

            wait(500);

            oIntake.setPower(0.2);
            iIntake.setPower(0.2);

            wait(700);

            servoUp();

            wait(300);

            servoDown();

            wait(500);
            iIntake.setPower(0);
            oIntake.setPower(0);
            shooter.setPower(0);

            setPowers(-0.5,0.5,0.5,-0.5);

            wait(300);

            setPowers(0,0,0,0); // move out of zone

            pinpoint.update();

            telemetry.addData("X", pinpoint.getPosX(DistanceUnit.INCH));
            telemetry.addData("Y", pinpoint.getPosY(DistanceUnit.INCH));
            telemetry.addData("Heading", pinpoint.getPosition().getHeading(AngleUnit.DEGREES));
            telemetry.update();

            waitForStart();
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
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        pinpoint.setPosition(new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0));

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
        oIntake.setDirection(DcMotorSimple.Direction.REVERSE);
        iIntake.setDirection(DcMotorSimple.Direction.REVERSE);

        telemetry.addData("Status","Initialized");
        telemetry.update();
    }
    private void setPowers (double fl, double fr, double bl, double br) {
        frontLeft.setPower(fl);
        frontRight.setPower(fr);
        backLeft.setPower(bl);
        backRight.setPower(br);
    }
    private void servoUp () {
        servo.setPosition(0.6);
    }
    private void servoDown() {
        servo.setPosition(0.1);
    }
}
