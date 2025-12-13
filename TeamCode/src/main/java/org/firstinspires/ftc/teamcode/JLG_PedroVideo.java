package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
@Disabled
public class JLG_PedroVideo extends OpMode {

    private Follower follower;
    private Timer pathTimer;
    private Timer opModeTimer;

    public enum PathState{
        START_POS,
        SHOOT_POS
    }

    PathState pathState;

    private final Pose startPose = new Pose(20.12312345,120.123454, Math.toRadians(138));
    private final Pose shootPose = new Pose(20.156156,120.156186, Math.toRadians(138));

    private PathChain startToShoot;

    private void buildPaths() {
        startToShoot = follower.pathBuilder()
                .addPath(new BezierLine(startPose, shootPose))
                .setLinearHeadingInterpolation(startPose.getHeading(), shootPose.getHeading())
                .build();

    }
    public void statePathUpdate(){
        switch(pathState){
            case DRIVE_STARTPOS_SHOOT_POS;
            follower.followPath(startToShoot, true);
        }
    }
    @Override
    public void init() {

    }


    @Override
    public void loop() {

    }

}
