package com.example.scupsychological.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
@Data
@Schema
public class LoginDTO implements Serializable{
    private String username;
    private String password;
}
