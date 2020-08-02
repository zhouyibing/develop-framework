package com.fido.framework.core.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author: yibingzhou
 */
public class EnumValueValidator implements ConstraintValidator<EnumValue, Object> {

    private String[] values;

    @Override
    public void initialize(EnumValue enumValue) {
        values = enumValue.value();
        if(null == values || values.length ==0) {
            throw new IllegalArgumentException(" values can not be null");
        }
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        if ( value == null ) {
            return true;
        }
        String valueStr = value.toString();
        for (String s:values) {
            if(s.equals(valueStr)){
                return true;
            }
        }
        return false;
    }
}
