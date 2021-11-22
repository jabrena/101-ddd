package example.springdata.jdbc.basics.bricks;

import org.springframework.data.repository.CrudRepository;

/**
 * @author Jens Schauder
 */
public interface Bricks extends CrudRepository<Brick, Long> {

}
