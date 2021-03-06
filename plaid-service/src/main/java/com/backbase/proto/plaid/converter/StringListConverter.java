package com.backbase.proto.plaid.converter;

import java.util.Collections;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.List;

/**
 * This class converts a list to a string and vice versa to be stored in a database.
 */
@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {
    /**
     * Takes a list of strings and joins then with a ',' separator so it may be stored as a column in a database.
     *
     * @param list the list to be converted into a string
     * @return joined string containing the contents of the list parsed in
     */
    @Override
    public String convertToDatabaseColumn(List<String> list) {
        if(list == null) {
            return null;
        } else {
            return String.join(",", list);
        }
    }

    /**
     * Takes a string separated by ',' and splits it into a list to be stored as attributes.
     *
     * @param joined a string to be turned to a list
     * @return the list of strings to be stored as attributes
     */
    @Override
    public List<String> convertToEntityAttribute(String joined) {
        if(StringUtils.isEmpty(joined)) {
            return Collections.emptyList();
        } else {
            return Arrays.asList(joined.split(","));
        }
    }

}
