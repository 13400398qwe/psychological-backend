package com.example.scupsychological.common.exception;

import com.example.scupsychological.common.constant.MessageConstant;

public class UserNameExitsException extends BaseException{
    public UserNameExitsException() {
        super(MessageConstant.USER_NAME_EXITS);
    }
}
