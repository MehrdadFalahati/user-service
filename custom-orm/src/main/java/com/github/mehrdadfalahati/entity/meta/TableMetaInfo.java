package com.github.mehrdadfalahati.entity.meta;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TableMetaInfo {
    private String tableName;
    private Pair<String, BeanFieldInfo> idRow;
    private Map<String, BeanFieldInfo> baseRows;
}
