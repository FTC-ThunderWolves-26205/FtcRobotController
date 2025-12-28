//In theory, back up, shoot 3, intake first set.  stop
//start bot centered on the line for blue goal


package org.firstinspires.ftc.teamcode;
import com.arcrobotics.ftclib.controller.PIDFController;
import com.pedropathing.follower.Follower;
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

@Autonomous(name = "Pedro Testing", group = "Autonomous")

public class JLG_PedroTesting extends LinearOpMode {

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
    private double output;
    private double targetShooterVelocity = 1460;

    private GoBildaPinpointDriver pinpoint;

    private ElapsedTime timer = new ElapsedTime();
    private ElapsedTime servoTimer = new ElapsedTime();

    private Follower follower;


    // POSES GO HERE
    private final Pose startPose = new Pose(25, 119, Math.toRadians(145)); // Start position
    private final Pose firstShotPose = new Pose(57, 86, Math.toRadians(145)); // Path 1
    private final Pose intakeFirst = new Pose(19, 86, Math.toRadians(180)); // Path 2

    //RENAME THESE
    private PathChain firstShotPath, firstIntakePath;



    private enum AutoState {
        START_PATH1,
        WAIT_PATH1,
        SHOOT,
        WAIT_PATH2,
        END
    }

    private enum ShooterState {
        IDLE,
        SHOOT_TWO,
        SHOOT_THIRD,
        STOP_INTAKES,
        END
    }
    private AutoState autoState = AutoState.START_PATH1;
    private ShooterState shooterState = ShooterState.IDLE;



    @Override
    public void runOpMode() throws InterruptedException {

        hardwareStart();

        initialize();

        shooterControl = new PIDFController(kP, kI, kD, kF);
        FtcDashboard dashboard = FtcDashboard.getInstance();

        pinpoint.resetPosAndIMU();

        servo.setPosition(RESTING_SERVO);


        waitForStart();




        while (opModeIsActive()) {

            // We don't need pinpoint.update(); since pedro handles for us

            follower.update();

            double shooterVelocity = shooter.getVelocity();
            output = shooterControl.calculate(shooterVelocity, targetShooterVelocity);


            autonomousPathUpdate(); // This calls our state machine.  It's all we need in the main loop







            telemetry.addData("x", follower.getPose().getX());
            telemetry.addData("y", follower.getPose().getY());
            telemetry.addData("heading", follower.getPose().getHeading());
            telemetry.update();

        }
    }



//BUILD PATHS HERE
    public void buildPaths() {
        firstShotPath = follower.pathBuilder()
                .addPath(new BezierLine(startPose, firstShotPose))
                .setLinearHeadingInterpolation(startPose.getHeading(), firstShotPose.getHeading())
                .build();

        firstIntakePath = follower.pathBuilder()
                .addPath(new BezierLine(firstShotPose, intakeFirst))
                .setLinearHeadingInterpolation(firstShotPose.getHeading(), intakeFirst.getHeading())
                .build();
    }

    public void autonomousPathUpdate() {
        switch (autoState) {

            case START_PATH1:
                follower.followPath(firstShotPath);
                autoState = AutoState.WAIT_PATH1;
                break;

            case WAIT_PATH1:
                if (!follower.isBusy()) {
                    shooterState = ShooterState.IDLE;   // reset shooter FSM
                    autoState = AutoState.SHOOT;
                }
                break;

            case SHOOT:
                shootThree();

                if (shooterState == ShooterState.END) {
                    intakeSet(0.5, 0.5);
                    follower.followPath(firstIntakePath);
                    autoState = AutoState.WAIT_PATH2;
                }
                break;


            case WAIT_PATH2:
                if (!follower.isBusy()) {
                    intakeSet(0, 0);
                    autoState = AutoState.END;
                }
                break;

            case END:
                // Autonomous complete
                break;
        }
    }

    private void shootThree() {
        shooter.setPower(Math.abs(output));

        switch (shooterState) {

            case IDLE:
                timer.reset();
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
                if (timer.milliseconds() > 2000) {
                    servoMovement();
                    shooterState = ShooterState.STOP_INTAKES;
                }
                break;

            case STOP_INTAKES:
                if (servoTimer.milliseconds() > SERVO_DURATION) {
                    servo.setPosition(RESTING_SERVO);
                    intakeSet(0, 0);
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
        oIntake = hardwareMap.get(DcMotor.class,"OID");
        iIntake = hardwareMap.get(DcMotor.class,"IID");
        servo = hardwareMap.get(Servo.class, "servo");

        shooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        oIntake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        iIntake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


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

    public boolean atTargetSpeed(double shooterVelocity, double targetVelocity, double range) {
        return Math.abs(shooterVelocity - targetVelocity) <= range;
    }
}

