package com.marketing.web.services.user;

import com.marketing.web.models.City;
import com.marketing.web.models.State;

import java.util.List;

public interface StateService {

    List<State> findAll();

    List<State> findAllByUuids(List<String> uuids);

    List<State> findAllByCity(City city);

    State findById(Long id);

    State findByUuid(String uuid);

    State findByUuidAndCity(String uuid, City city);

    State create(State state);

    State update(String uuid, State updatedState);

    void delete(State state);
}
