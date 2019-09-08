package com.marketing.web.services.user;

import com.marketing.web.models.City;
import com.marketing.web.models.State;

import java.util.List;

public interface IStateService {

    List<State> findAll();

    List<State> findByCity(City city);

    State findById(Long id);

    State findByUuid(String uuid);

    State create(State state);

    State update(String uuid, State updatedState);

    void delete(State state);
}
