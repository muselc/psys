package org.ye.psys.core.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;


public class SortValidator implements ConstraintValidator<Sort, String> {
    private List<String> valueList;

    @Override
    public void initialize(Sort constraintAnnotation) {
        valueList = new ArrayList<>();
        for (String val : constraintAnnotation.accepts()) {
            valueList.add(val.toUpperCase());
        }
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (valueList.contains(s.toUpperCase())) {
            return true;
        }
        return false;
    }
}
