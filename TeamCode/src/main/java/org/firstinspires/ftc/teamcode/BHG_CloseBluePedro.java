package org.firstinspires.ftc.teamcode;

import com.arcrobotics.ftclib.controller.PIDFController;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
@Autonomous(name = "Close Blue Pedro Test - Ben", group = "Autonomous")
@Configurable // Panels
public class BHG_CloseBluePedro extends OpMode {
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
    private boolean intakeReverse = false;
    private double output;
    private double targetShooterVelocity = 1460;
    private enum ShooterStates {
        IDLE,
        LOAD,
        FIRST_TWO_SHOTS,
        THIRD_SHOT,
        END
    }
    private ShooterStates shooterState = ShooterStates.IDLE;
    private enum AutoSteps {
        ONE,
        TWO,
        THREE
    }
    private AutoSteps autoStep = AutoSteps.ONE;
    private GoBildaPinpointDriver pinpoint;
    private TelemetryManager panelsTelemetry; // Panels Telemetry instance
    public Follower follower; // Pedro Pathing follower instance
    private int pathState; // Current autonomous path state (state machine)
    private Paths paths; // Paths defined in the Paths class

    @Override
    public void init() {

        hardwareStart();

        shooterControl = new PIDFController(kP, kI, kD, kF);

        servo.setPosition(RESTING_SERVO);

        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(72, 8, Math.toRadians(90)));

        paths = new Paths(follower); // Build paths

        panelsTelemetry.debug("Status", "Initialized");
        panelsTelemetry.update(telemetry);
    }

    @Override
    public void loop() {

        double shooterVelocity = shooter.getVelocity();
        output = shooterControl.calculate(shooterVelocity, targetShooterVelocity);

        follower.update(); // Update Pedro Pathing
        pathState = autonomousPathUpdate(); // Update autonomous state machine

        switch (autoStep) {
            case ONE:
                follower.followPath(paths.Path1);
                autoStep = autoStep.TWO;
                break;
            case TWO:
                if (!follower.isBusy()) {
                    shootThree();

                   if (shooterState == ShooterStates.END) {
                        intakeSet(1,1);
                        follower.followPath(paths.Path2);
                        autoStep = AutoSteps.THREE;
                    }
                }
                break;
            case THREE:
                if (!follower.isBusy() && !intakeReverse) {
                    follower.followPath(paths.Path3);
                    timer.reset();
                    intakeReverse = false;
                }

                if (!follower.isBusy()) {

                    if (!intakeReverse) {
                        intakeSet(-0.25, -0.1);

                        if (timer.milliseconds() > 100) {
                            intakeSet(0, 0);
                            shooterState = ShooterStates.IDLE;
                            intakeReverse = true;
                        }
                    }
                    if (intakeReverse) {
                        shootThree();
                    }
                }
                break;
        }


        // Log values to Panels and Driver Station
        panelsTelemetry.debug("Path State", pathState);
        panelsTelemetry.debug("X", follower.getPose().getX());
        panelsTelemetry.debug("Y", follower.getPose().getY());
        panelsTelemetry.debug("Heading", follower.getPose().getHeading());
        panelsTelemetry.update(telemetry);
    }

    public static class Paths {

        public PathChain Path1;
        public PathChain Path2;
        public PathChain Path3;

        public Paths(Follower follower) {
            Path1 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(25.162, 129.719), new Pose(52.024, 91.466))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(143), Math.toRadians(130))
                    .build();

            Path2 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierCurve(
                                    new Pose(52.024, 91.466),
                                    new Pose(96.907, 80.076),
                                    new Pose(16.661, 83.646)
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(143), Math.toRadians(180))
                    .build();

            Path3 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(16.661, 83.646), new Pose(52.024, 91.636))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(130))
                    .build();
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

                    shooterState = shooterState.END;
                }
                break;

            case END:
                shooter.setPower(0);
                break;
        }
    }
    public boolean atTargetSpeed(double shooterVelocity, double targetVelocity, double range) {
        return Math.abs(shooterVelocity - targetVelocity) <= range;
    }
    public int autonomousPathUpdate() {
        // Add your state machine Here
        // Access paths with paths.pathName
        // Refer to the Pedro Pathing Docs (Auto Example) for an example state machine
        return pathState;
    }
}
