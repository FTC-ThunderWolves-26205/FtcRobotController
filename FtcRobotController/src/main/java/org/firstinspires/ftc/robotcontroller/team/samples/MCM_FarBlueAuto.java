//


package org.firstinspires.ftc.robotcontroller.team.samples;
import com.arcrobotics.ftclib.controller.PIDFController;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
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


@Autonomous(name = "Far Blue - Maddie", group = "Autonomous")
@Disabled

public class MCM_FarBlueAuto extends LinearOpMode {

    private DcMotorEx shooter;
    private DcMotor iIntake;
    private DcMotor oIntake;
    private Servo servo;

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
    //private final Pose startPose = new Pose(48,9,105); //where the robot starts - middle of back of robot centered on bottom of left side of back triangle-lines
    private final Pose shootPose = new Pose(60,9,105); //the position where robot shoots, in back left
    private final Pose firstIntakePose = new Pose(8.8,35.7,180);

    //PATHS GO HERE.  USE DESCRIPTIVE NAMES.
    private PathChain
            //toLaunch1,
            Intake1;


    //ENUM DEFINING STATES FOR AUTO PATH.  YOU MUST HAVE A WAIT STEP AFTER ANY STEP THAT MOVES THE ROBOT.
    //THE WAIT STEP MUST INCLUDE A CHECK TO SEE IF FOLLOWER.ISBUSY IS FALSE

    //Standard Order and Terminology:
    //        MOVE_TO_SHOOT1,
    //        SHOOT1,
    //        INTAKE1,
    //        WAIT1,
    //        ... REPEAT ...
    //        END

    private enum AutoState {
        TO_LAUNCH1,
        LAUNCH1,
        INTAKE1,
        WAIT1,
        TO_LAUNCH2, //Moving from end of intake path to shootPose
        LAUNCH2,
        INTAKE2, // back set of balls
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

    //SETTING STATES FOR OUR TWO FSM'S
    private AutoState autoState = AutoState.LAUNCH1;
    private ShooterState shooterState = ShooterState.IDLE;


    @Override
    public void runOpMode() throws InterruptedException {

        hardwareStart();  //Initializing all our non-movement hardware.

        initialize();  //Initializing Pedro, Building Paths

        shooterControl = new PIDFController(kP, kI, kD, kF);
        FtcDashboard dashboard = FtcDashboard.getInstance();  //not using dashboard but keep for now in case we do

     


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
//        toLaunch1 = follower.pathBuilder()
//                .addPath(new BezierLine(startPose,shootPose))
//                .setLinearHeadingInterpolation(startPose.getHeading(), shootPose.getHeading())
//                .build();

        Intake1 = follower.pathBuilder()
                .addPath(new BezierCurve(
                        shootPose,
                        new Pose (57.4,37.5),
                        firstIntakePose))
                .setLinearHeadingInterpolation(shootPose.getHeading(),firstIntakePose.getHeading())
                .build();

    }

    /*
    Here is our main FSM for the auto.
    We MUST add a wait after ANY movement.  The wait should include a check to see if the follower.isBusy() is false.
    Leave comment describing what you are doing on each path.
     */
    public void autonomousPathUpdate() {
        switch (autoState) {

//            case TO_LAUNCH1:
//                follower.followPath(toLaunch1);
//                autoState = AutoState.LAUNCH1;
//                break;

            case LAUNCH1:
                if (!follower.isBusy()) {
                    shootThree();
                }
                if (shooterState == ShooterState.END) {
                    shooter.setPower(0);
                    intakeSet(1,0.85);
                    autoState = AutoState.INTAKE1;
                }
                break;

            case INTAKE1:
                follower.setMaxPower(0.8);
                follower.followPath(Intake1);
                autoState = AutoState.WAIT1;
                break;

            case WAIT1:
                if (!follower.isBusy()) {
                    intakeSet(0,0);
                    autoState = AutoState.END;
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
                targetShooterVelocity = 1800;
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
                   // servo.setPosition(RESTING_SERVO);
                    intakeSet(0, 0);
                    targetShooterVelocity = 0;
                    shooterState = ShooterState.END;
                }
                break;

            case END:

                break;
        }
    }

    private void initialize() {

  
        follower.setStartingPose(shootPose);
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
    //    servo.setPosition(LAUNCHING_SERVO);
        servoTimer.reset();
    }

    public boolean atTargetSpeed(double shooterVelocity, double targetVelocity, double range) {
        return Math.abs(shooterVelocity - targetVelocity) <= range;
    }
}


