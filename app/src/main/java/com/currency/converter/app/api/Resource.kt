/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.currency.converter.app.api

import com.currency.converter.app.api.Status.*

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
</T> */
data class Resource<out T>(val status: com.currency.converter.app.api.Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T?): com.currency.converter.app.api.Resource<T> {
            return com.currency.converter.app.api.Resource(SUCCESS, data, null)
        }

        fun <T> error(msg: String, data: T?): com.currency.converter.app.api.Resource<T> {
            return com.currency.converter.app.api.Resource(ERROR, data, msg)
        }

        fun <T> loading(data: T?): com.currency.converter.app.api.Resource<T> {
            return com.currency.converter.app.api.Resource(LOADING, data, null)
        }
    }
}
