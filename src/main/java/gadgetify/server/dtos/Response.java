package gadgetify.server.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {
    @Builder.Default
    private Boolean success = true;
    @Builder.Default
    private Object error = null;
    private T result;
}
