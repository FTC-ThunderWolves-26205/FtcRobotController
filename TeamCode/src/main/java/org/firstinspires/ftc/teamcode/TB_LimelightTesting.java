//This code was derived from the youtube video here:  https://www.youtube.com/watch?v=Ap1lBywv00M
//We will follow the steps in that video to tune our distance sensing with our Limelight
//We will them move on to learning Pose3D of the april tags
//We will use these concepts to (1) auto align for shots and (2) automate shooter speed at any distance



package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.arcrobotics.ftclib.controller.PIDFController;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

@TeleOp(name = "Limelight Testing", group = "Teleop")
@Disabled
public class TB_LimelightTesting extends LinearOpMode{
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
    private final double NORMAL_SPEED = 0.75;
    private final double SLOW_SPEED = 0.25;
    private final double TURBO_SPEED = 1.0;
    private final double SERVO_DURATION = 750;
    private final double TICKS_PER_REV = 28.0; // GoBilda 6k Motor has 28 Ticks per Rev per GoBilda website
    private PIDFController shooterControl;
    public static double kP = 0.004;
    public static double kI = 0.0;
    public static double kD = 0.00001;
    public static double kF = 0.00045;
    private Limelight3A limelight;
    private double distance;
    private double orientation;

    private GoBildaPinpointDriver pinpoint;


    @Override
    public void runOpMode() {
        hardwareStart();
        double speed = NORMAL_SPEED;
        servo.setPosition(RESTING_SERVO);
        double targetShooterVelocity = 0;
        double iIntakePower = 0;
        double oIntakePower = 0;
        boolean isServo = false;
        double output;

        shooterControl = new PIDFController(kP, kI, kD, kF);
        FtcDashboard dashboard = FtcDashboard.getInstance();

        waitForStart();


        while (opModeIsActive()) {

            pinpoint.update();
            limelight.updateRobotOrientation(pinpoint.getHeading(AngleUnit.DEGREES));

            LLResult result = limelight.getLatestResult();
            if (result != null && result.isValid()){
                Pose3D pose = result.getBotpose_MT2();
                distance = getDistance(result.getTa());

                telemetry.addData("LL Distance", distance);
                telemetry.addData("Ll Tx", result.getTx());
                telemetry.addData("LL Ty", result.getTy());
                telemetry.addData("ll Ta", result.getTa());
            }


            telemetry.addData("Bot Pose X", pinpoint.getPosX(DistanceUnit.INCH));
            telemetry.addData("Bot Pose Y", pinpoint.getPosY(DistanceUnit.INCH));
            telemetry.addData("Bot Heading", pinpoint.getHeading(AngleUnit.DEGREES));




            double forward = -gamepad1.left_stick_y;
            double strafe = gamepad1.left_stick_x;
            double turn = gamepad1.right_stick_x;

            double frontLeftPower = (forward + strafe + turn) * speed;
            double backLeftPower = (forward - strafe + turn) * speed;
            double frontRightPower = (forward - strafe - turn) * speed;
            double backRightPower = (forward + strafe - turn) * speed;

            frontLeft.setPower(clampFull(frontLeftPower));
            frontRight.setPower(clampFull(frontRightPower));
            backLeft.setPower(clampFull(backLeftPower));
            backRight.setPower(clampFull(backRightPower));

            if (gamepad1.right_bumper) {
                speed = TURBO_SPEED;
            } else if(gamepad1.left_bumper) {
                speed = SLOW_SPEED;
            } else {
                speed = NORMAL_SPEED;
            }













            telemetry.update();

        }
    }

    private double clampFull(double val) {
        return Math.max(-1.0, Math.min(1.0, val));
    }

    private double getDistance(double ta){
        double scale = 1; // Add scale here
        double distance = (scale / ta);
        return distance;
    }
    private void hardwareStart() {


        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
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
}
