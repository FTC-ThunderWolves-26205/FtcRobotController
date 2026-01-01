//Use this base code to build auto.  Use visualizer to get pose points and build paths.
//See comments below.
//DO NOT TOUCH SHOOT THREE METHOD.


package org.firstinspires.ftc.teamcode;
import com.arcrobotics.ftclib.controller.PIDFController;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.util.ElapsedTime;


import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Autonomous(name = "Close Blue Test - BEN", group = "Autonomous")

public class BHG_CloseBlueAutoTest extends LinearOpMode {

    private DcMotorEx shooter;
    private DcMotor iIntake;
    private DcMotor oIntake;
    private Servo servo;
    private static final double RESTING_SERVO = 0.35;
    private static final double LAUNCHING_SERVO = 0.;
    private final double RANGE = 40;
    private final long SERVO_DURATION = 500;
    private PIDFController shooterControl;
    public static double kP = 0.004;
    public static double kI = 0.0;
    public static double kD = 0.00001;
    public static double kF = 0.00045;
    private double output;
    private double targetShooterVelocity = 1460;
    private boolean intakeReverse = false;
    private boolean intakeReverseStarted = false;

    private GoBildaPinpointDriver pinpoint;

    private ElapsedTime timer = new ElapsedTime();
    private ElapsedTime servoTimer = new ElapsedTime();
    private ElapsedTime intakeTimer = new ElapsedTime();

    private Follower follower;


    /*
    POSES GO HERE.
    ADD A COMMENT AFTER EACH POSE DESCRIBING WHAT IT IS.
     */
    private final Pose startPose = new Pose(25, 129, Math.toRadians(143)); // Start position
    private final Pose firstShotPose = new Pose(59, 84, Math.toRadians(130)); // Pose for First Group of Shots
    private final Pose firstIntakePose = new Pose(16, 83, Math.toRadians(192)); // Pose for Intake 3 more
    private final Pose secondShotPose = new Pose(59,  84, Math.toRadians(130)); // Pose for Second Group of Shots
    private final Pose secondIntakePose = new Pose(10.37,59.5, Math.toRadians(180)); // Pose for the middle 3 artifacts
    private final Pose thirdShotPose = new Pose(59,83, Math.toRadians(130));

    //PATHS GO HERE.  USE DESCRIPTIVE NAMES.
    private PathChain firstShotPath, firstIntakePath, secondShotPath, secondIntakePath, thirdShotPath;


    //ENUM DEFINING STATES FOR AUTO PATH.  YOU MUST HAVE A WAIT STEP AFTER ANY STEP THAT MOVES THE ROBOT.
    //THE WAIT STEP MUST INCLUDE A CHECK TO SEE IF FOLLOWER.ISBUSY IS FALSE
    private enum AutoState {
        MOVE_TO_SHOOT1,
        SHOOT1,
        INTAKE1,
        WAIT1,
        MOVE_TO_SHOOT2,
        SHOOT2,
        INTAKE2,
        WAIT2,
        MOVE_TO_SHOOT3,
        SHOOT3,
        END
    }

    //ENUM DEFINING STATES FOR SHOOTER.  DON'T MODIFY THIS AS IT TIES TO OUR SHOOTTHREE METHOD.
    private enum ShooterState {
        IDLE,
        SHOOT_TWO,
        SHOOT_THIRD,
        STOP_INTAKES,
        END
    }
    private enum ReverseIntakes {
        START_REVERSE_INTAKES,
        STOP_INTAKES,
        END

    }

    //SETTING STATES FOR OUR TWO FSM'S
    private AutoState autoState = AutoState.MOVE_TO_SHOOT1;
    private ShooterState shooterState = ShooterState.IDLE;
    private ReverseIntakes reverseIntakes = ReverseIntakes.START_REVERSE_INTAKES;


