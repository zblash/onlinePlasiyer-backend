package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.user.ReadableCity;
import com.marketing.web.dtos.user.ReadableState;
import com.marketing.web.models.City;
import com.marketing.web.models.State;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface CityMapper {

    CityMapper INSTANCE = Mappers.getMapper( CityMapper.class );

    default ReadableCity cityToReadableCity(City city){
        ReadableCity readableCity = new ReadableCity();
        readableCity.setId(city.getUuid().toString());
        readableCity.setCode(city.getCode());
        readableCity.setTitle(city.getTitle());
        return readableCity;
    }

    default ReadableState stateToReadableState(State state){
        ReadableState readableState = new ReadableState();
        readableState.setId(state.getUuid().toString());
        readableState.setCode(state.getCode());
        readableState.setTitle(state.getTitle());
        readableState.setCityTitle(state.getCity().getTitle());
        return readableState;
    }
}
