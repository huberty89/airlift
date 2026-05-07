package io.airlift.api.validation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import io.airlift.api.ApiServiceTrait;
import io.airlift.api.ServiceType;
import io.airlift.api.builders.ApiBuilder;
import io.airlift.api.model.ModelApi;
import io.airlift.api.model.ModelResource;
import io.airlift.api.model.ModelResourceType;
import io.airlift.api.model.ModelServiceMetadata;
import io.airlift.api.model.ModelServiceType;
import io.airlift.api.model.ModelServices;
import io.airlift.api.openapi.OpenApiMetadata;
import io.airlift.api.openapi.OpenApiProvider;
import io.airlift.api.openapi.models.OpenAPI;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static io.airlift.api.builders.ResourceBuilder.resourceBuilder;
import static io.airlift.api.validation.ResourceValidator.validateResource;
import static io.airlift.json.JsonCodec.jsonCodec;
import static org.assertj.core.api.Assertions.assertThat;

public class TestPossibleTypes
{
    private static final ModelServiceMetadata METADATA = new ModelServiceMetadata("dummy", new ModelServiceType("dummy", 1, "dummy", "dummy", ImmutableSet.copyOf(ApiServiceTrait.values())), "dummy", ImmutableList.of());

    @Test
    public void testServiceBuildsAndValidates()
    {
        ModelServices services = ApiBuilder.apiBuilder().add(ServiceWithPossibleTypes.class).build().modelServices();
        assertThat(services.errors()).isEmpty();
    }

    @Test
    public void testListElementCarriesPossibleTypes()
    {
        ModelResource modelResource = resourceBuilder(PossibleTypesResource.class).build();
        ValidationContext context = new ValidationContext();
        validateResource(context, METADATA, modelResource);
        assertThat(context.errors()).isEmpty();

        ModelResource values = componentByName(modelResource, "values");
        assertThat(values.resourceType()).isEqualTo(ModelResourceType.LIST);
        assertThat(values.type()).isEqualTo(Object.class);
        assertThat(values.possibleTypes()).containsExactly(int.class, long.class, String.class);
    }

    @Test
    public void testMapValueCarriesPossibleTypes()
    {
        ModelResource modelResource = resourceBuilder(PossibleTypesResource.class).build();
        ModelResource attributes = componentByName(modelResource, "attributes");
        assertThat(attributes.resourceType()).isEqualTo(ModelResourceType.MAP);
        assertThat(attributes.type()).isEqualTo(Object.class);
        assertThat(attributes.possibleTypes()).containsExactly(String.class, boolean.class);
    }

    @Test
    public void testOptionalValueCarriesPossibleTypes()
    {
        ModelResource modelResource = resourceBuilder(PossibleTypesResource.class).build();
        ModelResource maybeValue = componentByName(modelResource, "maybeValue");
        assertThat(maybeValue.modifiers()).contains(io.airlift.api.model.ModelResourceModifier.OPTIONAL);
        assertThat(maybeValue.type()).isEqualTo(Object.class);
        assertThat(maybeValue.possibleTypes()).containsExactly(int.class, String.class);
    }

    @Test
    public void testDirectFieldCarriesPossibleTypes()
    {
        ModelResource modelResource = resourceBuilder(PossibleTypesResource.class).build();
        ModelResource single = componentByName(modelResource, "single");
        assertThat(single.resourceType()).isEqualTo(ModelResourceType.BASIC);
        assertThat(single.type()).isEqualTo(Object.class);
        assertThat(single.possibleTypes()).containsExactly(String.class, boolean.class);
    }

    @Test
    public void testRejectsAnnotationOnNonObjectType()
    {
        ValidationContext context = new ValidationContext();
        context.inContext("", _ -> resourceBuilder(BadPossibleTypesNonObject.class).build());
        assertThat(context.errors()).anyMatch(s -> s.contains("@ApiPossibleTypes is only allowed on Object types"));
    }

    @Test
    public void testRejectsNonBasicListedType()
    {
        ValidationContext context = new ValidationContext();
        context.inContext("", _ -> resourceBuilder(BadPossibleTypesNonBasic.class).build());
        assertThat(context.errors()).anyMatch(s -> s.contains("@ApiPossibleTypes only supports basic types"));
    }

    @Test
    public void testOpenApiSchemaContainsOneOf()
    {
        ModelApi modelApi = ApiBuilder.apiBuilder().add(ServiceWithPossibleTypes.class).build();
        OpenApiProvider provider = OpenApiProvider.create(modelApi.modelServices(), new OpenApiMetadata(Optional.empty(), ImmutableList.of()));

        ModelServiceType serviceType = ModelServiceType.map(new ServiceType());
        OpenAPI openAPI = provider.build(serviceType, _ -> true);
        String json = jsonCodec(OpenAPI.class).toJson(openAPI);

        // The values list element schema should be a oneOf of integer (with int64 format for long) and string
        assertThat(json).contains("\"PossibleTypes\"");
        assertThat(json).contains("\"oneOf\"");
        // values: int / long / string
        assertThat(json).containsPattern("\"values\"\\s*:\\s*\\{[^}]*\"items\"\\s*:\\s*\\{[^{}]*\"oneOf\"");
        // attributes: Map<String, oneOf<String, boolean>>
        assertThat(json).containsPattern("\"attributes\"\\s*:\\s*\\{[^}]*\"additionalProperties\"\\s*:\\s*\\{[^{}]*\"oneOf\"");
    }

    private static ModelResource componentByName(ModelResource modelResource, String name)
    {
        return modelResource.components().stream()
                .filter(c -> c.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("component not found: " + name));
    }
}
