package com.artem.junit.extension;

import com.artem.junit.service.UserService;
import java.lang.reflect.Field;
import lombok.Getter;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

public class PostProcessingExtension implements TestInstancePostProcessor {

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext extensionContext) throws Exception {
        System.out.println("post processing extension");
        var fields = testInstance.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Getter.class)) {
                field.set(testInstance, new UserService(null));
            }
        }
    }
}
