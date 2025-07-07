package Oops.backend.domain.user.domain;

import Oops.backend.domain.common.BaseEntity;
import jakarta.persistence.Column;

public class User extends BaseEntity {
    @Column(length = 100, nullable = false)
    private String username;

    @Column(length = 100, nullable = false)
    private String email;

    @Column(length = 100, nullable = false)
    private String password;
}
