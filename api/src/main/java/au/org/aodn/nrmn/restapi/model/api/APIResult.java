package au.org.aodn.nrmn.restapi.model.api;

public class APIResult<T> {
    private Integer code;
    private T Data;


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getData() {
        return Data;
    }

    public void setData(T data) {
        Data = data;
    }
}
