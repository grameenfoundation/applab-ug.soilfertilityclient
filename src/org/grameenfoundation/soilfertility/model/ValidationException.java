package org.grameenfoundation.soilfertility.model;

/**
 * Copyright (c) 2013 AppLab, Grameen Foundation
 * Created by: David
 *
 * custom exception thrown when validation fails
 */
public class ValidationException extends Exception {
    public ValidationException() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public ValidationException(String detailMessage) {
        super(detailMessage);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
