package org.gobiiproject.gobiimodel.utils.customserializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Helper class for Jackson Json serializer for Date objects.
 * Serializes Date String as following yyyy-MM-dd'T'hh:mm:ss.
 * Sets time zone as UTC.
 */
@SuppressWarnings("serial")
public class UtcDateSerializer extends StdSerializer<Date> {

    private SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd'T'hh:mm:ss");

    public UtcDateSerializer() {
        this(null);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public UtcDateSerializer(Class t) {
        super(t);
    }

    @Override
    public void serialize(Date dateValue, JsonGenerator generator,
                          SerializerProvider provider) throws IOException, JsonProcessingException {
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        generator.writeString(formatter.format(dateValue));
    }

}
