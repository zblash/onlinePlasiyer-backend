package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.user.ReadableCity;
import com.marketing.web.dtos.user.ReadableState;
import com.marketing.web.models.City;
import com.marketing.web.models.State;

public final class CityMapper {

    public static ReadableCity cityToReadableCity(City city) {
        if (city == null) {
            return null;
        } else {
            ReadableCity readableCity = new ReadableCity();
            readableCity.setId(city.getUuid().toString());
            readableCity.setCode(city.getCode());
            readableCity.setTitle(city.getTitle());
            return readableCity;
        }
    }

    public static ReadableState stateToReadableState(State state){
        if (state == null) {
            return null;
        } else {
            ReadableState readableState = new ReadableState();
            readableState.setId(state.getUuid().toString());
            readableState.setCode(state.getCode());
            readableState.setTitle(state.getTitle());
            readableState.setCityTitle(state.getCity().getTitle());
            return readableState;
        }
    }
}
