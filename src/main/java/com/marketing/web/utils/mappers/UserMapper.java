package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.RegisterDTO;
import com.marketing.web.models.Address;
import com.marketing.web.models.User;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper( UserMapper.class );

    @InheritInverseConfiguration
    User registerDTOToUser(RegisterDTO registerDTO);

    @InheritInverseConfiguration
    Address registerDTOToAddress(RegisterDTO registerDTO);
}
