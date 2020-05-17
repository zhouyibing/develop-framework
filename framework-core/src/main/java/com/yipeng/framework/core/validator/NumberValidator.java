package com.yipeng.framework.core.validator;

import com.yipeng.framework.core.utils.Precondition;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author: yibingzhou
 */
public class NumberValidator implements ConstraintValidator<Number, CharSequence> {

    private int minLen;
    private int maxLen;

    @Override
    public void initialize(Number parameters) {
        minLen = parameters.minLen();
        maxLen = parameters.maxLen();
        validateParameters();
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext constraintValidatorContext) {
        if ( value == null ) {
            return true;
        }

        int length = value.length();
        return (length >= minLen && length <= maxLen) || Precondition.checkNumber(value);
    }

    private void validateParameters() {
        if ( minLen < 0 ) {
            throw new IllegalArgumentException("The minLen parameter cannot be negative.");
        }
        if ( maxLen < 0 ) {
            throw new IllegalArgumentException("The maxLen parameter cannot be negative.");
        }
        if ( maxLen < minLen ) {
            throw new IllegalArgumentException("The length cannot be negative.");
        }
    }
}
