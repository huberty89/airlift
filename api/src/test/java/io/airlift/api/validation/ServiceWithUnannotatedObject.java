package io.airlift.api.validation;

import io.airlift.api.ApiGet;
import io.airlift.api.ApiParameter;
import io.airlift.api.ApiService;
import io.airlift.api.ServiceType;

@SuppressWarnings("unused")
@ApiService(name = "unannotatedObjectService", type = ServiceType.class, description = "A service with unannotated object fields")
public class ServiceWithUnannotatedObject
{
    @ApiGet(description = "get the thing with object")
    public ResourceWithUnannotatedObject getThing(@ApiParameter ThingId thingId)
    {
        return null;
    }
}
