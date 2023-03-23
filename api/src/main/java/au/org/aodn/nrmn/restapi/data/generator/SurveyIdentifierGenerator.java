package au.org.aodn.nrmn.restapi.data.generator;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import au.org.aodn.nrmn.restapi.data.model.Survey;

import java.io.Serializable;
import java.sql.SQLException;

public class SurveyIdentifierGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        var sequence = "nrmn.survey_survey_id";
        if (object instanceof Survey) {
            var survey = (Survey)object;
            if (survey.getProgram().getProgramId() == 0)
                sequence = "nrmn.survey_survey_id_m0";
        } else {
            throw new HibernateException("Unsupported entity for this identifier generator");
        }

        var connection = session.connection();
        try {
            var preparedStatement = connection.prepareStatement("SELECT nextval(?)");
            preparedStatement.setString(1, sequence);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new HibernateException("Error generating identifier", e);
        }

        throw new HibernateException("Unable to generate identifier");
    }
}
