package codexe.han.common.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CodexeApi<T> {

    private int ret;

    private T data;

    private String msg;

    @JsonProperty("error_message")
    private CodexeApiResponse.ErrorMessage errorMessage;

}