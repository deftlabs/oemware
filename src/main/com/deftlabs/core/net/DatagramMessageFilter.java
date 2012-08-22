/**
 * (C) Copyright 2007, Deft Labs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.deftlabs.core.net;

/**
 * The message filter interface. Allows for pre-processing logic to be
 * inserted.
 */
public interface DatagramMessageFilter {

    /**
     * Called to execute the filter.
     * @param pDatagramMessage The message.
     * @return True if the operation should continue.
     */
    public boolean execute(final DatagramMessage pDatagramMessage);
}

