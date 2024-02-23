package com.backend.reservation.config;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilder objectMapperBuilder(){
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();

        builder.featuresToEnable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
        builder.simpleDateFormat("yyyy-MM-dd");
        builder.serializers(new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm")));
        builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        return builder;

    }
}
