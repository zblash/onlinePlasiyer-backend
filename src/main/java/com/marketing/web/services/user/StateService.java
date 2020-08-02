package com.marketing.web.services.user;

import com.marketing.web.models.City;
import com.marketing.web.models.State;

import java.util.List;
import java.util.Set;

public interface StateService {

    List<State> findAll();

    List<State> findAllByIds(List<String> ids);

    List<State> findAllByCity(City city);

    State findById(String id);

    State findByUuidAndCity(String id, City city);

    State create(State state);

    State update(String uuid, State updatedState);

    void delete(State state);
}
