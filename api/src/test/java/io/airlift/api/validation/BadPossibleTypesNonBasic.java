package io.airlift.api.validation;

import io.airlift.api.ApiDescription;
import io.airlift.api.ApiPossibleTypes;
import io.airlift.api.ApiResource;

import java.util.List;

@ApiResource(name = "badPossibleTypesNonBasic", description = "@ApiPossibleTypes lists a non-basic class")
public record BadPossibleTypesNonBasic(
        @ApiDescription("nope") List<@ApiPossibleTypes(Thing.class) Object> values) {}
