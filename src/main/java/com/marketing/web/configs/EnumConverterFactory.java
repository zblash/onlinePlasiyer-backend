package com.marketing.web.configs;


import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

public class EnumConverterFactory implements ConverterFactory<String, Enum<?>> {
    @Override
    public <E extends Enum<?>> Converter<String, E> getConverter(
            Class<E> targetType) {
        return  new StringToEnumConverter<E>(targetType);
    }
    static class StringToEnumConverter<E extends Enum<?>>
            implements Converter<String, E> {
        Class<E> enumType;
        StringToEnumConverter(Class<E> enumType) {
            this.enumType = enumType;
        }
        @Override
        public E convert(String source) {
            source = source.trim();
            for (E constant : enumType.getEnumConstants()) {
                if (constant.name().equalsIgnoreCase(source)) {
                    return constant;
                }
            }
            throw new IllegalArgumentException(source);
        }
    }
}