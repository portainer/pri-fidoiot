// Copyright 2022 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.fidoalliance.fdo.protocol.dispatch;

import java.io.IOException;
import org.apache.commons.lang3.function.FailableSupplier;
import org.fidoalliance.fdo.protocol.KeyResolver;

public interface CwtKeySupplier extends FailableSupplier<KeyResolver, IOException> {


}
