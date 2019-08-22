package com.marketing.web.repositories;

import com.marketing.web.models.City;
import com.marketing.web.models.State;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface StateRepository extends JpaRepository<State,Long> {

    List<State> findAllByTitleIn(List<String> titles);
    List<State> findAllByCity(City city);
}
