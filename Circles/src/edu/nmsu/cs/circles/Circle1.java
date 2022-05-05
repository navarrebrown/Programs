package edu.nmsu.cs.circles;

public class Circle1 extends Circle
{

	public Circle1(double x, double y, double radius)
	{
		super(x, y, radius);
	}

	public boolean intersects(Circle other)
	{
		// distance between the two centers
		double dist = Math.sqrt((Math.pow(this.center.x - other.center.x, 2)) + (Math.pow(this.center.y - other.center.y, 2))); 
  
		// sum of the radii
		double radSum = this.radius + other.radius;

		if (dist <= radSum) {
			return true;
		}

		return false;
	}
}
