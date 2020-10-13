package au.org.aodn.nrmn.restapi.model.api;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class UpdatedResult<T,E> {
    public Optional<T> updatedEntity;
    public List<E> errors;
}
