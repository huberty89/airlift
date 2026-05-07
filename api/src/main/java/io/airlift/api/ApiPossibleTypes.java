package io.airlift.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates an {@link Object}-typed position to declare which concrete value types it may hold.
 * The generated OpenAPI schema is a {@code oneOf} of the listed types' schemas.
 *
 * <p>Only basic types are allowed as possible types: {@code boolean}, {@code int}, {@code long},
 * {@code double}, {@code String}, {@code java.time.Instant}, {@code java.time.LocalDate},
 * {@code java.math.BigDecimal}, {@code java.util.UUID}, and {@code Enum} subtypes.
 *
 * <p>Allowed positions: {@code List}/{@code Collection}/{@code Set} element type, {@code Map}
 * value type (key must remain {@code String}), {@code Optional} value type, or directly on a
 * record component whose declared type is {@code Object}.
 *
 * <p>Example:
 * <pre>{@code
 * @ApiResource(name = "thing", description = "...")
 * public record Thing(
 *         List<@ApiPossibleTypes({int.class, long.class, String.class}) Object> values,
 *         Map<String, @ApiPossibleTypes({String.class, boolean.class}) Object> attributes) {}
 * }</pre>
 */
@Target(ElementType.TYPE_USE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiPossibleTypes
{
    Class<?>[] value();
}
