package io.airlift.api.validation;

import io.airlift.api.ApiDescription;
import io.airlift.api.ApiPossibleTypes;
import io.airlift.api.ApiResource;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApiResource(name = "possibleTypes", description = "resource exercising @ApiPossibleTypes")
public record PossibleTypesResource(
        @ApiDescription("a list of values that may be int, long, or string")
        List<@ApiPossibleTypes({int.class, long.class, String.class}) Object> values,
        @ApiDescription("attributes whose values may be string or boolean")
        Map<String, @ApiPossibleTypes({String.class, boolean.class}) Object> attributes,
        @ApiDescription("an optional value that may be int or string")
        Optional<@ApiPossibleTypes({int.class, String.class}) Object> maybeValue,
        @ApiDescription("a single value that may be string or boolean")
        @ApiPossibleTypes({String.class, boolean.class}) Object single) {}
