package io.github.md5sha256.pls;


/**
 * Represents the result of a request to OpenAi
 *
 * @param result The string result from the web request, or an error errorMessage
 * @param errorData Information about the error if there was one
 */
public record RequestResult<T>(T result, ErrorData errorData) {

    public RequestResult(ErrorData errorData) {
        this(null, errorData);
    }

    public RequestResult(T response) {
        this(response, null);
    }

    public T result() {
        if (error()) {
            throw new IllegalStateException("Tried to access result when there was an error");
        }
        return this.result;
    }

    public boolean error() {
        return errorData != null;
    }

}
