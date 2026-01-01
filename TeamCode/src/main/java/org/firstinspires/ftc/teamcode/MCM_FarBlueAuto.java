
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

@Autonomous(name = "Far Blue", group = "Autonomous")

public class MCM_FarBlueAuto extends LinearOpMode {

    private DcMotorEx shooter;
    private DcMotor iIntake;
    private DcMotor oIntake;
    private Servo servo;
    private static final double RESTING_SERVO = MCM_Constants.resting_servo;
    private static final double LAUNCHING_SERVO = MCM_Constants.launching_servo;
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
    Pose startPose = new Pose(48,9,90);
    Pose shootPose = new Pose(66.2,19.7,115);

    //PATHS GO HERE.  USE DESCRIPTIVE NAMES.
    private PathChain ToLaunch1;


    //ENUM DEFINING STATES FOR AUTO PATH.  YOU MUST HAVE A WAIT STEP AFTER ANY STEP THAT MOVES THE ROBOT.
    //THE WAIT STEP MUST INCLUDE A CHECK TO SEE IF FOLLOWER.ISBUSY IS FALSE
    private enum AutoState {
       ToLaunch1,
        End
    }

    //ENUM DEFINING STATES FOR SHOOTER.  DON'T MODIFY THIS AS IT TIES TO OUR SHOOTTHREE METHOD.
    private enum ShooterState {
        START_SHOOTER,
        SHOOT_TWO,
        SHOOT_THIRD,
        STOP_INTAKES,
        END
    }

    //SETTING STATES FOR OUR TWO FSM'S
    private AutoState autoState = AutoState.ToLaunch1;
    private ShooterState shooterState = ShooterState.START_SHOOTER;


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
        ToLaunch1 = follower.pathBuilder()
                .addPath(new BezierLine(startPose,shootPose))
                .setLinearHeadingInterpolation(startPose.getHeading(), shootPose.getHeading())
                .build();

    }

    /*
    Here is our main FSM for the auto.
    We MUST add a wait after ANY movement.  The wait should include a check to see if the follower.isBusy() is false.
    Leave comment describing what you are doing on each path.
     */
    public void autonomousPathUpdate() {
        switch (autoState) {

            case ToLaunch1:
                if (!follower.isBusy()) {
                    follower.
                }


            case End: //Always have an END.  Seems to be recommended to keep it empty.

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

            case START_SHOOTER:
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


