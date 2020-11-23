package com.efs.cloud.trackingservice;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * @author jabez.huang
 */
public class Jsr303Validate {

    public final ServiceResult validate() {

        List<String> errorMessageList = new ArrayList<>();
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Jsr303Validate>> violationSet = validator.validate(this);
        if (!violationSet.isEmpty()) {
            for (ConstraintViolation<Jsr303Validate> violation : violationSet) {
                errorMessageList.add(violation.getMessage());
            }
        }
        if (!errorMessageList.isEmpty()) {
            return ServiceResult.builder().code(-1001).msg(String.join("；", errorMessageList)).build();
        }
        return null;
    }
}

