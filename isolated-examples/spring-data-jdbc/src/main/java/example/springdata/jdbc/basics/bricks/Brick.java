package example.springdata.jdbc.basics.bricks;

import org.springframework.data.annotation.Id;

/**
 * @author Jens Schauder
 */
public class Brick {
	@Id
	Long Id;

	private String description;

	Brick(String description) {
		this.description = description;
	}
}
