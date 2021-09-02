package com.github.mehrdadfalahati.entity.meta;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BeanFieldInfo {
    private Field field;
    private Method getter;
    private Method setter;
}
