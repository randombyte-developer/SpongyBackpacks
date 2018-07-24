package io.github.eufranio.spongybackpacks.util;

import org.spongepowered.api.service.permission.Subject;

import java.util.Optional;

/**
 * Created by Frani on 13/02/2018.
 */
public class PlayerUtil {

    public static int getIntOption(Subject subject, String option) {
        String opt = "spongybackpacks." + option;
        Optional<String> value = subject.getOption(opt);
        if (value.isPresent()) {
            try {
                return Integer.parseInt(value.get());
            } catch (NumberFormatException e) {}
        }
        return -1;
    }

}
