package example.springdata.jdbc.basics.bricks;

import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Jens Schauder
 */
@Table("Content")
@EqualsAndHashCode(of = "brickId")
public class BrickContentItem {

	final Long brickId;
	final Integer amount;

	public BrickContentItem(Long brickId, Integer amount) {
		this.brickId = brickId;
		this.amount = amount;
	}
}