    @Override
    public void runOpMode() throws InterruptedException {

        hardwareStart();  //Initializing all our non-movement hardware.

        initialize();  //Initializing Pedro, Building Paths

        shooterControl = new PIDFController(kP, kI, kD, kF);
        FtcDashboard dashboard = FtcDashboard.getInstance();  //not using dashboard but keep for now in case we do

        servo.setPosition(RESTING_SERVO);


        waitForStart();


        while (opModeIsActive()) {

            // We don't need pinpoint.update(); since pedro handles for us

            follower.update();


            autonomousPathUpdate(); // This calls our state machine.  It's all we need in the main loop
            //Ben wants to put the state machine here instead and just get rid of autonomousPathUpdate().  Considering it...


            telemetry.addData("x", follower.getPose().getX());
            telemetry.addData("y", follower.getPose().getY());
            telemetry.addData("heading", follower.getPose().getHeading());
            telemetry.update();

        }
    }


    //BUILD PATHS HERE
    //EACH MUST BE INTRODUCED ABOVE IN THE PATHCHAIN FIRST
    //USE DESCRIPTIVE NAME, ACTION OR DESTINATION
    public void buildPaths() {
        firstShotPath = follower.pathBuilder()
                .addPath(new BezierLine(startPose, firstShotPose))
                .setLinearHeadingInterpolation(startPose.getHeading(), firstShotPose.getHeading())
                .build();

        firstIntakePath = follower.pathBuilder()
                .addPath(new BezierLine(firstShotPose, firstIntakePose))
                .setLinearHeadingInterpolation(firstShotPose.getHeading(), firstIntakePose.getHeading())
                .build();

        secondShotPath = follower.pathBuilder()
                .addPath(new BezierLine(firstIntakePose, secondShotPose))
                .setLinearHeadingInterpolation(firstIntakePose.getHeading(), secondShotPose.getHeading())
                .build();

        secondIntakePath = follower.pathBuilder()
                .addPath(new BezierCurve(
                        secondShotPose,
                        new Pose(63.4, 53.6, Math.toRadians(0)),  // CONTROL POINT
                        secondIntakePose
                ))
                .setLinearHeadingInterpolation(secondShotPose.getHeading(), secondIntakePose.getHeading())
                .build();

        thirdShotPath = follower.pathBuilder()
                .addPath(new BezierLine(secondIntakePose, thirdShotPose))
                .setLinearHeadingInterpolation(secondIntakePose.getHeading(), thirdShotPose.getHeading())
                .build();

    }

