package io.airlift.api.validation;

import io.airlift.api.ApiDescription;
import io.airlift.api.ApiPossibleTypes;
import io.airlift.api.ApiResource;

import java.util.List;

@ApiResource(name = "badPossibleTypesNonObject", description = "@ApiPossibleTypes used on non-Object type")
public record BadPossibleTypesNonObject(
        @ApiDescription("nope") List<@ApiPossibleTypes(int.class) String> values) {}
