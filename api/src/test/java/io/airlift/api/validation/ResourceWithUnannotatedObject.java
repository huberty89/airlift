package io.airlift.api.validation;

import io.airlift.api.ApiDescription;
import io.airlift.api.ApiResource;
import io.airlift.api.ApiResourceVersion;

import java.util.List;

@ApiResource(name = "resourceWithUnannotatedObject", description = "a resource with an unannotated list of objects")
public record ResourceWithUnannotatedObject(
        ApiResourceVersion syncToken,
        @ApiDescription("id") ThingId thingId,
        @ApiDescription("free-form list") List<Object> dataList) {}
