package be.lambdaware.mappers;

import be.lambdaware.entities.ClassTopicsEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by jensv on 08-Apr-16.
 */
public class ClassTopicsMapper implements RowMapper<ClassTopicsEntity> {
    @Override
    public ClassTopicsEntity mapRow(ResultSet resultSet, int i) throws SQLException {
        ClassTopicsEntity entity = new ClassTopicsEntity();
        entity.setCourseTopicId(resultSet.getInt("course_topic_id"));
        entity.setClassId(resultSet.getInt("class_id"));
        return entity;
    }
}