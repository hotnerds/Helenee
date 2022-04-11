package com.hotnerds.utils;

import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;

public interface DocumentUtils {

    static OperationRequestPreprocessor getDocumentRequestPreprocess() {
        return preprocessRequest(
                prettyPrint()
        );
    }

    static OperationResponsePreprocessor getDocumentResponsePreprocess() {
        return preprocessResponse(
                prettyPrint()
        );
    }
}
