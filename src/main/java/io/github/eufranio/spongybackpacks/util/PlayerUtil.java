package io.github.eufranio.spongybackpacks.util;

import org.spongepowered.api.service.permission.Subject;

/**
 * Created by Frani on 13/02/2018.
 */
public class PlayerUtil {

    public static int getIntOption(Subject subject, String option) {
        String opt = "spongybackpacks." + option;
        if (subject.getOption(opt).isPresent()) {
            try {
                return Integer.parseInt(subject.getOption(opt).get());
            } catch (NumberFormatException e) {}
        }
        return -1;
    }

}
