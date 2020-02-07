package com.marketing.web.services.user;

import com.marketing.web.configs.constants.MessagesConstants;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.City;
import com.marketing.web.models.State;
import com.marketing.web.repositories.StateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StateServiceImpl implements StateService {

    @Autowired
    private StateRepository stateRepository;

    @Override
    public List<State> findAll() {
        return stateRepository.findAll();
    }

    @Override
    public List<State> findAllByUuids(List<String> uuids) {
        return stateRepository.findAllByUuidIn(uuids.stream().map(UUID::fromString).collect(Collectors.toList()));
    }

    @Override
    public List<State> findAllByCity(City city) {
        return stateRepository.findAllByCity(city);
    }

    @Override
    public State findById(Long id) {
        return stateRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"state",id.toString()));
    }

    @Override
    public State findByUuid(String uuid) {
        return stateRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"state",uuid));
    }

    @Override
    public State findByUuidAndCity(String uuid, City city) {
        return stateRepository.findByUuidAndCity(UUID.fromString(uuid), city).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"state",uuid));
    }

    @Override
    public State create(State state) {
        return stateRepository.save(state);
    }

    @Override
    public State update(String uuid, State updatedState) {
        State state = findByUuid(uuid);
        state.setCity(updatedState.getCity());
        state.setCode(updatedState.getCode());
        state.setTitle(updatedState.getTitle());
        return stateRepository.save(state);
    }

    @Override
    public void delete(State state) {
        stateRepository.delete(state);
    }
}
