package pl.edu.agh.capo.fear;

import java.util.ArrayList;
import java.util.Random;

import com.vividsolutions.jts.math.Vector2D;

import pl.edu.agh.capo.fear.collisions.CollisionDetector;
import pl.edu.agh.capo.fear.data.LocationInTime;
import pl.edu.agh.capo.fear.data.Trajectory;
import pl.edu.agh.capo.robot.CapoRobotMotionModel;

public class CapoSafeTrajectoryGenerator
{

	public static final double trajectoryTimeStep = 0.2;
	public static final int trajectoryStepCount = 8;

	public static final int alternativeTrajectoriesTested = 100;

	protected ArrayList<CollisionDetector> collisionDetectors = new ArrayList<CollisionDetector>();
	protected Random random = new Random();

	protected double lastBestVelocityLeft = 0, lastBestVelocityRight = 0;

	public CapoSafeTrajectoryGenerator()
	{
	}

	public void addColisionDetector(CollisionDetector collisionDetector)
	{
		collisionDetectors.add(collisionDetector);
	}

	// public Vector2D newDestinations(Vector2D currentLocation, Vector2D
	// targetDestination)
	// {
	// CollisionDetector collisionDetector = collisionDetectors.get(0);
	//
	// if (collisionDetector != null)
	// return collisionDetector.getNextDestination(currentLocation,
	// targetDestination);
	// else
	// return null;
	// }

	/**
	 * 
	 * @param motionModel
	 *            -- velocity can and will be modified if given one causes
	 *            collisions
	 * @param destination
	 * @return Safe (collision-free) trajectory, which brings the robot closest
	 *         to the destination.
	 * 
	 */
	public Trajectory getSafeTrajectory(CapoRobotMotionModel motionModel, Vector2D destination)
	{
		Trajectory trajectory = buildTrajectory(motionModel, destination);

		if (getTrajectoryCost(trajectory, destination) > 0) // given is safe
			return trajectory;

		double bestTrajectoryCost = Double.MAX_VALUE;
		double bestVelocityLeft = 0, bestVelocityRight = 0;
		Trajectory bestTrajectory = null;

		// //////// restore last best
		motionModel.setVelocity(lastBestVelocityLeft, lastBestVelocityRight);
		trajectory = buildTrajectory(motionModel, destination);
		double trajectoryCost = getTrajectoryCost(trajectory, destination);
		if (trajectoryCost > 0)
		{
			bestVelocityLeft = lastBestVelocityLeft;
			bestVelocityRight = lastBestVelocityRight;
			bestTrajectoryCost = trajectoryCost;
			bestTrajectory = trajectory;
		}

		// //////// try find better
		for (int timeout = alternativeTrajectoriesTested; timeout > 0; timeout--)
		{
			motionModel.setVelocity_LinearVelocity_AngularVelocity(
			// both directions:
			1.6 * motionModel.getMaxLinearVelocity() * (random.nextDouble() - 0.5),
			// 0.8 * motionModel.getMaxLinearVelocity() * random.nextDouble(),
			3 * Math.PI * (random.nextDouble() - 0.5));

			trajectory = buildTrajectory(motionModel, destination);
			trajectoryCost = getTrajectoryCost(trajectory, destination);
			if (trajectoryCost > 0 && trajectoryCost < bestTrajectoryCost)
			{
				bestVelocityLeft = motionModel.getVelocityLeft();
				bestVelocityRight = motionModel.getVelocityRight();
				bestTrajectoryCost = trajectoryCost;
				bestTrajectory = trajectory;
			}
		}

		if (bestTrajectoryCost < Double.MAX_VALUE)
		{
			motionModel.setVelocity(bestVelocityLeft, bestVelocityRight);
			lastBestVelocityLeft = bestVelocityLeft;
			lastBestVelocityRight = bestVelocityRight;
			return bestTrajectory;
		}
		else
		{			
			motionModel.setVelocity(0, 0);
			return buildTrajectory(motionModel, destination); // 'stop'
																// trajectory
		}

	}

	/**
	 * 
	 * @param trajectory
	 * @return -1 if the trajectory causes collision according to any
	 *         CollisionDetector. Final distance to destination otherwise.
	 */
	private double getTrajectoryCost(Trajectory trajectory, Vector2D destination)
	{
		for (CollisionDetector collisionDetector : collisionDetectors)
			if (collisionDetector.getCollidingTrajecotryMinStepNumber(trajectory) >= 0)
				return -1;

		return trajectory.getLastStepLocation().getPositionVector().distance(destination);
	}

	/**
	 * Just generates steps of the trajectory by given motionModel
	 * 
	 * @param motionModel
	 * @param destination
	 * @return
	 */
	public static Trajectory buildTrajectory(CapoRobotMotionModel motionModel, Vector2D destination)
	{
		Trajectory trajectory = new Trajectory(destination);
		for (int i = 0; i < trajectoryStepCount; i++)
			trajectory.addTrajectoryStep(new LocationInTime(motionModel.getLocationAfterTime(trajectoryTimeStep * i), trajectoryTimeStep * i));
		return trajectory;
	}

}
