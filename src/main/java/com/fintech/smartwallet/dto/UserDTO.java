package com.fintech.smartwallet.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private String phone;
    private String avatar;
}
