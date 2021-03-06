package com.sinewang.kiimate.model.core.fui;

import org.springframework.stereotype.Component;
import one.kii.kiimate.model.core.fui.AnStructureValidator;

/**
 * Created by WangYanJiong on 26/03/2017.
 */

@Component
public class DefaultStructureValidator implements AnStructureValidator {


    @Override
    public boolean isValid(String structure) {
        if (structure == null) {
            return false;
        }
        try {
            String upperCase = structure.toUpperCase();
            Structure.valueOf(upperCase);
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

}
