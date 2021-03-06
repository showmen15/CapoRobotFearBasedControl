package pl.edu.agh.capo.robot;

import pl.edu.agh.capo.fear.data.Location;

public class CapoRobotMock
{

	protected CapoRobotMotionModel capoRobotModel;

	public CapoRobotMock(double maxLinearVelocity)
	{
		this.capoRobotModel = new CapoRobotMotionModel(maxLinearVelocity);
		this.capoRobotModel.setLocation(0.4, 1.7, 0.2);

		lastMiliseconds = System.currentTimeMillis();
	}

	public CapoRobotMock(double maxLinearVelocity, double initX, double initY, double initRadius)
	{
		this.capoRobotModel = new CapoRobotMotionModel(maxLinearVelocity);
		this.capoRobotModel.setLocation(initX, initY, initRadius);

		lastMiliseconds = System.currentTimeMillis();
	}

	public void SetRoboClawVelocity(double currentVelocityLeft, double currentVelocityRight)
	{
		capoRobotModel.setVelocity(currentVelocityLeft, currentVelocityRight);
	}

	/**
	 * Simulates scanner reading, blocks for approx 200ms, returns moved robot
	 * position
	 */
	protected long lastMiliseconds;

	public Location GetRobotLocation()
	{
		long currentMiliseconds = System.currentTimeMillis();

		if (currentMiliseconds - lastMiliseconds < 200)
		{
			try
			{
				Thread.sleep(200 - (currentMiliseconds - lastMiliseconds));
			}
			catch (InterruptedException e)
			{
			}
		}

		currentMiliseconds = System.currentTimeMillis();
		capoRobotModel.moveRobot(((double) (currentMiliseconds - lastMiliseconds)) / 1000);

		lastMiliseconds = currentMiliseconds;

		return capoRobotModel.getLocation();
	}
}
