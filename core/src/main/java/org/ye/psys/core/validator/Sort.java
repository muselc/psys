package org.ye.psys.core.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = SortValidator.class)
public @interface Sort {
    String message() default "排序字段不支持";

    String[] accepts() default {"create_time", "id"};

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
