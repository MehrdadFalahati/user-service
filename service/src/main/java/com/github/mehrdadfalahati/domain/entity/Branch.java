package com.github.mehrdadfalahati.domain.entity;

import com.github.mehrdadfalahati.annotations.Column;
import com.github.mehrdadfalahati.annotations.Table;
import lombok.*;

@Table(name = "Branch")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id", doNotUseGetters = true)
public class Branch {
    @Column(name = "id", primaryKey = true)
    private Long id;
    @Column(name = "address")
    private String address;
    @Column(name = "status_")
    private Boolean status;
    @Column(name = "branch_name")
    private String branchName;
}
