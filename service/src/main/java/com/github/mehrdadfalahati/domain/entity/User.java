package com.github.mehrdadfalahati.domain.entity;

import com.github.mehrdadfalahati.annotations.Column;
import com.github.mehrdadfalahati.annotations.Table;
import lombok.*;

@Table(name = "User")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id", doNotUseGetters = true)
public class User {
    @Column(name = "id", primaryKey = true)
    private Long id;
    @Column(name = "username")
    private String username;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
}
