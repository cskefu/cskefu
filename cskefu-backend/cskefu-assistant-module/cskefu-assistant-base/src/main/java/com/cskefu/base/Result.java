package com.cskefu.base;

import lombok.Getter;

@Getter
public class Result<T> {
    private final boolean success;
    private final int code;
    private final T data;
    private final String message;

    Result(boolean success, int code, T data, String message) {
        this.success = success;
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public static <T> ResultBuilder<T> builder() {
        return new ResultBuilder<>();
    }

    public static <T> Result<T> success() {
        return Result.<T>builder().code(0).success(true).build();
    }

    public static <T> Result<T> success(T data) {
        return Result.<T>builder().code(0).success(true).data(data).build();
    }

    public static <T> Result<T> success(int code, T data) {
        return Result.<T>builder().code(code).success(true).data(data).build();
    }

    public static <T> Result<T> error(int code, String i18nMessage) {
        return Result.<T>builder().code(code).success(false).message(i18nMessage).build();
    }

    @Override
    public String toString() {
        return "Result(success=" + this.success + ", code=" + this.code + ", data=" + this.data + ", message=" + this.message + ")";
    }

    public static class ResultBuilder<T> {
        private boolean success;
        private int code;
        private T data;
        private String message;

        ResultBuilder() {
        }

        public ResultBuilder<T> success(boolean success) {
            this.success = success;
            return this;
        }

        public ResultBuilder<T> code(int code) {
            this.code = code;
            return this;
        }

        public ResultBuilder<T> data(T data) {
            this.data = data;
            return this;
        }

        public ResultBuilder<T> message(String message) {
            this.message = message;
            return this;
        }

        public Result<T> build() {
            return new Result(this.success, this.code, this.data, this.message);
        }

        @Override
        public String toString() {
            return this.getClass().getName() + "(success=" + this.success + ", code=" + this.code + ", data=" + this.data + ", message=" + this.message + ")";
        }
    }
}
