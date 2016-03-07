package pl.edu.agh.capo.hough;

import java.util.Arrays;
import java.util.List;

import pl.edu.agh.capo.logic.common.Measure;
import pl.edu.agh.capo.logic.common.Vision;

public class CapoHough {

	protected final int houghSpaceThetaStepDegrees = 3;
	protected final int houghSpaceRadiusStepMilimeters = 30;
	protected final int houghSpaceRadiusMaxMilimeters = 5000;	
	
	protected int [][] houghSpace = new int[180/houghSpaceThetaStepDegrees][houghSpaceRadiusMaxMilimeters/houghSpaceRadiusStepMilimeters];
	
	public CapoHough()
	{		
	}
	
	public void getHoughLines(Measure measure)
	{
		for (int[] row: houghSpace)
		    Arrays.fill(row, 0);
		
		List<Vision> visions = measure.getVisions();
		
		for (Vision vision: visions)
		{
			int distance = (int)(vision.getDistance() * 1000);
			if (distance < 100 || distance > 5000)
				continue;
			
			for (int degreeStep = 0 ; degreeStep < 180/houghSpaceThetaStepDegrees ; degreeStep++ )
			{
				int degree = degreeStep * houghSpaceThetaStepDegrees - 90;
				houghSpace[(int)(360+ (vision.getAngle()+degree)/houghSpaceThetaStepDegrees)%(180/houghSpaceThetaStepDegrees)][(int)(Math.cos(Math.toRadians(degree))*distance/houghSpaceRadiusStepMilimeters)] ++;
			}
		}
		
		int maxH = 0;
		int maxHangle = -1;
		int maxHdistance = -1;
		
				
		for (int degreeStep = 0 ; degreeStep < 180/houghSpaceThetaStepDegrees ; degreeStep++ )
		{
			for (int distanceStep = 0 ; distanceStep < houghSpaceRadiusMaxMilimeters/houghSpaceRadiusStepMilimeters ; distanceStep++ )
			{
				if (houghSpace[degreeStep][distanceStep] > maxH)
				{
					maxH = houghSpace[degreeStep][distanceStep];
					maxHangle = degreeStep * houghSpaceThetaStepDegrees;
					maxHdistance = distanceStep * houghSpaceRadiusStepMilimeters;
				}
				if (houghSpace[degreeStep][distanceStep] > 44)
					System.out.println("->"+houghSpace[degreeStep][distanceStep]+" at deg="+(degreeStep * houghSpaceThetaStepDegrees)+" and dist="+(distanceStep * houghSpaceRadiusStepMilimeters));

			}
			
		}
		System.out.println("\n==========>>  Hmax="+maxH+" at deg="+maxHangle+" and dist="+maxHdistance);
		
	}
	
	
}
