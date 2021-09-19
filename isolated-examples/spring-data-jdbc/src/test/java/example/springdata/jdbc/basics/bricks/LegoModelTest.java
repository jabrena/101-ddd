package example.springdata.jdbc.basics.bricks;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.test.context.junit4.SpringRunner;

import static java.util.Arrays.*;

/**
 * @author Jens Schauder
 */
@DataJdbcTest
public class LegoModelTest {

	@Autowired
	LegoModels models;

	@Autowired
	Bricks bricks;

	@Test
	public void modelReferencingManual() {

		LegoModel womenOfNasa = new LegoModel("Women of NASA");
		womenOfNasa.add(new Manual("English", "blah, blah, ... assembly ... blah"));

		models.save(womenOfNasa);
	}

	@Test
	public void modelReferencingBricks() {

		Brick thin_2x2 = new Brick("2x2 - thin");
		Brick thin_2x4 = new Brick("2x4 - thin");
		Brick normal_2x2 = new Brick("2x2 - normal");

		bricks.saveAll(asList(thin_2x2, thin_2x4, normal_2x2));

		LegoModel womenOfNasa = new LegoModel("Women of NASA");
		womenOfNasa.add(thin_2x2, 6);
		womenOfNasa.add(thin_2x4, 3);
		womenOfNasa.add(normal_2x2, 4);

		models.save(womenOfNasa);
	}

	@Test
	public void modelManualAndBricks() {

		Brick thin_2x2 = new Brick("2x2 - thin");
		Brick thin_2x4 = new Brick("2x4 - thin");
		Brick normal_2x2 = new Brick("2x2 - normal");

		bricks.saveAll(asList(thin_2x2, thin_2x4, normal_2x2));

		LegoModel womenOfNasa = new LegoModel("Women of NASA");
		womenOfNasa.add(new Manual("English", "blah, blah, ... assembly ... blah"));

		womenOfNasa.add(thin_2x2, 6);
		womenOfNasa.add(thin_2x4, 3);
		womenOfNasa.add(normal_2x2, 4);

		models.save(womenOfNasa);
	}
}
