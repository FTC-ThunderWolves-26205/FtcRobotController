package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;


@Autonomous(name = "Ben's Odometry Drive", group = "Autonomous")

public class BHG_Odometry_Auto extends LinearOpMode {
    private DcMotor frontRight;
    private DcMotor frontLeft;
    private DcMotor backRight;
    private DcMotor backLeft;

    private GoBildaPinpointDriver pinpoint;




    @Override
    public void runOpMode() {
        hardwareStart();
        waitForStart();

        while(opModeIsActive()) {
            pinpoint.update();

            if(pinpoint.getPosX(DistanceUnit.INCH) > -35) {
                pinpoint.update();
                setPowers(0.5, 0.5, 0.5, 0.5);
            } else {
                pinpoint.update();
                setPowers(0, 0, 0, 0);
            }

            if(pinpoint.getHeading(AngleUnit.DEGREES) < 90 && pinpoint.getPosX(DistanceUnit.INCH) <= -35) {
                pinpoint.update();
                setPowers(-0.5, 0.5, -0.5, 0.5);
            } else {
                pinpoint.update();
                setPowers(0, 0, 0, 0);
            }

            telemetry.addData("X (in)", pinpoint.getPosX(DistanceUnit.INCH));
            telemetry.addData("Y (in)", pinpoint.getPosY(DistanceUnit.INCH));
            telemetry.addData("Heading", pinpoint.getHeading(AngleUnit.DEGREES));
            telemetry.update();

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

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
        pinpoint.setPosition(new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0));




        telemetry.addData("Status","Initialized");
    }
    private void setPowers(double frontLeftPower, double frontRightPower, double backLeftPower, double backRightPower) {
        frontLeft.setPower(frontLeftPower);
        frontRight.setPower(frontRightPower);
        backLeft.setPower(backLeftPower);
        backRight.setPower(backRightPower);
    }
}

