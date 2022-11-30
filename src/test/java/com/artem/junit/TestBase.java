package com.artem.junit;

import com.artem.junit.extension.GlobalExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({
    GlobalExtension.class
})
public abstract class TestBase {
}
