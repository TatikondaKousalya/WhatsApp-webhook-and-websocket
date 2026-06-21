package com.chatapp.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String bio;
}