package com.marketing.web.repositories;

import com.marketing.web.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity,UUID>, JpaSpecificationExecutor<Activity> {


}
