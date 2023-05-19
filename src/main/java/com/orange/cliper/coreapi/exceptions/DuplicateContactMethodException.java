package com.orange.cliper.coreapi.exceptions;

public class DuplicateContactMethodException extends IllegalStateException {

    public DuplicateContactMethodException(String contactMethodId) {
        super("Cannot duplicate party  for contactMethod identifier [" + contactMethodId + "]");
    }
}
