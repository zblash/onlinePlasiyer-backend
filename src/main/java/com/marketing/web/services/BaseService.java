package com.marketing.web.services;

import java.util.List;

public interface BaseService<T> {

    List<T> findAll();

    T findById(Long id);

    T create(T t);

    T update(T t);

    void delete(T t);
}
