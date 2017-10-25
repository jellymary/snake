package snake;

public class Apple extends MapObject
{
	private static final int COST = 1;

	public Apple(Point position) 
	{
		super(position);
	}
	
	public Apple(int x, int y)
	{
		super(x, y);
	}

	public void interact(Level level)
	{
		level.snake.adjustLength(COST);
		Point point = findPosition(level.snake, level.map);
		if (point != null)
		{
			try
			{					
				level.map.setObject(point.x, point.y, new Apple(point));
			}
			finally
			{
				level.map.setObject(this.getPosition().x, this.getPosition().y, null);
			}
		}
	}
}
