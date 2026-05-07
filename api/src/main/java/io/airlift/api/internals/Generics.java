package io.airlift.api.internals;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeResolver;
import com.google.common.reflect.TypeToken;
import io.airlift.api.ApiPossibleTypes;
import io.airlift.api.ApiStringId;
import io.airlift.api.validation.ValidatorException;

import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

public interface Generics
{
    TypeResolver typeResolver = new TypeResolver();

    static Type extractGenericParameter(Type type, int index)
    {
        if (type instanceof ParameterizedType parameterizedType) {
            if (parameterizedType.getRawType().equals(ApiStringId.class) && (index == 1)) {
                return String.class;
            }

            if (parameterizedType.getActualTypeArguments().length <= index) {
                throw new ValidatorException("%s does not have expected (%d) number of parameters".formatted(type, index + 1));
            }
            return typeResolver.resolveType(parameterizedType.getActualTypeArguments()[index]);
        }
        throw new ValidatorException("Expected %s to be parameterized type".formatted(type));
    }

    static Optional<AnnotatedType> extractAnnotatedGenericParameter(Optional<AnnotatedType> annotatedType, int index)
    {
        return annotatedType
                .filter(AnnotatedParameterizedType.class::isInstance)
                .map(AnnotatedParameterizedType.class::cast)
                .map(AnnotatedParameterizedType::getAnnotatedActualTypeArguments)
                .filter(args -> args.length > index)
                .map(args -> args[index]);
    }

    static Optional<ApiPossibleTypes> extractPossibleTypes(Optional<AnnotatedType> annotatedType)
    {
        return annotatedType.map(at -> at.getAnnotation(ApiPossibleTypes.class));
    }

    static List<Class<?>> possibleTypesAsList(Optional<ApiPossibleTypes> possibleTypes)
    {
        return possibleTypes.map(at -> ImmutableList.copyOf(at.value())).orElse(ImmutableList.of());
    }

    static void validateMap(Type type, List<Class<?>> valuePossibleTypes)
    {
        TypeToken<?> typeToken = TypeToken.of(typeResolver.resolveType(type));

        Type keyType = extractGenericParameter(typeToken.getType(), 0);
        Type valueType = extractGenericParameter(typeToken.getType(), 1);
        if (!keyType.equals(String.class)) {
            throw new ValidatorException("Maps in resources must have a String key. %s does not".formatted(typeToken.getType()));
        }
        if (valueType.equals(String.class)) {
            return;
        }
        if (valueType.equals(Object.class) && !valuePossibleTypes.isEmpty()) {
            return;
        }
        throw new ValidatorException("Maps in resources must be Map<String, String> or Map<String, @ApiPossibleTypes(...) Object>. %s is not".formatted(typeToken.getType()));
    }
}
