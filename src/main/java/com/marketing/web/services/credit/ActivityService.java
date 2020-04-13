package com.marketing.web.services.credit;

import com.marketing.web.models.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ActivityService {

    Page<Activity> findAll(int pageNumber, String sortBy, String sortType);

    Page<Activity> findAllBySpecification(Specification<Activity> specification, Integer pageNumber, String sortBy, String sortType);

    Activity findById(Long id);

    Activity findByUUID(String uuid);

    Activity create(Activity activity);

    Activity update(String uuid, Activity updatedActivity);

    void delete(Activity activity);

    void saveAll(List<Activity> activities);

}