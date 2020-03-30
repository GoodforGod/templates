import com.openfaas.function.Handler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class HandlerTest {

    @Test
    void handlerIsNotNull() {
        final Handler handler = new Handler();
        assertNotNull(handler, "Expected handler not to be null");
    }
}
