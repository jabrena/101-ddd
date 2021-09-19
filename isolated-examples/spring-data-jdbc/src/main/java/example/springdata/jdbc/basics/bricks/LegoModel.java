package example.springdata.jdbc.basics.bricks;

import lombok.val;
import org.springframework.data.annotation.Id;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Jens Schauder
 */
public class LegoModel {

	@Id
	private Long id;
	private String name;
	private List<Manual> manuals = new ArrayList<>();
	/**
	 * maps the id of a brick to the number of pieces contained.
	 */
	private Set<BrickContentItem> content = new HashSet<>();

	public LegoModel(String name) {
		this.name = name;
	}


	public void add(Brick brick, Integer amount) {

		Assert.notNull(brick, "We can't add <null> bricks to a model");
		Assert.notNull(brick.Id, "Save the brick before adding it to a model so that it has a valid ID");

		val brickId = brick.Id;

		content.add(new BrickContentItem(brickId, amount));
	}

	public int totalBrickCount() {

		return content.stream().mapToInt(br -> br.amount).sum();
	}

	public int distinctBrickCount() {
		return content.size();
	}

	public void add(Manual manual) {
		manuals.add(manual);
	}
}
