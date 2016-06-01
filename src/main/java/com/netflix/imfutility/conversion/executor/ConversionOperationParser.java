package com.netflix.imfutility.conversion.executor;

import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameterResolver;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Conversion Operation parser. Splits arguments and resolves all parameters.
 */
public class ConversionOperationParser {

    private final TemplateParameterResolver parameterResolver;

    public ConversionOperationParser(TemplateParameterResolver parameterResolver) {
        this.parameterResolver = parameterResolver;
    }

    public List<String> parseOperation(String conversionOperation, ContextInfo contextInfo) {
        // 1. resolve parameters BEFORE splitting since resolved values may contain multiple sub-parameters!
        conversionOperation = parameterResolver.resolveTemplateParameter(conversionOperation, contextInfo);

        // 2. split params
        // do not add quotes! it's up to conversion.xml to insert correct quotes.
        return splitParameters(conversionOperation);
    }


    public List<String> parseWithQuotes(String conversionOperation, ContextInfo contextInfo) {
        // 1. resolve parameters BEFORE splitting since resolved values may contain multiple sub-parameters!
        conversionOperation = parameterResolver.resolveTemplateParameter(conversionOperation, contextInfo);

        // 2. split params and add quotes if needed
        return splitParameters(conversionOperation).stream()
                .map(this::addQuotes)
                .collect(Collectors.toList());
    }


    private List<String> splitParameters(String conversionOperation) {
        String trimmedConversionOperation = conversionOperation.trim();

        ParseState state = ParseState.NORMAL;

        StringTokenizer tokenizer = new StringTokenizer(trimmedConversionOperation, "\"\' \r\n", true);
        List<String> result = new ArrayList<>();
        StringBuilder currentArg = new StringBuilder();
        boolean lastTokenQuoted = false;

        while (tokenizer.hasMoreTokens()) {
            String nextToken = tokenizer.nextToken();
            switch (state) {
                case IN_QUOTE:
                    currentArg.append(nextToken);
                    if ("\'".equals(nextToken)) {
                        lastTokenQuoted = true;
                        state = ParseState.NORMAL;
                    }
                    break;
                case IN_DOUBLE_QUOTE:
                    currentArg.append(nextToken);
                    if ("\"".equals(nextToken)) {
                        lastTokenQuoted = true;
                        state = ParseState.NORMAL;
                    }
                    break;
                default:
                    if ("\'".equals(nextToken)) {
                        state = ParseState.IN_QUOTE;
                        currentArg.append(nextToken);
                    } else if ("\"".equals(nextToken)) {
                        state = ParseState.IN_DOUBLE_QUOTE;
                        currentArg.append(nextToken);
                    } else if (" ".equals(nextToken) || "\n".equals(nextToken) || "\r".equals(nextToken)) {
                        if (lastTokenQuoted || currentArg.length() != 0) {
                            result.add(currentArg.toString());
                            currentArg = new StringBuilder();
                        }
                    } else {
                        currentArg.append(nextToken);
                    }
                    lastTokenQuoted = false;
                    break;
            }
        }

        if (lastTokenQuoted || currentArg.length() != 0) {
            result.add(currentArg.toString());
        }

        if (state == ParseState.IN_QUOTE || state == ParseState.IN_DOUBLE_QUOTE) {
            throw new IllegalArgumentException(String.format("Can not parse Conversion Operation '%s'. Unbalanced quotes.", conversionOperation));
        }

        return result;
    }

    private String addQuotes(String param) {
        if (!param.contains(" ") && !param.contains("\n") && !param.contains("\r")) {
            return param;
        }
        if (!param.contains("=")) {
            return addQuotesIfNeeded(param);
        }
        String subParam = StringUtils.substringAfter(param, "=");
        String quotedSubParam = addQuotesIfNeeded(subParam);
        return StringUtils.substringBefore(param, "=") + "=" + quotedSubParam;
    }

    private String addQuotesIfNeeded(String param) {
        String trimmedParam = param.trim();
        Pattern p = Pattern.compile("(\".*\")|('.*')", Pattern.DOTALL);
        if (!p.matcher(trimmedParam).matches()) {
            if (trimmedParam.contains("\"")) {
                trimmedParam = String.format("'%s'", trimmedParam);
            } else {
                trimmedParam = String.format("\"%s\"", trimmedParam);
            }
        }
        return trimmedParam;
    }

    private enum ParseState {
        NORMAL, IN_QUOTE, IN_DOUBLE_QUOTE
    }
}
