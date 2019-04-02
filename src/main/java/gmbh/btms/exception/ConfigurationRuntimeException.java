/*
 *  Copyright 2019 [https://btms.gmbh]
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package gmbh.btms.exception;

import org.apache.commons.lang3.exception.ContextedRuntimeException;

/**
 * <p>Signals all errors in conjunction with a netlink configuration.<br/>
 * Common error sources are:
 * <ul>
 * <li>Syntax error in XML</li>
 * <li>Incorrect URL syntax</li>
 * </ul>
 *
 * @author Oliver Dornauf
 * @since 1.0.0
 */

public class ConfigurationRuntimeException extends ContextedRuntimeException {

	public ConfigurationRuntimeException(String message) {
		super(message);
	}

	public ConfigurationRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
}