    /*
    Here is our main FSM for the auto.
    We MUST add a wait after ANY movement.  The wait should include a check to see if the follower.isBusy() is false.
    Leave comment describing what you are doing on each path.
     */
    public void autonomousPathUpdate() {
        switch (autoState) {

            case MOVE_TO_SHOOT1:  //Back up from starting position.
                follower.followPath(firstShotPath);
                autoState = AutoState.SHOOT1;
                break;

            case SHOOT1:  //Shoot three after movement, then turn on Intakes
                if (!follower.isBusy()) {
                    shootThree();
                }
                if (shooterState == ShooterState.END) {
                    shooter.setPower(0);
                    intakeSet(1, 0.85);
                    autoState = AutoState.INTAKE1;
                }

                break;

            case INTAKE1:  //Move and get the three artifacts
                follower.setMaxPower(0.8);
                follower.followPath(firstIntakePath);
                autoState = AutoState.WAIT1;
                break;

            case WAIT1:  //Wait after movement. Stop intakes.
                if (!follower.isBusy()) {
                    intakeSet(0, 0);
                    autoState = AutoState.MOVE_TO_SHOOT2;
                }
                break;

            case MOVE_TO_SHOOT2:  //Move back to shooting position
                follower.setMaxPower(1);
                follower.followPath(secondShotPath);
                shooterState = ShooterState.IDLE;
                reverseIntakes = ReverseIntakes.START_REVERSE_INTAKES;
                autoState = AutoState.SHOOT2;
                break;

            case SHOOT2:  //Reverse intakes, then shoot second group of artifacts
                if (!follower.isBusy()) {
                    intakeReverse();
                    if (reverseIntakes == ReverseIntakes.END) {
                        shootThree();
                    }
                    if (shooterState == ShooterState.END) {
                        shooter.setPower(0);
                        autoState = AutoState.INTAKE2;
                    }
                }
                break;

            case INTAKE2: //  Turn on Intakes, drives to get the middle three
                intakeSet(1, 0.85);
                follower.setMaxPower(0.8);
                follower.followPath(secondIntakePath);
                autoState = AutoState.WAIT2;
                break;

            case WAIT2: //  Wait after movement
                if(!follower.isBusy()) {
                    intakeSet(0,0);
                    autoState = AutoState.MOVE_TO_SHOOT3;
                }
                break;

            case MOVE_TO_SHOOT3: //  Move back to shooting position. Expecting to have trouble bumping into gate
                follower.setMaxPower(1);
                follower.followPath(thirdShotPath);
                shooterState = ShooterState.IDLE;
                reverseIntakes = ReverseIntakes.START_REVERSE_INTAKES;
                autoState = AutoState.SHOOT3;
                break;

            case SHOOT3: //  Reverse Intakes, then shoot
                if (!follower.isBusy()) {
                    intakeReverse();
                    if (reverseIntakes == ReverseIntakes.END) {
                        shootThree();
                    }
                    if (shooterState == ShooterState.END) {
                        shooter.setPower(0);
                        autoState = AutoState.END;
                    }
                }
                break;


            case END: //Always have an END.  Seems to be recommended to keep it empty.

                break;
        }
    }

    /*
    Here is our FSM for shooting three.
     */
    private void shootThree() {
        double shooterVelocity = shooter.getVelocity();
        output = shooterControl.calculate(shooterVelocity, targetShooterVelocity);
        shooter.setPower(output);


        switch (shooterState) {

            case IDLE:
                timer.reset();
                servoTimer.reset();
                targetShooterVelocity = 1460;
                shooterState = ShooterState.SHOOT_TWO;
                break;


            case SHOOT_TWO:

                if (atTargetSpeed(shooter.getVelocity(), targetShooterVelocity, RANGE) && timer.milliseconds() > 1000) {
                    intakeSet(1, 1);
                    timer.reset();
                    shooterState = ShooterState.SHOOT_THIRD;
                }
                break;

            case SHOOT_THIRD:
                if (timer.milliseconds() > 2500) {
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
    private void intakeReverse() {
        switch (reverseIntakes) {
            case START_REVERSE_INTAKES:
                intakeSet(-0.25,-0.1);
                intakeTimer.reset();
                reverseIntakes = ReverseIntakes.STOP_INTAKES;
                break;

            case STOP_INTAKES:
                if (intakeTimer.milliseconds() > 250) {
                    intakeSet(0, 0);
                    reverseIntakes = ReverseIntakes.END;
                }
                break;

            case END:
                break;
        }
    }

    private void initialize() {

        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(startPose);
        buildPaths();

    }


    private void hardwareStart() {

        shooter = hardwareMap.get(DcMotorEx.class, "SD");
        oIntake = hardwareMap.get(DcMotor.class, "OID");
        iIntake = hardwareMap.get(DcMotor.class, "IID");
        servo = hardwareMap.get(Servo.class, "servo");

        shooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        oIntake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        iIntake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        shooter.setDirection(DcMotorSimple.Direction.REVERSE);
        oIntake.setDirection(DcMotorSimple.Direction.FORWARD);
        iIntake.setDirection(DcMotorSimple.Direction.FORWARD);

        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
        pinpoint.setPosition(new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0));

        telemetry.addData("Status", "Initialized");
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

    public boolean atTargetSpeed(double shooterVelocity, double targetVelocity, double range) {
        return Math.abs(shooterVelocity - targetVelocity) <= range;
    }
}

