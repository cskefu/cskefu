/*
 * Copyright (C) 2019 Chatopera Inc, <https://www.chatopera.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chatopera.cc.util;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class SerializeUtil {

    private static final Logger logger = LoggerFactory.getLogger(SerializeUtil.class);

    private SerializeUtil() {
    }

    public static String serialize(final Serializable object) {
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(object);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (final IOException e) {
            logger.warn("[serialize] error", e);
            return null;
        }
    }

    public static <T extends Serializable> String serialize(final List<T> object) {
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(object);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (final IOException e) {
            logger.warn("[serialize] List<T> error", e);
            return null;
        }
    }

    public static <TK, TV extends Serializable> String serialize(final Map<TK, TV> object) {
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(object);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (final IOException e) {
            logger.warn("[serialize] Map<TK, TV> error", e);
            return null;
        }
    }

    public static <T extends Serializable> T deserialize(final String objectAsString) {
        if (StringUtils.isBlank(objectAsString))
            return null;

        final byte[] data = Base64.getDecoder().decode(objectAsString);
        try (final ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return (T) ois.readObject();
        } catch (final IOException | ClassNotFoundException e) {
            logger.warn("[serialize] error", e);
            return null;
        }
    }
}
