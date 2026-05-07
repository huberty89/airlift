package io.airlift.api.validation;

import io.airlift.api.ApiCreate;
import io.airlift.api.ApiGet;
import io.airlift.api.ApiService;
import io.airlift.api.ServiceType;

@ApiService(type = ServiceType.class, name = "possible types service", description = "exercises @ApiPossibleTypes")
public class ServiceWithPossibleTypes
{
    @ApiGet(description = "get the possible types resource")
    public PossibleTypesResource get()
    {
        return null;
    }

    @ApiCreate(description = "post the possible types resource", quotas = "POSSIBLE_TYPES")
    public void create(PossibleTypesResource resource) {}
}
