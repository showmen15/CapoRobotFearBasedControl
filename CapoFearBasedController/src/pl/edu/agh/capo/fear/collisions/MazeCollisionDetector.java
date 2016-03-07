package pl.edu.agh.capo.fear.collisions;

import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.math.Vector2D;

import pl.edu.agh.capo.fear.CapoSafeTrajectoryGenerator;
import pl.edu.agh.capo.fear.data.Location;
import pl.edu.agh.capo.fear.data.LocationInTime;
import pl.edu.agh.capo.fear.data.Trajectory;
import pl.edu.agh.capo.maze.MazeMap;
import pl.edu.agh.capo.maze.Wall;
import pl.edu.agh.capo.maze.Gate;
import pl.edu.agh.capo.robot.CapoRobotMotionModel;

public class MazeCollisionDetector implements CollisionDetector
{

	MazeMap mazeMap;
	ArrayList<LineSegment> wallsLineSegments = new ArrayList<LineSegment>();

	public MazeCollisionDetector(String mazeRosonFilename) throws Exception
	{
		mazeMap = MazeMap.loadMazeFromFile(mazeRosonFilename);
		for (Wall wall : mazeMap.getWalls())
		{
			wallsLineSegments.add(new LineSegment(wall.getFrom().getX(), wall.getFrom().getY(), wall.getTo().getX(), wall.getTo().getY()));
		}
	}

	// public double getMinimumDistanceFromWall(Location location)
	// {
	// double minDistance = Double.MAX_VALUE;
	// Coordinate locationCoordinate = new Coordinate(location.positionX,
	// location.positionY);
	// for (LineSegment segment : wallsLineSegments)
	// {
	// double distance = segment.distance(locationCoordinate);
	// if (distance < minDistance)
	// minDistance = distance;
	// }
	// return minDistance;
	// }
	//
	// public double getMinimumDistanceFromWall(Trajectory trajectory)
	// {
	// double minDistance = Double.MAX_VALUE;
	// for (LocationInTime locationInTime : trajectory.getTrajectorySteps())
	// {
	// double distance =
	// getMinimumDistanceFromWall(locationInTime.getLocation());
	// if (distance < minDistance)
	// minDistance = distance;
	// }
	// return minDistance;
	// }

	/**
	 * 
	 * @param trajectory
	 * @return -1 if no collision detected
	 */
	public int getCollidingTrajecotryMinStepNumber(Trajectory trajectory)
	{
		int trajectoryStepNumber = 0;
		for (LocationInTime locationInTime : trajectory.getTrajectorySteps())
		{
			if (trajectoryStepNumber == 0) // skip current robot position
			{
				trajectoryStepNumber++;
				continue;
			}
			if (trajectoryStepNumber > 3)// CapoSafeTrajectoryGenerator.trajectoryStepCount
											// / 2)
			{
				return -1; // only piece of trajectory verified against walls
			}
			Coordinate locationCoordinate = new Coordinate(locationInTime.getLocation().positionX, locationInTime.getLocation().positionY);
			for (LineSegment segment : wallsLineSegments)
			{
				if (segment.distance(locationCoordinate) < CapoRobotMotionModel.robotHalfDiameter)
					return trajectoryStepNumber;
			}
			trajectoryStepNumber++;
		}
		return -1;
	}

	public Vector2D getNextDestination(Vector2D currentLocation, Vector2D targetDestination)
	{
		return targetDestination;
	}

}
