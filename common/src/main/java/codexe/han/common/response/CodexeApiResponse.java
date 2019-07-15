package codexe.han.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CodexeApiResponse {


    public static <T> CodexeApiResponse.ApiResponseBuilder<T> builder() {
        return new CodexeApiResponse.ApiResponseBuilder<>();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ErrorMessage{
        private Integer errorType;
        private String title;
        private String description;
        private String contactUs;
    }

    public static class ApiResponseBuilder<T>{

        /**
         * default 200
         */
        private HttpStatus statusCode = HttpStatus.OK;

        private int ret = ApiStatus.OPERATOR_SUCCESS;

        private T data;

        private String msg = "success";
        private ErrorMessage errorMessage;

        public CodexeApiResponse.ApiResponseBuilder<T> ret(int ret){
            this.ret = ret;
            return this;
        }

        public CodexeApiResponse.ApiResponseBuilder<T> data(T data){
            this.data = data;
            return this;
        }

        public CodexeApiResponse.ApiResponseBuilder<T> msg(String msg){
            this.msg = msg;
            return this;
        }

        public CodexeApiResponse.ApiResponseBuilder<T> errorMessage(ErrorMessage errorMessage){
            this.errorMessage = errorMessage;
            return this;
        }

        public CodexeApiResponse.ApiResponseBuilder<T> ok(){
            statusCode = HttpStatus.OK;
            return this;
        }

        public CodexeApiResponse.ApiResponseBuilder<T> created(){
            statusCode = HttpStatus.CREATED;
            return this;
        }

        public CodexeApiResponse.ApiResponseBuilder<T> unauthorized(){
            statusCode = HttpStatus.UNAUTHORIZED;
            setErrorMsg();
            return this;
        }

        public CodexeApiResponse.ApiResponseBuilder<T> badRequest(){
            statusCode = HttpStatus.BAD_REQUEST;
            setErrorMsg();
            return this;
        }

        public CodexeApiResponse.ApiResponseBuilder<T> notFound(){
            statusCode = HttpStatus.NOT_FOUND;
            setErrorMsg();
            return this;
        }

        public CodexeApiResponse.ApiResponseBuilder<T> conflict(){
            statusCode = HttpStatus.CONFLICT;
            setErrorMsg();
            return this;
        }

        public CodexeApiResponse.ApiResponseBuilder<T> internalServerError(){
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
            setErrorMsg();
            return this;
        }

        private void setErrorMsg(){
            this.ret = 0 - this.statusCode.value();
            this.msg = this.statusCode.getReasonPhrase();
        }

        public ResponseEntity<CodexeApi<T>> build(){
            return new ResponseEntity<>(new CodexeApi<>(ret,data,msg,errorMessage),statusCode);
        }
    }
}
