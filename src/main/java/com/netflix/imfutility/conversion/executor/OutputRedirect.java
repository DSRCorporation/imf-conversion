package com.netflix.imfutility.conversion.executor;

/**
 * Defines where external process's stdout must be redirected by default (if no specific stdout location is specified).
 */
public enum OutputRedirect {

    ERR_LOG, INHERIT, PIPE, FILE

}
