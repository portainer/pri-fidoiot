// Copyright 2022 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.fidoalliance.fdo.protocol.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import org.fidoalliance.fdo.protocol.message.NullValue;

public class NullValueSerializer extends StdSerializer<NullValue> {

  public NullValueSerializer() {
    this(null);
  }

  public NullValueSerializer(Class<NullValue> t) {
    super(t);
  }

  @Override
  public void serialize(NullValue value, JsonGenerator gen, SerializerProvider provider)
      throws IOException {
    gen.writeNull();
  }

}
