package be.lambdaware.jdbc;

import be.lambdaware.dao.CourseTopicDAO;
import be.lambdaware.entities.CourseTopicEntity;
import be.lambdaware.mappers.CourseTopicMapper;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Created by jensv on 07-Apr-16.
 */
public class CourseTopicDAOImpl extends AbstractDAOImpl implements CourseTopicDAO {

    public int create(CourseTopicEntity entity) {

        String SQL = "INSERT INTO `course_topic`(`name`) VALUES (?)";

        //TODO process result our catch SQL Exception
        GeneratedKeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement statement = con.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, entity.getName());
                return statement;
            }
        }, holder);

        return 0;
    }


    public CourseTopicEntity get(Integer id) {
        String SQL = "SELECT * FROM `course_topic` WHERE `id` = ?";
        CourseTopicEntity entity = jdbcTemplate.queryForObject(SQL, new Object[]{id}, new CourseTopicMapper());
        //TODO catch SQL Exception

        return entity;
    }


    public List<CourseTopicEntity> getAll() {
        String SQL = "SELECT * FROM `course_topic`";
        List<CourseTopicEntity> entities = jdbcTemplate.query(SQL, new CourseTopicMapper());
        //TODO catch SQL Exception
        return entities;
    }


    public void delete(Integer id) {
        String SQL = "DELETE FROM `course_topic` WHERE `id` = ?";
        jdbcTemplate.update(SQL, id);
        //TODO catch SQL Exception
    }


    public void update(CourseTopicEntity entity) {
        String SQL = "UPDATE `users` SET `name` = ? where id = ?";
        jdbcTemplate.update(SQL, entity.getName(), entity.getId());
        //TODO catch SQL Exception
    }
}